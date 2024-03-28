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

public class EndScreen implements Screen {
    // ScreenManager as Parent
    private ScreenManager parent;

    // Create Stage to store ui elements and skin for button skins
    private Stage stage;
    private Skin skin;

    // Image of background
    private Image backgroundImage;

    // Table to store ui elements in it and then only pass the table to stage
    private Table table;
    
    // Store highscore value
    private int highscore;


    public EndScreen(ScreenManager screenManager) {
        parent = screenManager;

        OrthographicCamera gameCam = new OrthographicCamera();
        stage = new Stage(new FitViewport(parent.VIEW_WIDTH, parent.VIEW_HEIGHT, gameCam));

        // Load skin using asset manager
        parent.assMan.queueAddSkin();
        parent.assMan.manager.finishLoading();
        skin = parent.assMan.manager.get(ConstHub.skinName);

        // Create Image from backgroundTexture from ScreenManager
        backgroundImage = new Image(parent.backgroundTexture);
        
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

        // Create table (which holds buttons) and set to fill whole screen (parent)
        table = new Table();
        table.setFillParent(true);
        // table.setDebug(true);

        // Create button and label
        Label l1 = new Label("Oh no you didn't make it in time!\nInstatt is locked again.", skin, "big");
        Label l2 = new Label("Your score: " + parent.finalScore, skin, "big");
        Label l3 = new Label("Your high score is " + highscore, skin, "big");

        // Button to go back to menu
        TextButton menu = new TextButton("Back to Menu", skin);

        // Action for menu button
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(ScreenManager.MENU);
            }
        });

        // Add labels and buttons to table
        table.add(l1).colspan(2);
        table.row().pad(10, 0, 10, 0);
        table.add(l2).colspan(2);
        table.row().padTop(50);
        table.add(l3).colspan(2);
        table.row().padTop(50);
        table.add(menu).colspan(2);

        // Add table to stage
        stage.addActor(table);
    }


    @Override
    public void render(float delta) {
        // Clear screen before start drawing the next screen
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
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