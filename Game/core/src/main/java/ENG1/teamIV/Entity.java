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

    public Entity(String spriteTexture, float size, Vector2 pos){
        sprite = new Sprite(new Texture(spriteTexture));
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
        // Update the sprite and it's rectangle together
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

    /**
     * Checks whether entities are colliding
     * 
     * @param other The other entity in the collisison
     * @return Whether the entities overlap
     */
    public boolean overlaps(Entity other){
        return rectangle.overlaps(other.rectangle);
    }

    /**
     * Finds the Minimum Translation Vector to separate two overlapping entities
     * Used in collision resolution
     * 
     * @param other The other entity in the collision
     * @return The Minimum Translation Vector that will push the this entity out. null if no collision
     */
    private Vector2 getMTV(Entity other){
        if(!rectangle.overlaps(other.rectangle)) return null;

        // Subtract the rightmost left-edge of the rectangles from the leftmost right-edge of the rectangles
        // This gives the size of overlap
        float overlapX = Math.min(rectangle.x + rectangle.width, other.rectangle.x + other.rectangle.width) - Math.max(rectangle.x, other.rectangle.x);
        // Similar for Y axis
        float overlapY = Math.min(rectangle.y + rectangle.height, other.rectangle.y + other.rectangle.height) - Math.max(rectangle.y, other.rectangle.y);

        // Move in the direction of minumum overlap
        if(overlapX < overlapY){
            // Push along the X axis

            // If this entity's centre is to the right of the other entity's centre then move to the right
            if(rectangle.x + rectangle.width / 2f > other.rectangle.x + other.rectangle.width / 2f){
                return new Vector2(overlapX, 0);
            }
            else{
                return new Vector2(-overlapX, 0);
            }
        }
        else{
            // Push along the Y axis

            // If this entity's centre is above the other entity's centre then move to the up
            if(rectangle.y + rectangle.height / 2f > other.rectangle.y + other.rectangle.height / 2f){
                return new Vector2(0, overlapY);
            }
            else{
                return new Vector2(0, -overlapY);
            }
        }
    }

    /**
     * Resolves a collision between entity and other by moving this entity out of the overlap
     * using the MTV
     * 
     * @param other The other entity in the collision
     * @return The new position of the entity
     */
    public Vector2 collide(Entity other){
        Vector2 mtv = getMTV(other);
        if(mtv == null) return getPos();     // No collision

        return getPos().add(mtv);
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
