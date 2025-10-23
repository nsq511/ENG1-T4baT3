package ENG1.teamIV;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    FitViewport viewport;
    SpriteBatch spriteBatch;

    Texture playerTexture;
    Texture backgroundTexture;
    Texture wallTexture;

    Entity playerEntity;
    Entity wallEntity;

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void create(){
        viewport = new FitViewport(16, 10);
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");

        playerEntity = new Entity("player.png", 1, new Vector2());
        playerEntity.setSpeed(5f);

        wallEntity = new Entity("wall.png", 1, new Vector2(4,3));
    }

    @Override
    public void render(){
        input();
        logic();
        draw();
    }

    private void input(){
        Vector2 movementDirection = new Vector2();
        Vector2 playerPos = playerEntity.getPos();
        float delta = Gdx.graphics.getDeltaTime();

        // Get directional movement from arrow keys
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            movementDirection.x += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            movementDirection.x -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            movementDirection.y += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            movementDirection.y -= 1;
        }

        movementDirection.nor();    // Normalise so diagonal movement is not faster than orthogonal
        playerPos.add(movementDirection.scl(delta * playerEntity.getSpeed()));
        playerEntity.updatePos(playerPos);
    }

    private void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Collisions
        Vector2 playerPos = playerEntity.collide(wallEntity);

        // Clamp the player position to the world borders
        playerPos.x = MathUtils.clamp(playerPos.x, 0, worldWidth - playerEntity.getSize());
        playerPos.y = MathUtils.clamp(playerPos.y, 0, worldHeight - playerEntity.getSize());

        playerEntity.updatePos(playerPos);
    }

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        playerEntity.draw(spriteBatch);
        wallEntity.draw(spriteBatch);

        spriteBatch.end();
    }
}