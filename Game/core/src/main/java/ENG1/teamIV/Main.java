package ENG1.teamIV;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    FitViewport viewport;
    SpriteBatch spriteBatch;

    Texture backgroundTexture;
    Texture menuBgTexture;

    Entity playerEntity;
    Array<Entity> wallEntities;

    BitmapFont font;
    Timer timer;
    Music music;
    Sound dropSound;

    Array<Event> events;
    ObjectMap<String, Entity> eventEntities;    // Entities related to events should be added and removed as required

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void create(){
        viewport = new FitViewport(AppConstants.worldWidth, AppConstants.worldHeight);
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        backgroundTexture = new Texture(AppConstants.BACKGROUND_TEX);
        menuBgTexture = new Texture(AppConstants.MENU_BG_TEX);

        playerEntity = new Entity(AppConstants.PLAYER_TEX, 0.7f * AppConstants.cellSize, new Vector2());
        playerEntity.setSpeed(AppConstants.playerSpeedDefault);
        playerEntity.collidable = true;

        wallEntities = Utilities.loadMap(AppConstants.MAP_FP);
        eventEntities = new ObjectMap<>();
        events = new Array<>();

        timer = new Timer(AppConstants.TIMER_LIMIT_DEFAULT, AppConstants.TIMER_STEP_DEFAULT);
        music = Gdx.audio.newMusic(Gdx.files.internal(AppConstants.MUSIC_FP));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        dropSound = Gdx.audio.newSound(Gdx.files.internal(AppConstants.DROP_SOUND_FP));

        // Define events here
        
        // 1. Key Event
        Vector2 doorPos = new Vector2(380, 370);
        Vector2 keyPos = new Vector2(60, 260);
        
        // Create a door to block the path
        Entity door = new Entity(AppConstants.DOOR_TEX, AppConstants.cellSize, doorPos);
        door.collidable = true;
        eventEntities.put("door", door);

        // Event init
        Event getKey0 = new Event(new Array<>(), 1.1f * AppConstants.cellSize, doorPos){
            @Override
            void execute(){
                // Spawn a key
                eventEntities.put("key", new Entity(AppConstants.KEY_TEX, 0.8f * AppConstants.cellSize, keyPos));
                dropSound.play();
                System.out.println("Pick up the key to open the door!");
            }
        };
        events.add(getKey0);

        // Pick up the key
        Event getKey1 = new Event(new Array<>(new Event[]{getKey0}), 0.7f * AppConstants.cellSize, keyPos){
            @Override
            void execute(){
                // Despawn the key
                eventEntities.remove("key");
                dropSound.play();
            }
        };
        events.add(getKey1);

        // Open the door
        Event getKey2 = new Event(new Array<>(new Event[]{getKey1}), 1.1f * AppConstants.cellSize, doorPos){
            @Override
            void execute(){
                // Despawn the door
                eventEntities.remove("door");
            }
        };
        events.add(getKey2);
    }

    @Override
    public void render(){
        input();
        logic();
        draw();
    }

    private void input(){
        Vector2 movementDirection = new Vector2();
        Vector2 playerPos = playerEntity.getPos();
        float delta = Gdx.graphics.getDeltaTime();

        // Get directional movement from arrow keys
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            movementDirection.x += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            movementDirection.x -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            movementDirection.y += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            movementDirection.y -= 1;
        }

        movementDirection.nor();    // Normalise so diagonal movement is not faster than orthogonal
        playerPos.add(movementDirection.scl(delta * playerEntity.getSpeed()));
        playerEntity.setPos(playerPos);
    }

    private void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float delta = Gdx.graphics.getDeltaTime();

        // Collisions
        for(Entity wallEntity : wallEntities){
            playerEntity.setPos(playerEntity.collide(wallEntity));
        }
        for(Entity eventEntity : eventEntities.values()){
            playerEntity.setPos(playerEntity.collide(eventEntity));
        }

        Vector2 playerPos = playerEntity.getPos();
        // Clamp the player position to the world borders
        playerPos.x = MathUtils.clamp(playerPos.x, 0, AppConstants.mapWidth - playerEntity.getWidth());
        playerPos.y = MathUtils.clamp(playerPos.y, 0, AppConstants.mapHeight - playerEntity.getHeight());

        // Set player position before event interactions
        playerEntity.setPos(playerPos);

        // Check event triggers
        for(Event e : events){
            if(playerEntity.overlaps(e)) e.tryEvent();
        }
        
        timer.tick(delta);
        playerEntity.updatePos();   // Player position should not change after this line
    }

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        playerEntity.draw(spriteBatch);

        // Draw maze walls
        for(Entity wallEntity : wallEntities){
            wallEntity.draw(spriteBatch);
        }

        // Draw extra entities for events
        for(Entity e : eventEntities.values()){
            e.draw(spriteBatch);
        }

        // Menu should be on top of anything maze related
        spriteBatch.draw(menuBgTexture, AppConstants.mapWidth, 0, AppConstants.worldWidth - AppConstants.mapWidth, AppConstants.worldHeight);

        // Draw timer text
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        GlyphLayout timerText = new GlyphLayout(font, timer.toString());
        // Center timeText in the menu
        float timerTextWidth = timerText.width;
        float menuWidth = AppConstants.worldWidth - AppConstants.mapWidth;
        float offset = (menuWidth - timerTextWidth) / 2f;
        font.draw(spriteBatch, timerText, AppConstants.mapWidth + offset, AppConstants.mapHeight - (2 * AppConstants.cellSize));

        spriteBatch.end();
    }
}