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
import com.instattrunner.GameWorld;
import com.instattrunner.ScreenManager;
import com.instattrunner.controller.KeyboardController;
import com.instattrunner.loader.ConstHub;
import com.instattrunner.loader.IRAssetManager;

import java.util.Random;
import java.util.concurrent.TimeoutException;


// Screen which shows the game play
public class GameScreen implements Screen {
    // ScreenManager as Parent
    private ScreenManager parent;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private ConstHub locCHub;
    private GameWorld gameWorld;
    private SpriteBatch sb;
    private BitmapFont font = new BitmapFont();

    private Box2DDebugRenderer debugRenderer;
    private boolean debug = true; // tweak if want to debug
    
    // Declare Texture var for all Body in game
    private Texture floorTex;
    private Texture playerTex;
    private Texture bgTex;
    private Array<Texture> obTexs = new Array<Texture>();
    private Array<Texture> buffTexs = new Array<Texture>();
    private Array<Texture> debuffTexs = new Array<Texture>();

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




    public GameScreen(ScreenManager screenManager) {
        parent = screenManager;
        locCHub = parent.constHub;

        cam = new OrthographicCamera(32, 24);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true,true, true);

        sb = new SpriteBatch();
        sb.setProjectionMatrix(cam.combined);

        controller = new KeyboardController();

        // Reset all constHub's values changed
        locCHub.playerScale = locCHub.REFplayerScale;


        gameWorld = new GameWorld(controller, parent.assMan, this);
    
        parent.assMan.queueAddImages();
        parent.assMan.manager.finishLoading();

        // Gets images as Texture from asset manager (indivdual Texture for player and background)
        // Load images as Texture into array of Texture (obstacle, buff, debuff as there are multiple options)
        floorTex = parent.assMan.manager.get(locCHub.floorImageName);
        playerTex = parent.assMan.manager.get(locCHub.playerImageName);
        for (String obstacleImage : locCHub.obstacleImagesName)
            obTexs.add(parent.assMan.manager.get(obstacleImage));
        for (String buffImage : locCHub.buffImagesName)
            buffTexs.add(parent.assMan.manager.get(buffImage));
        for (String debuffImage : locCHub.debuffImagesName)
            debuffTexs.add(parent.assMan.manager.get(debuffImage));

        // Load width and heigth of player, obstacle, buff and debuff
        floorWidHei = locCHub.floorWidHei;
        playerWidHei = locCHub.playerWidHei;
        obstacleWidHei = locCHub.obstacleWidHei;
        buffWidHei = locCHub.buffWidHei;
        debuffWidHei = locCHub.debuffWidHei;

        // Load scale of category of Body
        obstacleScale = locCHub.obstacleScale;
        buffScale = locCHub.buffScale;
        debuffScale = locCHub.debuffScale;
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameWorld.logicStep(delta); // move game logic forward; use if to pause game

        if (debug) debugRenderer.render(gameWorld.world, cam.combined);

        /* START DRAWING */
        sb.begin();
 
        // Draw all objects
        // Draw player 
        sb.draw(playerTex, gameWorld.player.getPosition().x, model.player.getPosition().y, playerWidHei.x * assMan.playerScale, playerWidHei.y * assMan.playerScale);
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

        BitmapFont font = new BitmapFont(Gdx.files.internal("skin/score.fnt"));
        // Set the font size for the "Score" text
        font.getData().setScale(0.03f);
// Draw the "Score" text
        font.draw(sb, "Score", 5, 11);

        // Set the font size for the "Score" text
        font.getData().setScale(0.03f);
// Draw the "Score" text
        font.draw(sb, String.format("%04d", model.score), 6, 9);

// Set the font size for the "HighScore" text
        font.getData().setScale(0.03f);
        font.draw(sb, "H1ghscore", -10,11);

        font.getData().setScale(0.03f);
        font.draw(sb, String.format("%04d", highScore), -8,9);


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
