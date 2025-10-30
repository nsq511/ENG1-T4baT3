package ENG1.teamIV;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

/**
 * Combines a glyph layout and vector 2 so functions that handle text can return an object
 * that contians all the data about the text drawn
 * 
 * pos is the position at the top left of the text
 */
public class LayoutPos{
    public final GlyphLayout glyphLayout;
    public final Vector2 pos;

    public LayoutPos(GlyphLayout layout, Vector2 position){
        glyphLayout = layout;
        pos = position;
    }
}