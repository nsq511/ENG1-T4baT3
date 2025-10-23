package ENG1.teamIV;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Utilities {
    public static Array<Entity> loadMap(String mapFP){
        FileHandle file = Gdx.files.internal(mapFP);
        String[] lines = file.readString().split("\n");
        Array<Entity> walls = new Array<>();

        for(int i = 0; i < lines.length; i++){
            // The screen co-ordinates start at the bottom left
            // The file "co-ordinates" start at the top right
            // So we must flip the file's Y position
            String line = lines[lines.length - 1 - i];
            for(int k = 0; k < line.length(); k++){
                if(line.charAt(k) == '#'){
                    walls.add(new Entity(AppConstants.WALL_TEX, 1, new Vector2(k, i)));
                }
            }
        }
        return walls;
    }
}
