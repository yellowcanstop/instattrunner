package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.instattrunner.ScreenManager;
import com.instattrunner.loader.ConstHub;

/* displays the menu screen to the user
for them to decide what action to take next
 */
public class MenuScreen implements Screen {
    // ScreenManager as Parent
    private ScreenManager parent;

    // Create Stage to store ui elements and skin for button skins
    private Stage stage;
    private Skin skin;

    // Image of background
    private Image backgroundImage;

    // Table to store ui elements in it and then only pass the table to stage
    private Table table;


    public MenuScreen(ScreenManager screenManager) {
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

        // should not need
        // stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        // stage.draw();

        // Add table (which holds buttons) to the stage
        table = new Table();
        table.setFillParent(true);
        table.setDebug(true);

        // Create LabelStyle for labels
        LabelStyle bigLabelStyle = skin.get("big", LabelStyle.class);
        bigLabelStyle.font.getData().setScale(2); // Set font scale to 2, making it twice as big

        // Create labels and buttons
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

        // Action for play button
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(ScreenManager.PLAY);
            }
        });

        // Action for help button
        help.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(ScreenManager.HELP);
            }
        });
 
        // Action for highscore button:
        highscore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(ScreenManager.SCORE);
            }
        });

        // Action for exit button
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

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
