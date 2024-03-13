package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.BodyData;
import com.instattrunner.IRModel;
import com.instattrunner.InstattRunner;
import com.instattrunner.controller.KeyboardController;

import java.util.Iterator;


// Screen which shows the game play
public class MainScreen implements Screen {
    private InstattRunner parent;
    IRModel model;
    OrthographicCamera cam;
    Box2DDebugRenderer debugRenderer;
    boolean debug = true; // tweak if want to debug
    KeyboardController controller;
    public final String[] obstacleImages = {"pic/Cat.png", "pic/Goose.png", "pic/Lake.png", "pic/Stairs.png"};
    Texture playerTex;
    Texture bgTex;
    Array<Texture> obTex = new Array<Texture>();
    Texture buffTex;
    Texture debuffTex;
    SpriteBatch sb;
    BitmapFont font = new BitmapFont();


    public MainScreen(InstattRunner instattRunner) {
        parent = instattRunner;
        cam = new OrthographicCamera(32, 24);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true,true, true);

        parent.assetMan.queueAddImages();
        parent.assetMan.manager.finishLoading();


        playerTex = parent.assetMan.manager.get("images/droplet.png");
<<<<<<< HEAD
        
        for (String obstacleImage : obstacleImages)
            obTex.add(parent.assetMan.manager.get(obstacleImage));
        
        buffTex = parent.assetMan.manager.get("pic/Coffee.png");
        debuffTex = parent.assetMan.manager.get("pic/Alcohol.png");
=======
        obTex = parent.assetMan.manager.get("images/bucket.png");
        buffTex = parent.assetMan.manager.get("images/buff.png");
        debuffTex = parent.assetMan.manager.get("images/debuff.png");
>>>>>>> dc7280a68d88e04c1b201f3543490e9e22b50db6
        bgTex = parent.assetMan.manager.get("images/bg.jpg");

        sb = new SpriteBatch();
        sb.setProjectionMatrix(cam.combined);

        controller = new KeyboardController();
        model = new IRModel(controller, cam, parent.assetMan);

    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        model.logicStep(delta); // move game logic forward; use if to pause game

        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //sb.setProjectionMatrix(StatusBar.stage.getCamera().combined);
        //StatusBar.stage.draw();

        if (debug) debugRenderer.render(model.world, cam.combined);

        sb.begin();
        /*
        // player only 2 units wide so set width and height at 2
        // sb draws images from the corner vs box2d bodies positioned centre
        // have to position texture 1/2 the width to left and 1/2 the height down
        // todo: need to store the player class in the body's userdata?
        // which will contain the size, and use hat to get correct offset needed (not just -1)

         */

        sb.draw(playerTex, model.player.getPosition().x-2, model.player.getPosition().y-1, 3, 3);



        for (Iterator<Body> iter = model.obstacles.iterator(); iter.hasNext(); ) {
            Body obstacle = iter.next();
            sb.draw(obTex.get(((BodyData) obstacle.getUserData()).texture_id), obstacle.getPosition().x-2, obstacle.getPosition().y-1, 3, 3);
        }


        for (Iterator<Body> iter = model.buffs.iterator(); iter.hasNext(); ) {
            Body buff = iter.next();
            sb.draw(buffTex, buff.getPosition().x-2, buff.getPosition().y-1, 3, 3);
        }


        for (Iterator<Body> iter = model.debuffs.iterator(); iter.hasNext(); ) {
            Body debuff = iter.next();
            sb.draw(debuffTex, debuff.getPosition().x-2, debuff.getPosition().y-1, 3, 3);
        }





        font.getData().setScale(0.05f);
        font.draw(sb, "Score: " + model.score, 12, 10);

        if(TimeUtils.millis() - model.lastTime > 2000) {
            if (model.speedUp) {
                model.spawnObstacles(model.fast);
            }
            else {
                model.spawnObstacles(model.regular);
            }
        }

        model.trackObstacles();

        int choice = MathUtils.random(1); // 0 or 1
        if (choice == 0) {
            if (TimeUtils.millis() - model.buffTime > 2000) model.spawnBuffs();
        }
        else {
            if(TimeUtils.millis() - model.buffTime > 2000) model.spawnDebuffs();
        }



        sb.end();


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
        for (Texture obstacle : obTex)
            obstacle.dispose();
        sb.dispose();
    }
}
