package ENG1.teamIV;

public class AppConstants {
    private AppConstants(){}

    public static final String APP_NAME = "Maze Game";
    public static final String LOGO_FP_SUFFIX = "libgdx.png";     // The window icon needs to be have different resolutions: 128, 64, 32, 16. The resolution will be prefixed to the name, e.g. 128logo.png
    
    public static final String PLAYER_TEX = "player.png";
    public static final String WALL_TEX = "wall.png";
    public static final String BACKGROUND_TEX = "background.png";
    public static final String TRANSPARENT_TEX = "Empty.png";
    public static final String KEY_TEX = "key.png";
    public static final String DOOR_TEX = "door.png";
    public static final String MENU_BG_TEX = "menu_bg.png";
    public static final String END_CELL_TEX = "end.png";
    public static final String CONTROLS_TEX = "controls.png";
    public static final String TRIO_BG_TEX = "trio_bg.png";

    public static final String MUSIC_FP = "music.mp3";
    public static final String DROP_SOUND_FP = "drop.mp3";

    public static final String MAP_FP = "map.txt";

    public static final float playerSpeedDefault = 50f;

    public static final float TIMER_STEP_DEFAULT = 0.5f;
    public static final float TIMER_LIMIT_DEFAULT = 300f;

    public static final int worldHeight = 400;
    public static final int worldWidth = 650;
    public static final int mapWidth = 550;
    public static final int mapHeight = worldHeight;
    public static final int cellSize = 10; // The size of each cell in the world grid

    public static final String trioCode = "123456";
}
