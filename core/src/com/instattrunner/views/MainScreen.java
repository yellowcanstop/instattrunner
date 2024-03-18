package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
import com.instattrunner.loader.IRAssetManager;

import java.util.Iterator;


// Screen which shows the game play
public class MainScreen implements Screen {
    private InstattRunner parent;
    IRModel model;
    OrthographicCamera cam;
    Box2DDebugRenderer debugRenderer;
    boolean debug = true; // tweak if want to debug
    KeyboardController controller;

    // Declare Texture var for all Body in game
    Texture floorTex;
    Texture playerTex;
    Texture bgTex;
    Array<Texture> obTexs = new Array<Texture>();
    Array<Texture> buffTexs = new Array<Texture>();
    Array<Texture> debuffTexs = new Array<Texture>();
  
    SpriteBatch sb;
    BitmapFont font = new BitmapFont();

    // Declare array to store width and height of different player, obstacle, buff and debuff
    private Vector2 floorWidHei;
    private Vector2 playerWidHei;
    private Vector2[] obstacleWidHei;
    private Vector2[] buffWidHei;
    private Vector2[] debuffWidHei; 

    // Scale of category of body
    private final float obstacleScale;
    private final float buffScale;
    private final float debuffScale;

    // Determines how many milli second has to pass to spawn new obstacle/buff/debuff
    public long spawnInterval = 2000;



    public MainScreen(InstattRunner instattRunner) {
        parent = instattRunner;

        IRAssetManager assMan;   // Yes, I did it on purpose (I just followed the tutorial, not my fault :) )
        assMan = parent.assetMan;

        cam = new OrthographicCamera(32, 24);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true,true, true);

        sb = new SpriteBatch();
        sb.setProjectionMatrix(cam.combined);

        controller = new KeyboardController();
        model = new IRModel(controller, cam, assMan, this);
    
        assMan.queueAddImages();
        assMan.manager.finishLoading();


        // Load name of obstacle, buff, debuff image files from asset manager
        String[] obstacleImages = assMan.obstacleImages;
        String[] buffImages = assMan.buffImages;
        String[] debuffImages = assMan.debuffImages;

        // Gets images as Texture from asset manager (indivdual Texture for player and background)
        // Load images as Texture into array of Texture (obstacle, buff, debuff as there are multiple options)
        floorTex = assMan.manager.get(assMan.floorImage);
        playerTex = assMan.manager.get(assMan.playerImage);
        bgTex = assMan.manager.get("images/bg.jpg");
        for (String obstacleImage : obstacleImages)
            obTexs.add(assMan.manager.get(obstacleImage));
        for (String buffImage : buffImages)
            buffTexs.add(assMan.manager.get(buffImage));
        for (String debuffImage : debuffImages)
            debuffTexs.add(assMan.manager.get(debuffImage));

        // Load width and heigth of player, obstacle, buff and debuff
        floorWidHei = assMan.floorWidHei;
        playerWidHei = assMan.playerWidHei;
        obstacleWidHei = assMan.obstacleWidHei;
        buffWidHei = assMan.buffWidHei;
        debuffWidHei = assMan.debuffWidHei;

        // Load scale of category of Body
        obstacleScale = assMan.obstacleScale;
        buffScale = assMan.buffScale;
        debuffScale = assMan.debuffScale;
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

        /* START DRAWING */
        sb.begin();
 
        // Draw all objects
        // Draw player 
        sb.draw(playerTex, model.player.getPosition().x, model.player.getPosition().y, playerWidHei.x * parent.assetMan.playerScale, playerWidHei.y * parent.assetMan.playerScale);
        // Draw floor
        sb.draw(floorTex, model.floor.getPosition().x - (floorWidHei.x / 2), model.floor.getPosition().y - (floorWidHei.y / 2), floorWidHei.x, floorWidHei.y);
        // Draw all obstacles, buffs, debuffs
        loopDraw(model.obstacles, obTexs, obstacleWidHei, obstacleScale);
        loopDraw(model.buffs, buffTexs, buffWidHei, buffScale);
        loopDraw(model.debuffs, debuffTexs, debuffWidHei, debuffScale);


        font.getData().setScale(0.05f);
        font.draw(sb, "Score: " + model.score, 12, 10);


        // have to change as this is not how its supposed to work 
        // Spawn obstacle based on speed var determiner 
        if(TimeUtils.millis() - model.obstacleTime > spawnInterval) 
            model.spawnObstacles(model.regular);
   
        model.trackObstacles();

        // Randomly choose to spawn buff or debuff every 2 seconds 
        // Type of buff/debuff will be randomly choosen by .create method in IRModel
        int choice = MathUtils.random(1); // 0 or 1
        if (choice == 0) 
            if (TimeUtils.millis() - model.buffTime > spawnInterval) model.spawnBuffs();
        else 
            if(TimeUtils.millis() - model.buffTime > spawnInterval) model.spawnDebuffs();
        model.trackBuffsDebuffs();

   

        sb.end();

        if (model.isDead) {
            if (model.immunity){
                model.removeCollidedObstacle();
                model.resetImmune();
            }

            else {
                parent.finalScore = model.score;
                parent.changeScreen(InstattRunner.END);
            }

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
        for (Texture obTex : obTexs)
            obTex.dispose();
        for (Texture buffTex : buffTexs)
            buffTex.dispose();
        for (Texture debuffTex : debuffTexs)
            debuffTex.dispose();
        sb.dispose();
    }

    // Just trying to reduce repeated code
    private void loopDraw(Array<Body> bodys, Array<Texture> bodyTexs, Vector2[] bodyWidHei, float bodyScale) {
        int tempTextureId;
        Vector2 tempWidHei;

        for (Body body : bodys) {
            // .getTextureId return texture id of particular model and use it as index on the texture array 
            // .getPosition returns bottom left coord as these are complex polygon (only floor .getPosition return center)
            tempTextureId = model.getTextureId(body);
            tempWidHei = bodyWidHei[tempTextureId];
            sb.draw(bodyTexs.get(tempTextureId), body.getPosition().x, body.getPosition().y, tempWidHei.x* bodyScale, tempWidHei.y * bodyScale);
        }
   }
}
