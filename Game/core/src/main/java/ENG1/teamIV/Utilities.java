package ENG1.teamIV;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Utilities {
    /**
     * Loads in the walls of the maze as Entities
     * The shape of the maze is defined in the map file, where '#' represents a wall
     * The dimensions of the maze in the map file should match the world dimensions
     * If they are larger then portions of the map will exist out of bounds
     * The player will spawn at position (0, 0) so this space should be kept clear
     * 
     * @param mapFP The filepath to the text file that defines the map
     * @return An Array of Entities representing the walls
     */
    public static Array<Entity> loadMap(String mapFP){
        FileHandle file = Gdx.files.internal(mapFP);
        String[] lines = file.readString().split("\n");

        Array<Entity> walls = new Array<>();
        Texture wallTex = new Texture(AppConstants.WALL_TEX);

        for(int i = 0; i < lines.length; i++){
            // The screen co-ordinates start at the bottom left
            // The file "co-ordinates" start at the top left
            // So we must invert the file's Y position by starting from the last line
            String line = lines[lines.length - 1 - i];
            for(int k = 0; k < line.length(); k++){
                if(line.charAt(k) == '#'){
                    Entity wall = new Entity(wallTex, 1f, new Vector2(k, i));
                    wall.collidable = true;
                    walls.add(wall);
                }
            }
        }
        return walls;
    }

    public static String doubleDigit(int num){
        if(num < 10){
            return "0" + Integer.toString(num);
        }
        else{
            return Integer.toString(num);
        }
    }
}
