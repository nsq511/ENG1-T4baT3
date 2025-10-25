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

        // Check height
        // The map file must adhere to the map dimensions specified in AppConstants
        if(lines.length != AppConstants.mapHeight) throw new InvalidMazeFormatException(InvalidMazeFormatException.INCOMPATIBLE_HEIGHT_MSG(lines.length));

        for(int i = 0; i < lines.length; i++){
            // The screen co-ordinates start at the bottom left
            // The file "co-ordinates" start at the top left
            // So we must invert the file's Y position by starting from the last line
            String line = lines[lines.length - 1 - i];

            // Check width
            // The map file must adhere to the map dimensions specified in AppConstants
            if(line.length() != AppConstants.mapWidth) throw new InvalidMazeFormatException(InvalidMazeFormatException.INCOMPATIBLE_WIDTH_MSG(line.length(), lines.length - i));

            for(int k = 0; k < line.length(); k++){
                if(line.charAt(k) == '#'){
                    Entity wall = new Entity(wallTex, 1f, new Vector2(k, i));
                    wall.collidable = true;
                    walls.add(wall);
                }
                else if(line.charAt(k) == ' '){
                    continue;
                }
                else{
                    throw new InvalidMazeFormatException(InvalidMazeFormatException.INVALID_CHARACTER(line.charAt(k), lines.length - i, k + 1));
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

class InvalidMazeFormatException extends RuntimeException{
        public static String INCOMPATIBLE_HEIGHT_MSG(int numLines){
            return "Maze height does not match map height. AppConstants.mapHeight = "
            + AppConstants.mapHeight + ", but map file '" + AppConstants.MAP_FP
             + "' has " + numLines + " lines";
        }

        public static String INCOMPATIBLE_WIDTH_MSG(int numChars, int lineNo){
            return "Maze width does not match map width. AppConstants.mapWidth = "
            + AppConstants.mapWidth + ", but map file '" + AppConstants.MAP_FP
             + "' has " + numChars + " characters on line " + lineNo;
        }

        public static String INVALID_CHARACTER(char c, int lineNo, int colNo){
            return "Invalid character in map file '" + AppConstants.MAP_FP
             + "'. '" + c + "' on line " + lineNo + ", column " + (colNo + 1);
        }
        
        public InvalidMazeFormatException(String message){
            super(message);
        }
    }