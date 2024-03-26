package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.instattrunner.InstattRunner;
import com.instattrunner.ScreenManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;




public class MenuScreen implements Screen {
    private ScreenManager parent;
    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;

    public MenuScreen(ScreenManager screenManager) {
        parent = screenManager;
        OrthographicCamera gameCam  = new OrthographicCamera();
        stage = new Stage(new FitViewport(parent.VIEW_WIDTH, parent.VIEW_HEIGHT, gameCam));
        // load skin using asset manager
        parent.assetMan.queueAddSkin();
        parent.assetMan.manager.finishLoading();
        skin = parent.assetMan.manager.get("skin/comic-ui.json");

        backgroundTexture = new Texture(Gdx.files.internal("pic/background.jpg")); // Change "background_image.png" to your image path
    }

    @Override
    public void show() {
        // Set the background image
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);

        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Add table (which holds buttons) to the stage
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);

        stage.addActor(table);

        // Create labels and buttons
        // Assuming you have a style defined in your skin
        LabelStyle bigLabelStyle = skin.get("big", LabelStyle.class);
        bigLabelStyle.font.getData().setScale(2); // Set font scale to 2, making it twice as big

        // Now you can apply this style to your label
        Label titleLabel = new Label("Instatt Runner", bigLabelStyle);
        TextButton play = new TextButton("Start Game",skin);
        TextButton help = new TextButton("How to Play",skin);
        TextButton highscore = new TextButton("Highscore", skin);
        TextButton exit = new TextButton("Quit",skin);


        // Add buttons to table
        table.add(titleLabel);
        table.row().pad(50, 0, 10, 0);
        table.add(play).width(help.getWidth()).height(help.getHeight());
        table.row().pad(10, 0, 10, 0);
        table.add(help).width(help.getWidth()).height(help.getHeight());
        table.row().pad(10, 0, 10, 0);
        table.add(highscore).width(help.getWidth()).height(help.getHeight());
        table.row().pad(10, 0, 10, 0);
        table.add(exit);

        // Action for exit button
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Action for highscore button:
        highscore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(InstattRunner.HIGHSCORE);
            }
        });

        // Action for help button
        help.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(InstattRunner.HELP);
            }
        });

        // Action for play button
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(InstattRunner.PLAY);
            }
        });

    }

    @Override
    public void render(float delta) {
        // Clear screen before start drawing the next screen
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // recalculate viewport each time window is resized
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        // skin disposed via asset manager
        backgroundTexture.dispose();
    }
}
