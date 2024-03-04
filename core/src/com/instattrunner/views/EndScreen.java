package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.instattrunner.InstattRunner;

public class EndScreen implements Screen {
    private InstattRunner parent;
    private Stage stage;
    private Skin skin;

    public EndScreen(InstattRunner instattRunner) {
        parent = instattRunner;
        stage = new Stage(new ScreenViewport()); // Stage is the controller to react to user input
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Add table (which holds buttons) to the stage
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);


        // Create button and label
        skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));
        Label l1 = new Label("Oh no you didn't make it in time!\nInstatt is locked again.", skin);
        Label l2 = new Label("lol i guess you managed to score " + parent.finalScore + " tho...", skin);

        // Go back to menu
        TextButton menu = new TextButton("Back to Menu", skin);
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(InstattRunner.MENU);
            }
        });

        table.add(l1).colspan(2);
        table.row().pad(10, 0, 10, 0);
        table.add(l2).colspan(2);
        table.row().padTop(50);
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
        // skin disposed via asset manager
    }
}