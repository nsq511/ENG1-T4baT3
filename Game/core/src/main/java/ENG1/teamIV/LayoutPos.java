package ENG1.teamIV;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

public class LayoutPos{
    public final GlyphLayout glyphLayout;
    public final Vector2 pos;

    public LayoutPos(GlyphLayout layout, Vector2 position){
        glyphLayout = layout;
        pos = position;
    }
}