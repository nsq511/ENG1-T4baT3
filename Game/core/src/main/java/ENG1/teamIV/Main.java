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

    Array<Entity> wallEntities;
    
    BitmapFont smallFont;
    BitmapFont largeFont;
    Timer timer;
    Music music;
    Sound dropSound;

    // Player
    Entity playerEntity;

    // Events
    Array<Event> events;
    ObjectMap<String, Entity> eventEntities;    // Entities related to events should be added and removed as required

    // Game states
    boolean freeze;     // Whether to freeze gameplay (for pause and game end)
    boolean gameEnd;    // Whether the game is finished
    boolean win;        // Whether the game end is due to a win or loss
    boolean paused;     // Whether the game is paused
    // freeze will simply stop gameplay, while pause will draw the pause screen. Both should be true when paused

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void create(){
        // Set up game states to start paused
        paused = true;
        freeze = true;
        gameEnd = false;
        win = false;

        // Basics setup
        viewport = new FitViewport(AppConstants.worldWidth, AppConstants.worldHeight);
        spriteBatch = new SpriteBatch();
        smallFont = new BitmapFont();
        smallFont.getData().setScale(2f);
        largeFont = new BitmapFont();
        largeFont.getData().setScale(7f);

        backgroundTexture = new Texture(AppConstants.BACKGROUND_TEX);
        menuBgTexture = new Texture(AppConstants.MENU_BG_TEX);

        // Player setup
        playerEntity = new Entity(AppConstants.PLAYER_TEX, 0.7f * AppConstants.cellSize, new Vector2());
        playerEntity.setSpeed(AppConstants.playerSpeedDefault);
        playerEntity.collidable = true;

        // Map setup
        wallEntities = Utilities.loadMap(AppConstants.MAP_FP);

        // Events setup
        eventEntities = new ObjectMap<>();
        events = new Array<>();

        timer = new Timer(AppConstants.TIMER_LIMIT_DEFAULT, AppConstants.TIMER_STEP_DEFAULT);
        music = Gdx.audio.newMusic(Gdx.files.internal(AppConstants.MUSIC_FP));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        dropSound = Gdx.audio.newSound(Gdx.files.internal(AppConstants.DROP_SOUND_FP));

        // Define events here
        
        // 0. Game Win
        Vector2 endPos = new Vector2(AppConstants.mapWidth - AppConstants.cellSize, AppConstants.mapHeight - AppConstants.cellSize);

        Event gameWin0 = new Event(new Array<>(), AppConstants.mapWidth, new Vector2()){
            @Override
            void execute(){
                // Spawn end cell
                Entity endCell = new Entity(AppConstants.END_CELL_TEX, AppConstants.cellSize, endPos);
                eventEntities.put("endCell", endCell);
            }
        };
        events.add(gameWin0);

        Event gameWin1 = new Event(new Array<>(new Event[]{gameWin0}), 0.2f * AppConstants.cellSize, endPos){
            @Override
            void execute(){
                // Win game
                freeze = true;
                gameEnd = true;
                win = true;
            }
        };
        events.add(gameWin1);
        
        // 1. Key Event
        Vector2 doorPos = new Vector2(380, 370);
        Vector2 keyPos = new Vector2(60, 260);
        
        // Event init
        Event getKey0 = new Event(new Array<>(), AppConstants.mapWidth, new Vector2()){
            @Override
            void execute(){        
                // Create a door to block the path
                Entity door = new Entity(AppConstants.DOOR_TEX, AppConstants.cellSize, doorPos);
                door.collidable = true;
                eventEntities.put("door", door);
            }
        };
        events.add(getKey0);

        // Event trigger
        Event getKey1 = new Event(new Array<>(new Event[]{getKey0}), 1.1f * AppConstants.cellSize, doorPos){
            @Override
            void execute(){
                // Spawn a key
                eventEntities.put("key", new Entity(AppConstants.KEY_TEX, 0.8f * AppConstants.cellSize, keyPos));
                dropSound.play();
                System.out.println("Pick up the key to open the door!");
            }
        };
        events.add(getKey1);

        // Pick up the key
        Event getKey2 = new Event(new Array<>(new Event[]{getKey1}), 0.7f * AppConstants.cellSize, keyPos){
            @Override
            void execute(){
                // Despawn the key
                eventEntities.remove("key");
                dropSound.play();
            }
        };
        events.add(getKey2);

        // Open the door
        Event getKey3 = new Event(new Array<>(new Event[]{getKey2}), 1.1f * AppConstants.cellSize, doorPos){
            @Override
            void execute(){
                // Despawn the door
                eventEntities.remove("door");
            }
        };
        events.add(getKey3);
    }

    @Override
    public void render(){
        input();
        logic();
        draw();
    }

    private void input(){
        // Some checks must still be done while frozen
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            if(gameEnd){
                // Restart game
                restart();
            }
            else{
                // Toggle pause
                paused = !paused;
                // freeze may be changed due to non-pause events such as a game end
                // So freeze and pause may become out of sync
                // Therefore, freeze must be set to the value of paused rather than simply toggled
                freeze = paused;
            }
        }

        if(freeze) return;      // Anything after this will not run during pause/game end

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
        if(freeze){
            music.pause();
            return;
        }
        else{
            music.play();
        }
        // Anything after this will not run while frozen

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

        // Check game-over
        if(timer.isFinished()){
            freeze = true;
            gameEnd = true;
            win = false;
            music.stop();
        }
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
        smallFont.setColor(Color.WHITE);
        GlyphLayout timerText = new GlyphLayout(smallFont, timer.toString());
        // Center timeText in the menu
        float timerTextWidth = timerText.width;
        float menuWidth = AppConstants.worldWidth - AppConstants.mapWidth;
        float offset = (menuWidth - timerTextWidth) / 2f;
        smallFont.draw(spriteBatch, timerText, AppConstants.mapWidth + offset, AppConstants.mapHeight - (2 * AppConstants.cellSize));

        // Draw pause screen
        if(paused){
            overlay("PAUSED", "Press ESC to Resume");
        }
        if(gameEnd){
            if(win){
                overlay("YOU WIN!", "Press ESC to Restart");
            }
            else{
                overlay("GAME OVER", "Press ESC to Restart");
            }
        }

        spriteBatch.end();
    }

    /**
     * Write an overlay on the screen with a main message in large text above a secondary message in smaller text
     * 
     * @param mainMsg The main message to be shown in large text
     * @param minorMsg The secondary message to be shown below the main message in smaller text
     */
    private void overlay(String mainMsg, String minorMsg){
        // Main message
        largeFont.setColor(Color.RED);
        GlyphLayout mainText = new GlyphLayout(largeFont, mainMsg);
        float mainTextWidth = mainText.width;
        float mainTextHeight = mainText.height;
        float offsetX = (AppConstants.worldWidth - mainTextWidth) / 2f;
        float offsetY = (AppConstants.worldHeight + mainTextHeight) / 2f;
        largeFont.draw(spriteBatch, mainText, offsetX, offsetY);

        // Minor message
        smallFont.setColor(Color.RED);
        GlyphLayout minorText = new GlyphLayout(smallFont, minorMsg);
        float minorTextWidth = minorText.width;
        offsetX = (AppConstants.worldWidth - minorTextWidth) / 2f;
        offsetY = offsetY - mainTextHeight - AppConstants.cellSize;
        smallFont.draw(spriteBatch, minorText, offsetX, offsetY);
    }

    @Override
    public void pause(){
        freeze = true;
        paused = true;
    }

    @Override
    public void resume(){
    }

    public void restart(){
        // Reset game states
        gameEnd = false;
        freeze = false;
        win = false;
        paused = false;

        // Reset player
        playerEntity.reset();
        playerEntity.collidable = true;
        playerEntity.setSpeed(AppConstants.playerSpeedDefault);

        // Reset events
        for(Event e : events){
            e.reset();
        }
        // Reset eventEntities
        eventEntities.clear();

        // Reset timer
        timer.reset();

        music.play();
    }
}