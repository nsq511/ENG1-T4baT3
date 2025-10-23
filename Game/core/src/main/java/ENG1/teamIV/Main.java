package ENG1.teamIV;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    FitViewport viewport;
    SpriteBatch spriteBatch;

    Texture backgroundTexture;

    Entity playerEntity;
    Array<Entity> wallEntities;

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void create(){
        viewport = new FitViewport(64, 38);
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture(AppConstants.BACKGROUND_TEX);

        playerEntity = new Entity(AppConstants.PLAYER_TEX, 0.7f, new Vector2());
        playerEntity.setSpeed(5f);

        wallEntities = Utilities.loadMap(AppConstants.MAP_FP);
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
        for(Entity wallEntity : wallEntities){
            playerEntity.updatePos(playerEntity.collide(wallEntity));
        }

        Vector2 playerPos = playerEntity.getPos();
        // Clamp the player position to the world borders
        playerPos.x = MathUtils.clamp(playerPos.x, 0, worldWidth - playerEntity.getWidth());
        playerPos.y = MathUtils.clamp(playerPos.y, 0, worldHeight - playerEntity.getHeight());

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
        for(Entity wallEntity : wallEntities){
            wallEntity.draw(spriteBatch);
        }

        spriteBatch.end();
    }
}