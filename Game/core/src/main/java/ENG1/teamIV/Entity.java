package ENG1.teamIV;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Combines a Sprite and a Rectangle so translation operations act on both 
 */
public class Entity {
    private Sprite sprite;
    private Rectangle rectangle;

    public Entity(Texture spriteTexture, float size, Vector2 pos){
        sprite = new Sprite(spriteTexture);
        sprite.setSize(size, size);

        rectangle = new Rectangle();

        updatePos(pos);
    }

    /**
     * Updates the position of the Entity
     * 
     * @param pos The world position to set the entity to
     */
    public void updatePos(Vector2 pos){
        sprite.setPosition(pos.x, pos.y);
        rectangle.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Draws the sprite
     * @param batch The SpriteBatch to draw to
     */
    public void draw(Batch batch){
        sprite.draw(batch);
    }
}
