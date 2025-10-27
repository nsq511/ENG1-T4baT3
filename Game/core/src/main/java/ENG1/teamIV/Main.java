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
    Texture controlsTexture;

    Array<Entity> wallEntities;
    
    BitmapFont smallFont;
    BitmapFont mediumFont;
    BitmapFont largeFont;
    Timer timer;
    Music music;
    Sound dropSound;

    int score;

    // This string is displayed in the menu and is used to relay information to the user
    // It should be kept short in order to fit in the space allocated
    // Updating this will overwrite the previous message
    // Any area of code, i.e. an event, can write to this variable to display a message
    // E.g. If the player tries to open a locked door, this message could be set to "Pick up the key to open the door!"
    String menuMsg;

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

        score = 0;
        
        // Fonts
        smallFont = new BitmapFont();
        smallFont.getData().setScale(0.7f);
        mediumFont = new BitmapFont();
        mediumFont.getData().setScale(2f);
        largeFont = new BitmapFont();
        largeFont.getData().setScale(7f);

        backgroundTexture = new Texture(AppConstants.BACKGROUND_TEX);
        menuBgTexture = new Texture(AppConstants.MENU_BG_TEX);
        controlsTexture = new Texture(AppConstants.CONTROLS_TEX);

        menuMsg = "";

        // Player setup
        playerEntity = new Entity(AppConstants.PLAYER_TEX, 0.7f * AppConstants.cellSize, new Vector2());
        playerEntity.setSpeed(AppConstants.playerSpeedDefault);
        playerEntity.collidable = true;

        // Map setup
        wallEntities = Utilities.loadMap(AppConstants.MAP_FP);
        
        timer = new Timer(AppConstants.TIMER_LIMIT_DEFAULT, AppConstants.TIMER_STEP_DEFAULT);
        music = Gdx.audio.newMusic(Gdx.files.internal(AppConstants.MUSIC_FP));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        dropSound = Gdx.audio.newSound(Gdx.files.internal(AppConstants.DROP_SOUND_FP));

        // Events setup
        eventEntities = new ObjectMap<>();
        events = new Array<>();

        // Define events here
        
        // 0. Game Win
        Vector2 endPos = new Vector2(AppConstants.mapWidth - AppConstants.cellSize, AppConstants.mapHeight - AppConstants.cellSize);

        Event gameWin0 = new Event(new Array<>(), AppConstants.mapWidth, new Vector2()){
            @Override
            void execute(){
                // Spawn end cell
                Entity endCell = new Entity(AppConstants.END_CELL_TEX, AppConstants.cellSize, endPos);
                eventEntities.put("endCell", endCell);
                menuMsg = "Escape the maze!";
            }
        };
        events.add(gameWin0);

        Event gameWin1 = new Event(new Array<>(new Event[]{gameWin0}), 0.3f * AppConstants.cellSize, endPos){
            @Override
            void execute(){
                menuMsg = "You escaped!";
                // Win game
                freeze = true;
                gameEnd = true;
                win = true;
            }
        };
        Utilities.centreOnCell(gameWin1);
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
                Entity key = new Entity(AppConstants.KEY_TEX, 0.8f * AppConstants.cellSize, keyPos);
                Utilities.centreOnCell(key);
                eventEntities.put("key", key);

                dropSound.play();
                menuMsg = "Pick up the key to open the door!";
            }
        };
        Utilities.centreOnCell(getKey1);
        events.add(getKey1);

        // Pick up the key
        Event getKey2 = new Event(new Array<>(new Event[]{getKey1}), 0.7f * AppConstants.cellSize, keyPos){
            @Override
            void execute(){
                // Despawn the key
                eventEntities.remove("key");
                dropSound.play();
                menuMsg = "Picked up key. Open the door!";
            }
        };
        Utilities.centreOnCell(getKey2);
        events.add(getKey2);

        // Open the door
        Event getKey3 = new Event(new Array<>(new Event[]{getKey2}), 1.1f * AppConstants.cellSize, doorPos){
            @Override
            void execute(){
                // Despawn the door
                eventEntities.remove("door");
                menuMsg = "Door opened!";
                Event.incrementBadEventCounter();
            }
        };
        Utilities.centreOnCell(getKey3);
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

        // Update score
        score = timer.toScore();
        
        timer.tick(delta);
        playerEntity.updatePos();   // Player position should not change after this line

        // Check game-over
        if(timer.isFinished()){
            menuMsg = "Times up...";
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

        // Draw controls texture
        float controlsBuffer = AppConstants.cellSize / 2f;
        float controlsX = AppConstants.mapWidth + controlsBuffer;
        float controlsY = controlsBuffer;
        float controlsWidth = AppConstants.worldWidth - AppConstants.mapWidth - (2 * controlsBuffer);
        float controlsHeight = 1.4f * controlsWidth;
        spriteBatch.draw(controlsTexture, controlsX, controlsY, controlsWidth, controlsHeight);

        // Draw timer text
        float menuWidth = AppConstants.worldWidth - AppConstants.mapWidth;
        float timerTextX = AppConstants.mapWidth;
        float timerTextY = AppConstants.mapHeight - (2 * AppConstants.cellSize);
        LayoutPos timerTextLP = Utilities.writeText(spriteBatch, mediumFont, timer.toString(), new Vector2(timerTextX, timerTextY), menuWidth, Color.WHITE);
        GlyphLayout timerTextLayout = timerTextLP.glyphLayout;

        // Display the menuMsg
        float buffer = AppConstants.cellSize;
        float menuMsgMaxWidth = menuWidth - buffer;  // Give a little buffer around the message
        float menuMsgX = AppConstants.mapWidth + (buffer / 2f);
        float menuMsgY = timerTextY - timerTextLayout.height - (4 * AppConstants.cellSize);
        LayoutPos menuMsgLP = Utilities.writeText(spriteBatch, smallFont, menuMsg, new Vector2(menuMsgX, menuMsgY), menuMsgMaxWidth, Color.WHITE);
        
        // Draw completed event counters
        float counterBufferY = AppConstants.cellSize * 3;
        float countersY = controlsY + controlsHeight + timerTextLayout.height + counterBufferY;      // We add timerTextLayout.height because it is the same size font as the counters and they draw from a top left origin
        // Draw the middle counter first. Simply centre it on the whole menu width
        float badCounterX = AppConstants.mapWidth;
        LayoutPos badCounterLP = Utilities.writeText(spriteBatch, mediumFont, Integer.toString(Event.getBadEventCounter()), new Vector2(badCounterX, countersY), menuWidth, Color.RED);
        // The left and right counters can now use half the menu width to determine the window to centre in
        float counterWindowWidth = menuWidth / 2f;
        LayoutPos goodCounterLP = Utilities.writeText(spriteBatch, mediumFont, Integer.toString(Event.getGoodEventCounter()), new Vector2(badCounterX, countersY), counterWindowWidth, Color.GREEN);
        LayoutPos hiddenCounterLP = Utilities.writeText(spriteBatch, mediumFont, Integer.toString(Event.getHiddenEventCounter()), new Vector2(badCounterX + counterWindowWidth, countersY), counterWindowWidth, Color.ORANGE);

        // Draw score
        float scoreBuffer = AppConstants.cellSize;
        float scoreTextX = AppConstants.mapWidth + scoreBuffer;
        float scoreTextY = badCounterLP.pos.y + (AppConstants.cellSize * 3f) + timerTextLayout.height;      // We add timerTextLayout.height because it is the same size font as the counters and they draw from a top left origin
        LayoutPos scoreTextLP = Utilities.writeText(spriteBatch, smallFont, "Score:", new Vector2(scoreTextX, scoreTextY), Color.WHITE);
        scoreTextX = scoreTextLP.pos.x + scoreTextLP.glyphLayout.width;
        // Create a vertical window larger than the size of a medium font and it will centre on the same line as the small font
        scoreTextY = scoreTextLP.pos.y + scoreTextLP.glyphLayout.height; // One small character above the start of the text
        float scoreTextHeightWindow = scoreTextLP.glyphLayout.height * 3f;  // One for the character above the line, one on the line, and one below the line
        // Get the horizontal window
        float scoreTextWidthWindow = AppConstants.worldWidth - scoreTextLP.pos.x - scoreTextLP.glyphLayout.width - scoreBuffer;
        LayoutPos scoreValueLP = Utilities.writeText(spriteBatch, mediumFont, Integer.toString(score), new Vector2(scoreTextX, scoreTextY), scoreTextWidthWindow, scoreTextHeightWindow, Color.WHITE);

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
        LayoutPos mainLayout = Utilities.writeText(spriteBatch, largeFont, mainMsg, new Vector2(0, AppConstants.worldHeight), AppConstants.worldWidth, AppConstants.worldHeight, Color.RED);

        // Minor message
        float offsetY = mainLayout.pos.y - mainLayout.glyphLayout.height - AppConstants.cellSize;
        LayoutPos minorLayout = Utilities.writeText(spriteBatch, mediumFont, minorMsg, new Vector2(0, offsetY), AppConstants.worldWidth, Color.RED);
    }

    @Override
    public void pause(){
        // Pause the game
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

        score = 0;

        menuMsg = "";

        Event.resetEventCounters();

        music.play();
    }
}