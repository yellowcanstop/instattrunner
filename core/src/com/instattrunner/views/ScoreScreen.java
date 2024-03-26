package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.instattrunner.InstattRunner;
import com.instattrunner.ScreenManager;
import com.instattrunner.loader.ConstHub;

public class ScoreScreen implements Screen {
    // ScreenManager as Parent 
    private ScreenManager parent;
 
    // Create Stage to store ui elements and skin for button skins
    private Stage stage;
    private Skin skin;

    // Image of background
    private Image backgroundImage;

    // Table to store ui elements in it and then only pass the table to stage
    private Table table;


    public ScoreScreen(ScreenManager screenManager) {
        parent = screenManager;

        OrthographicCamera gameCam  = new OrthographicCamera();
        stage = new Stage(new FitViewport(parent.VIEW_WIDTH, parent.VIEW_HEIGHT, gameCam));

        // Load skin using asset manager
        parent.assMan.queueAddSkin();
        parent.assMan.manager.finishLoading();
        skin = parent.assMan.manager.get(parent.constHub.skinName);

        // Create Image from backgroundTexture from ScreenManager
        backgroundImage = new Image(parent.backgroundTexture);
    }


    @Override
    public void show() {
        // Set the background image
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Push input to stage
        Gdx.input.setInputProcessor(stage);
        // Should not need
        // stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        // stage.draw();

        // Add table (which holds buttons) to the stage
        table = new Table();
        table.setFillParent(true);
        table.setDebug(true);

        highScore = loadTextFile();

        // Create labels
        Label titleLabel = new Label("High Score", skin,"big");
        Label i1 = new Label("" + highScore, skin,"big");

        // Create Text Buttons to go back to menu
        TextButton menu = new TextButton("Back to Menu", skin);

        // Action for menu button
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(ScreenManager.MENU);
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
    }



    int highScore;
    public int loadTextFile(){
        // Load the file using a FileHandle
        FileHandle fileHandle = Gdx.files.internal("score/HighScore.txt");

        // Read the contents of the file into a String
        String highScoreString = fileHandle.readString();

        int score=0;
        // Parse the String to an integer
        try {
            score = Integer.parseInt(highScoreString.trim());
        } catch (NumberFormatException e) {
            // Handle parsing error (e.g., file contents are not a valid integer)
            e.printStackTrace();
        }

        return score;
    }
}
