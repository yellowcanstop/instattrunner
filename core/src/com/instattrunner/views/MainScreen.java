package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.IRModel;
import com.instattrunner.InstattRunner;
import com.instattrunner.controller.KeyboardController;


// Screen which shows the game play
public class MainScreen implements Screen {
    private InstattRunner parent;
    IRModel model;
    OrthographicCamera cam;
    Box2DDebugRenderer debugRenderer;
    boolean debug = true; // flag for debug
    KeyboardController controller;
    Texture playerTex;
    Texture bgTex;
    Texture obTex;
    SpriteBatch sb;

    // Constructor with reference to parent passed in
    public MainScreen(InstattRunner instattRunner) {
        parent = instattRunner;
        cam = new OrthographicCamera(32, 24);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true,true, true);

        parent.assetMan.queueAddImages(); // tells AM we want to load images
        parent.assetMan.manager.finishLoading(); // tells AM to load images and wait until finished loading
        playerTex = parent.assetMan.manager.get("images/droplet.png"); // gets images as a texture
        bgTex = parent.assetMan.manager.get("images/bg.jpg");

        sb = new SpriteBatch();
        // tell SpriteBatch we are using OrthographicCamera with the 32x24 screen size
        sb.setProjectionMatrix(cam.combined);

        controller = new KeyboardController();
        model = new IRModel(controller, cam, parent.assetMan); // allow model to access controller



    }


    @Override
    public void show() {
        // set controller as the class which processes inputs
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        model.logicStep(delta); // move game logic forward; use if to pause game
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (debug) debugRenderer.render(model.world, cam.combined);

        // use sprite batch to draw playerTex on screen
        sb.begin();
        // player only 2 units wide so set width and height at 2
        // sb draws images from the corner vs box2d bodies positioned centre
        // have to position texture 1/2 the width to left and 1/2 the height down
        // todo: need to store the player class in the body's userdata
        // which will contain the size, and use hat to get correct offset needed (not just -1)
        sb.draw(playerTex, model.player.getPosition().x-2, model.player.getPosition().y-1, 3, 3);
        //sb.draw(bgTex, 0, 0, cam.viewportWidth, cam.viewportHeight);



        if(TimeUtils.millis() - model.lastTime > 3000) model.spawnObstacles();




        sb.end();

        // if player is dead, show end screen
        if (model.isDead) {
            parent.finalScore = model.score;
            parent.changeScreen(InstattRunner.END);
            model.isDead = false;
        }
    }

    @Override
    public void resize(int width, int height) {

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
        playerTex.dispose();
        bgTex.dispose();
        obTex.dispose();
        sb.dispose();
    }
}
