package com.instattrunner.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.BodyData;
import com.instattrunner.IRModel;
import com.instattrunner.InstattRunner;
import com.instattrunner.controller.KeyboardController;
import com.instattrunner.loader.IRAssetManager;

import java.util.Random;
import java.util.concurrent.TimeoutException;


// Screen which shows the game play
public class MainScreen implements Screen {
    private InstattRunner parent;
    IRModel model;
    OrthographicCamera cam;
    Box2DDebugRenderer debugRenderer;
    boolean debug = true; // tweak if want to debug
    KeyboardController controller;
    IRAssetManager assMan;   // Yes, I did it on purpose (I just followed the tutorial, not my fault :) )

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
    public long minSpawnInterval = 1200;
    public long obstacleSpawnInterval = minSpawnInterval;
    public long buffSpawnInterval = minSpawnInterval * 2;
    Random random = new Random(TimeUtils.millis());
    private int highScore;



    private float tempScale = 0.005f;




    public MainScreen(InstattRunner instattRunner) {
        parent = instattRunner;

        // For now oonly
        assMan = new IRAssetManager();

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
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        model.logicStep(delta); // move game logic forward; use if to pause game

        if (debug) debugRenderer.render(model.world, cam.combined);

        /* START DRAWING */
        sb.begin();
 
        // Draw all objects
        // Draw player 
        sb.draw(playerTex, model.player.getPosition().x, model.player.getPosition().y, playerWidHei.x * assMan.playerScale, playerWidHei.y * assMan.playerScale);
        // Draw floor
        sb.draw(floorTex, model.floor.getPosition().x - (floorWidHei.x / 2), model.floor.getPosition().y - (floorWidHei.y / 2), floorWidHei.x, floorWidHei.y);
        // Draw all obstacles, buffs, debuffs
        loopDraw(model.obstacles, obTexs, obstacleWidHei, obstacleScale);
        loopDraw(model.buffs, buffTexs, buffWidHei, buffScale);
        loopDraw(model.debuffs, debuffTexs, debuffWidHei, debuffScale);


        float tempLocBuff = model.floor.getPosition().x - (floorWidHei.x / 2) + 0.5f;
        float tempLocDebuff = model.floor.getPosition().x - (floorWidHei.x / 2) + 0.5f;
        for (int i = 0; i < 4; i++){
            if (model.buffActive[i]){
                if (buffWidHei[i].x > buffWidHei[i].y)
                    sb.draw(buffTexs.get(i), tempLocBuff, 9.8f, 1.5f, 1.5f / buffWidHei[i].x * buffWidHei[i].y);
                else
                    sb.draw(buffTexs.get(i), tempLocBuff, 9.5f, 2 / buffWidHei[i].y * buffWidHei[i].x, 2);
                tempLocBuff += 2f;
            }
            if (model.debuffActive[i]){
                if (debuffWidHei[i].x > debuffWidHei[i].y)
                    sb.draw(debuffTexs.get(i), tempLocDebuff,7.1f, 1.5f, 1.5f / debuffWidHei[i].x * debuffWidHei[i].y);
                else
                    sb.draw(debuffTexs.get(i), tempLocDebuff, 7.1f, 2 / debuffWidHei[i].y * debuffWidHei[i].x, 2);
                tempLocDebuff += 2f;
            }

        }

        // System.out.printf("  Buff : %b   %b   %b   %b\n", model.buffActive[0], model.buffActive[1], model.buffActive[2], model.buffActive[3]);
        // System.out.printf("Debuff : %b   %b   %b   %b\n\n", model.debuffActive[0], model.debuffActive[1], model.debuffActive[2], model.debuffActive[3]);


        // Set the font size for the "Score" text
        font.getData().setScale(0.12f);
        // Draw the "Score" text
        font.draw(sb, "Score: " + model.score, 4, 11);
        // Set the font size for the "HighScore" text
        font.getData().setScale(0.1f);
        // Estimate the average width of characters in the font
        float averageCharWidth = font.getCapHeight() * 0.4f; // Adjust as needed
        // Estimate the width of the "HighScore" text based on the number of characters
        float highScoreTextWidth = ("HighScore: " + highScore).length() * averageCharWidth; // Adjust as needed
        // Calculate the x-coordinate for the "HighScore" text to prevent overlapping
        float highScoreX = -1 + highScoreTextWidth; // Adjust as needed
        // Calculate the y-coordinate for the "HighScore" text
        float highScoreY = 8 + font.getLineHeight(); // Adjust as needed
        // Draw the "HighScore" text
        font.draw(sb, "HighScore" + highScore, highScoreX,highScoreY);



        // Spawn obstacle based on speed var determiner 
        if(TimeUtils.timeSinceMillis(model.obstacleTime) > obstacleSpawnInterval) 
            model.spawnObstacles(model.regular);
   
        model.trackObstacles();

        // Randomly choose to spawn buff or debuff every 2 seconds 
        // Type of buff/debuff will be randomly choosen by .create method in IRModel
        int choice = random.nextInt(2);
        if (TimeUtils.timeSinceMillis(model.buffTime) > buffSpawnInterval){
            if (choice == 0) 
                model.spawnBuffs();
            else if (choice == 1)
                model.spawnDebuffs();
        }
        model.trackBuffsDebuffs();

        sb.end();

        highScore = loadTextFile();

        if (model.isDead) {
            if (model.immunity){
                model.removeCollidedObstacle();
                model.resetImmune();
            }

            else {
                if (highScore < model.score) {
                    highScore = model.score;
                    System.out.println("new high score obtain");
                    System.out.println(highScore);
                    updateHighScore(highScore);
                }
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


    // both to be removed
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
    private void updateHighScore(int newHighScore) {
        FileHandle fileHandle = Gdx.files.local("score/HighScore.txt");
        String stringHighScore = Integer.toString(newHighScore);
        fileHandle.writeString(stringHighScore, false);

        System.out.println("highscoreadded");
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
