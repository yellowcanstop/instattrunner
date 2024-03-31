package com.instattrunner;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.bodies.Buff;
import com.instattrunner.bodies.Debuff;
import com.instattrunner.bodies.Floor;
import com.instattrunner.bodies.Obstacle;
import com.instattrunner.bodies.Player;
import com.instattrunner.controller.KeyboardController;
import com.instattrunner.loader.ConstHub;
import com.instattrunner.loader.GameAssetManager;
import com.instattrunner.views.GameScreen;

import java.util.Random;

// Controls all logic in game
public class GameWorld {
    private GameScreen container;
    public World world;
    public KeyboardController controller;
    public GameAssetManager assMan;
    public CollisionListener collisionListener;

    // Random generator
    public Random random = new Random(TimeUtils.millis());

    // Bodies (yes bodies, just not human bodies, although we have a player BODY)
    public Body player;
    public Body smallPlayer;    // Pre-build as very computationaly intensive, tends to crash game when done during run time
    public Body regularPlayer;
    public Body bigPlayer;
    public Body floor;
    public Array<Body> obstacles = new Array<Body>();
    public Array<Body> buffs = new Array<Body>();
    public Array<Body> debuffs = new Array<Body>();
    public Body collideDeBuff;
    public Body collideObstacle;

    // Declare different type of Body objects in order to access their methods
    private Floor floorClass;
    private Player playerClass;
    public Obstacle obstacleClass;
    public Buff buffClass;
    public Debuff debuffClass;

    // Declare different support classes to access their methods
    private SpawnNTrack spawnNTrackClass;
    public BuffDebuffEffects buffDebuffEffectsClass;
    public JumpController jumpControllerClass;
    
    // Timestamps and spawnInterval
    public long renderMinSpawnInterval = ConstHub.regularMinSpawnInterval;    // Determines how many milli second has to pass to spawn new obstacle/buff/debuff
    public long obstacleSpawnInterval = renderMinSpawnInterval;    //Obstacle and buff/debuff set to min and four times of min during init
    public long buffDebuffSpawnInterval = renderMinSpawnInterval * 4;    //Changed to random within range everytime new obstacle/buff/debuff spawn
    public long obstacleTimestamp = TimeUtils.millis();    // Time since last obstacle spawn
    public long buffDebuffTimestamp = TimeUtils.millis();    // Time since last buff/debuff spawn
    

    // Vars for environment
    public boolean isDead = false;
    public int score = 0;
    public int velocityIncrement = 0;
    private int highscore = ScoreManager.loadTextFile();

    // Run time decided variables (moved over from ConstHub to make only const in ConstHub)
    // Will be refered by multiple classes for read and write 
    public float renderPlayerScale = ConstHub.regularPlayerScale;
    public float renderSpeed = ConstHub.regularSpeed;
    public int renderLowJump = ConstHub.regularLowJump;
    public int renderNormalJump = ConstHub.regularNormalJump;
    public int renderHighJump = ConstHub.regularHighJump;




    // // ArrayList for spawn randomization
    // private ArrayList<Integer> buffSpawnUnused = new ArrayList<>(Arrays.asList(0,1,2,3));
    // private ArrayList<Integer> buffSpawnUsed = new ArrayList<>();
    // private ArrayList<Integer> debuffSpawnUnused = new ArrayList<>(Arrays.asList(0,1,2));
    // private ArrayList<Integer> debuffSpawnUsed = new ArrayList<>();


    // Contructor
    // world to keep all physical objects in the game
    public GameWorld(KeyboardController cont, GameAssetManager assetMan, GameScreen gameScreen) {
        System.out.println("New Model Created.");

        controller = cont;
        container = gameScreen;
        assMan = assetMan;
        world = new World(new Vector2(0, -60f), true);
        collisionListener = new CollisionListener(this);
        world.setContactListener(collisionListener);

        // Init different type of Body classes
        floorClass = new Floor(this);
        playerClass = new Player(this);
        obstacleClass = new Obstacle(this);
        buffClass = new Buff(this);
        debuffClass = new Debuff(this);

        spawnNTrackClass = new SpawnNTrack(this);
        buffDebuffEffectsClass = new BuffDebuffEffects(this);
        jumpControllerClass = new JumpController(this);

        collideDeBuff = null;
        
        // Create floor and player of game 
        floorClass.createFloor();
        playerClass.createPlayer();
    }


    // todo ensure player cannot jump outside of view
    // logic method to run logic part of the model
    public void logicStep(float delta) {
        // Run spawnLogic to spawn obstacle/buff/debuff if conditions met
        spawnLogic();

        // Call tracking method to check whether obstacle/buff/debuff are out of screen
        // If true, remove (obstacle will also increment score by 1 
        spawnNTrackClass.trackObstacles();
        spawnNTrackClass.trackBuffsDebuffs();

        // Check if exists collided buff/debuff, if true, remove
        if (collideDeBuff != null)
            removeCollidedDeBuff();

        buffDebuffEffectsClass.checkBuffDebuffExpire();
        buffDebuffEffectsClass.checkBuffDebuffPairs();
        buffDebuffEffectsClass.activateBuffDebuffEffect();

        jumpControllerClass.jumpLogic();
        
        endGameLogic();

        world.step(delta, 3, 3); // tell Box2D world to move forward in time
    }


    private void spawnLogic() {
        // Spawn obstacle based on speed var determiner 
        if(TimeUtils.timeSinceMillis(obstacleTimestamp) > obstacleSpawnInterval) 
            spawnNTrackClass.spawnObstacles(renderSpeed - velocityIncrement);
            
        // Randomly choose to spawn buff or debuff  
        // Type of buff/debuff will be randomly choosen by .create method in GameWorld
        int choice = random.nextInt(2);
        if (TimeUtils.timeSinceMillis(buffDebuffTimestamp) > buffDebuffSpawnInterval){
            if (choice == 0) 
                spawnNTrackClass.spawnBuffs();
            else if (choice == 1)
                spawnNTrackClass.spawnDebuffs();
        }
    }


    private void endGameLogic() {
        if (isDead) {
            if (buffDebuffEffectsClass.immunity) {
                removeCollidedObstacle();
                buffDebuffEffectsClass.resetImmune();
            }

            else {
                if (highscore < score) {
                    highscore = score;
                    System.out.print("new high score obtain  :  ");
                    System.out.println(highscore);
                    ScoreManager.updateHighScore(highscore);
                }
                container.container.finalScore = score;
                container.container.changeScreen(ScreenManager.END);
            }

            isDead = false;
        }
    }


    public void passThrough(Body bod) {
        for (Fixture fix : bod.getFixtureList()) {
            fix.setSensor(true);
        }
    }
 

    public void removeCollidedObstacle(){
        collideObstacle.setTransform(-27f, collideObstacle.getPosition().y, collideObstacle.getAngle());
    }


    public void removeCollidedDeBuff(){
        collideDeBuff.setTransform(-27f, collideDeBuff.getPosition().y, collideDeBuff.getAngle());
        collideDeBuff = null;
    }
}