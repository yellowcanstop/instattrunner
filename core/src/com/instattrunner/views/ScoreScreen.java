package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.instattrunner.ScoreManager;
import com.instattrunner.ScreenManager;
import com.instattrunner.loader.ConstHub;


public class ScoreScreen implements Screen {
    // ScreenManager as Container 
    private ScreenManager container;

    // Create Stage to store ui elements and skin for button skins
    private Stage stage;
    private Skin skin;

    // Image of background
    private Image backgroundImage;

    // Table to store ui elements in it and then only pass the table to stage
    private Table table;

    // Store highscore value
    private int highscore;


    public ScoreScreen(ScreenManager screenManager) {
        container = screenManager;

        OrthographicCamera gameCam  = new OrthographicCamera();
        stage = new Stage(new FitViewport(container.VIEW_WIDTH, container.VIEW_HEIGHT, gameCam));

        // Load skin using asset manager
        container.assMan.queueAddSkin();
        container.assMan.manager.finishLoading();
        skin = container.assMan.manager.get(ConstHub.skinName);

        // Create Image from backgroundTexture from ScreenManager
        backgroundImage = new Image(container.backgroundTexture);

        // Call loadTextFile method in scoreManager to retrieve highscore from file and store to local highscore variable
        highscore = ScoreManager.loadTextFile();
    }


    @Override
    public void show() {
        // Set the background image
        backgroundImage.setFillParent(true);
        stage.clear();
        stage.addActor(backgroundImage);

        // Push input to stage
        Gdx.input.setInputProcessor(stage);

        // Add table (which holds buttons) to the stage
        table = new Table();
        table.setFillParent(true);
        // table.setDebug(true);


        // Create labels
        Label titleLabel = new Label("High Score", skin,"big");
        Label i1 = new Label("" + highscore, skin,"big");

        // Create Text Buttons to go back to menu
        TextButton menu = new TextButton("Back to Menu", skin);

        // Action for menu button
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                container.changeScreen(ScreenManager.MENU);
            }
        });

        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 10, 0);
        table.row().pad(10, 0, 10, 0);
        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 10, 0);
        table.add(i1).colspan(2);
        table.row().padTop(10);
        table.add(menu).colspan(2);

        stage.addActor(table);
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
        skin.dispose();
    }
}