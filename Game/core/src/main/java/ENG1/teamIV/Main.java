package ENG1.teamIV;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    FitViewport viewport;
    SpriteBatch spriteBatch;

    Texture playerTexture;
    Texture backgroundTexture;

    Entity playerEntity;

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void create(){
        viewport = new FitViewport(8, 5);
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        playerTexture = new Texture("player.png");

        playerEntity = new Entity(playerTexture, 1, new Vector2());
    }

    @Override
    public void render(){
        input();
        logic();
        draw();
    }

    private void input(){

    }

    private void logic(){

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

        spriteBatch.end();
    }
}