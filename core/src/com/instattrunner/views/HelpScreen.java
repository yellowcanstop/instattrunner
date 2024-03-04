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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.instattrunner.InstattRunner;

// Screen which shows "How to Play"
public class HelpScreen implements Screen {
    private InstattRunner parent;
    private Stage stage;
    private Skin skin;


    // Constructor with reference to parent passed in
    public HelpScreen(InstattRunner instattRunner) {
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

        // Create buttons
        skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));
        Label titleLabel = new Label("How to Play", skin);

        // todo: render text about instructions, buffs/debuffs
        Label i1 = new Label("Spacebar to jump", skin);
        Label i2 = new Label("Once hit obstacle, die", skin);

        Label b1 = new Label("Collect buffs and debuffs", skin);
        Label b2 = new Label("which are represented by students of other majors", skin);
        Label b3 = new Label("See detailed description", skin);



        // Go back to menu
        // todo: make back button small
        TextButton menu = new TextButton("Back to Menu", skin);
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(InstattRunner.MENU);
            }
        });

        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 10, 0);

        table.row().pad(10, 0, 10, 0);

        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 10, 0);
        table.add(i1).colspan(2);
        table.row().padTop(10);
        table.add(i2).colspan(2);
        table.row().padTop(40);
        table.add(b1).uniformX().align(Align.left);
        table.add(b2).uniformX().align(Align.left);
        table.add(b3).uniformX().align(Align.left);
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
