package ENG1.teamIV;

public class AppConstants {
    private AppConstants(){}

    public static final String APP_NAME = "Maze Game";
    public static final String LOGO_FP_SUFFIX = "libgdx.png";     // The window icon needs to be have different resolutions: 128, 64, 32, 16. The resolution will be prefixed to the name, e.g. 128logo.png
    
    public static final String PLAYER_TEX = "player.png";
    public static final String WALL_TEX = "wall.png";
    public static final String BACKGROUND_TEX = "background.png";

    public static final String MAP_FP = "map.txt";

    public static final float playerSpeedDefault = 5f;

    public static final float TIMER_STEP_DEFAULT = 0.5f;
    public static final float TIMER_LIMIT_DEFAULT = 300f;
}
