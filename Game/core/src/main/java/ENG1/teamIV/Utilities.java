package ENG1.teamIV;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

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
        FileHandle file;
        String[] lines;
        try{
            file = Gdx.files.internal(mapFP);
            lines = file.readString().split("\n");
        }
        catch(GdxRuntimeException e){
            System.err.println("Could not open file '" + mapFP + "'. Please check the filepath is correct or ensure file exists");
            return new Array<>();
        }

        Array<Entity> walls = new Array<>();
        Texture wallTex = new Texture(AppConstants.WALL_TEX);

        // Check height
        // The map file must adhere to the map dimensions specified in AppConstants
        if(lines.length != AppConstants.mapHeight / AppConstants.cellSize) throw new InvalidMazeFormatException(InvalidMazeFormatException.INCOMPATIBLE_HEIGHT_MSG(lines.length));

        for(int i = 0; i < lines.length; i++){
            // The screen co-ordinates start at the bottom left
            // The file "co-ordinates" start at the top left
            // So we must invert the file's Y position by starting from the last line
            String line = lines[lines.length - 1 - i];

            // Check width
            // The map file must adhere to the map dimensions specified in AppConstants
            if(line.length() != AppConstants.mapWidth / AppConstants.cellSize) throw new InvalidMazeFormatException(InvalidMazeFormatException.INCOMPATIBLE_WIDTH_MSG(line.length(), lines.length - i));

            for(int k = 0; k < line.length(); k++){
                if(line.charAt(k) == '#'){
                    Entity wall = new Entity(wallTex, AppConstants.cellSize, new Vector2(k , i).scl(AppConstants.cellSize));
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

    /**
     * Aligns the {@link Entity Entity's} centre to the centre of the cell that its position is within
     * 
     * E.g. If the cell size is 10, an entity with position (248, 300) will be moved so its centre is at (245, 305)
     * 
     * @param entity The entity to adjust
     */
    public static void centreOnCell(Entity entity){
        int cellSize = AppConstants.cellSize;

        // Find the cell the entity is in
        Vector2 pos = entity.getPos();
        pos.x = ((int)pos.x / cellSize) * cellSize;
        pos.y = ((int)pos.y / cellSize) * cellSize;
        
        // Find the centre of the cell
        pos.x += cellSize / 2f;
        pos.y += cellSize / 2f;

        // Find the entity position's offset from the entity's centre
        Vector2 offset = new Vector2(entity.getWidth() / 2f, entity.getHeight() / 2f);
        
        // Find the position that aligns the entity and the cell's centres
        Vector2 newPos = pos.sub(offset);

        entity.setPos(newPos);
    }

    /**
     * Insert newlines into the text in order to have it fit withing a certain width
     * 
     * @param text The text to wrap
     * @param width The width the text must fit within
     * @param font The font to use when writing the string
     * @return The wrapped text
     */
    public static String wrapText(String text, float width, BitmapFont font){
        // Split the text into words
        String[] words = text.split(" ");

        // Get the width of a single generic character for this font
        GlyphLayout layout = new GlyphLayout(font, "A");
        float charWidth = layout.width;

        // Must have a width of at least 2 characters in order to insert a '-' symbol on any words broken up across lines
        if(width / charWidth < 2) throw new IllegalArgumentException("Width must be wide enough to fit at least 2 characters of the specified font. (" + (charWidth * 2) + ")");
        
        // Progressively add words to each line, creating a new line if the line width exceeds the allocated width
        String wrappedText = "";
        float widthLine = 0;
        for(String word : words){
            float widthWord = word.length() * charWidth;

            if(widthWord > width){
                // If the word is bigger than the line width, split the word up
                float remainingWidth = width - widthLine;

                if(remainingWidth < charWidth * 3){
                    // Need at least a width of 3 characters remaining in the line
                    // One for ' ', one for at least the first character of the word, one for '-'
                    // Otherwise, simply start the word on a new line
                    wrappedText += "\n";
                    widthLine = 0;
                    remainingWidth = width;
                }
                else{
                    // If the line has space to start the word on the current line, add the space
                    wrappedText += " ";
                    remainingWidth--;
                }

                int remainingCharSpaces = (int)(remainingWidth / charWidth);
                // Repeatedly split the word up across however many lines are necessary
                while(remainingCharSpaces < word.length()){
                    // Get the section of the word that can fit in the line
                    String firstPart = word.substring(0, remainingCharSpaces - 1);
                    wrappedText += firstPart + "-\n";
                    word = word.substring(remainingCharSpaces - 1, word.length());
                    remainingCharSpaces = (int)(width / charWidth);
                }
                wrappedText += word;
                widthLine = word.length();        
            }
            else if(widthLine + widthWord <= width){
                // If the word fits in the line then add it
                wrappedText += " " + word;
                widthLine += widthWord;
            }
            else{
                // If the word does not fit on the line, create a new line
                wrappedText += "\n" + word;
                widthLine = widthWord;
            }
        }
        return wrappedText.substring(1, wrappedText.length()); // Remove the leading space character
    }

/**
     * Write the specified text
     * 
     * @param spriteBatch The batch that draws the font
     * @param font The font to use for the text
     * @param text The text to write
     * @param pos The position to write the text to (origin top-left)
     * @param colour The colour of the text
     * 
     * @return The LayoutPos contianing the GlyphLayout used to render the text and the position of the text
     */
    public static LayoutPos writeText(SpriteBatch spriteBatch, BitmapFont font, String text, Vector2 pos, Color colour){
        font.setColor(colour);
        GlyphLayout textLayout = new GlyphLayout(font, text);
        font.draw(spriteBatch, textLayout, pos.x, pos.y);

        return new LayoutPos(textLayout, pos.cpy());
    }

    /**
     * Write the specified text, centred in the width provided
     * 
     * @param spriteBatch The batch that draws the font
     * @param font The font to use for the text
     * @param text The text to write
     * @param pos The position to write the text to (origin top-left)
     * @param width The width of the window to centre the text in
     * @param colour The colour of the text
     * 
     * @return The LayoutPos contianing the GlyphLayout used to render the text and the position of the text
     */
    public static LayoutPos writeText(SpriteBatch spriteBatch, BitmapFont font, String text, Vector2 pos, float width, Color colour){
        font.setColor(colour);
        GlyphLayout textLayout = new GlyphLayout(font, text);
        float textWidth = textLayout.width;

        // Wrap text if needed
        if(textWidth > width){
            textLayout = new GlyphLayout(font, wrapText(text, width, font));
            textWidth = textLayout.width;
        }

        // Center text in the window
        float offset = (width - textWidth) / 2f;
        float textX = pos.x + offset;
        float textY = pos.y;
        font.draw(spriteBatch, textLayout, textX, textY);

        return new LayoutPos(textLayout, new Vector2(textX, textY));
    }

    /**
     * Write the specified text, centred in the width and height provided
     * 
     * @param spriteBatch The batch that draws the font
     * @param font The font to use for the text
     * @param text The text to write
     * @param pos The position to write the text to (origin top-left)
     * @param width The width of the window to centre the text in
     * @param height The height of the window to centre the text in 
     * @param colour The colour of the text
     * 
     * @return The LayoutPos contianing the GlyphLayout used to render the text and the position of the text
     */
    public static LayoutPos writeText(SpriteBatch spriteBatch, BitmapFont font, String text, Vector2 pos, float width, float height, Color colour){
        font.setColor(colour);
        GlyphLayout textLayout = new GlyphLayout(font, text);
        float textWidth = textLayout.width;
        float textHeight = textLayout.height;

        // Wrap text if needed
        if(textWidth > width){
            textLayout = new GlyphLayout(font, wrapText(text, width, font));
            textWidth = textLayout.width;
        }

        // Center text in the window
        float offsetX = (width - textWidth) / 2f;
        float offsetY = (height - textHeight) / 2f;
        float textX = pos.x + offsetX;
        float textY = pos.y - offsetY;  // Negative because the text anchor is at the top left
        font.draw(spriteBatch, textLayout, textX, textY);

        return new LayoutPos(textLayout, new Vector2(textX, textY));
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
            + AppConstants.mapHeight / AppConstants.cellSize + ", but map file '" + AppConstants.MAP_FP
             + "' has " + numLines + " lines";
        }

        public static String INCOMPATIBLE_WIDTH_MSG(int numChars, int lineNo){
            return "Maze width does not match map width. AppConstants.mapWidth = "
            + AppConstants.mapWidth / AppConstants.cellSize + ", but map file '" + AppConstants.MAP_FP
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