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
    private float speed;

    public Entity(Texture spriteTexture, float size, Vector2 pos){
        sprite = new Sprite(spriteTexture);
        sprite.setSize(size, size);

        rectangle = new Rectangle();

        speed = 0f;

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
     * Updates the position of the Entity
     * 
     * @param pos The world position to set the entity to
     */
    public void updatePos(float x, float y){
        sprite.setPosition(x, y);
        rectangle.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Get the position of the Entity
     * 
     * @return The position of the Entities anchor
     */
    public Vector2 getPos(){
        return new Vector2(sprite.getX(), sprite.getY());
    }

    /**
     * Draws the sprite
     * @param batch The SpriteBatch to draw to
     */
    public void draw(Batch batch){
        sprite.draw(batch);
    }

    public float getSpeed(){
        return speed;
    }

    public void setSpeed(float newSpeed){
        if (newSpeed < 0){
            throw new IllegalArgumentException("Speed must not be negative");
        }
        speed = newSpeed;
    }

    public float getSize(){
        return sprite.getWidth();   // Width and height are the same
    }
}
