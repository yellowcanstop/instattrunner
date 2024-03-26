package com.instattrunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnEllipseSide;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

// Controls all logic in game
public class GameWorld {
    public World world;
    private KeyboardController controller;
    private GameScreen gameScreen;
    private GameAssetManager assMan;
    public ConstHub locCHub;

    // Random generator
    public Random random = new Random(TimeUtils.millis());

    // Bodies (yes bodies, just not human bodies, although we have a player BODY)
    public Body player;
    public Body regularPlayer;
    public Body smallPlayer;    // Pre-build as very computationaly intensive, tends to crash game when done during run time
    public Body bigPlayer;
    public Body floor;
    public Body collideObstacle;
    public Body collideDeBuff;
    public Array<Body> obstacles = new Array<Body>();
    public Array<Body> buffs = new Array<Body>();
    public Array<Body> debuffs = new Array<Body>();

    // Declare different type of Body objects in order to access their methods
    private Floor floorClass;
    private Player playerClass;
    private Obstacle obstacleClass;
    private Buff buffClass;
    private Debuff debuffClass;


    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    // Declared here (only obstacle, buff, debuff) as repeatedly called and used (player is only used once, hence not here)
    private BodyEditorLoader buffLoader;
    private BodyEditorLoader debuffLoader;

    // ArrayList for spawn randomization
    private ArrayList<Integer> obstacleSpawnUnused = new ArrayList<>(Arrays.asList(0,1,2,3));
    private ArrayList<Integer> obstacleSpawnUsed = new ArrayList<>();
    private ArrayList<Integer> buffSpawnUnused = new ArrayList<>(Arrays.asList(0,1,2,3));
    private ArrayList<Integer> buffSpawnUsed = new ArrayList<>();
    private ArrayList<Integer> debuffSpawnUnused = new ArrayList<>(Arrays.asList(0,1,2));
    private ArrayList<Integer> debuffSpawnUsed = new ArrayList<>();

    // Declare object Sound to store sound loaded from asset manager
    private Sound jump;
    private Sound collect;

    // enum for sound 
    public static final int JUMP_SOUND = 0;
    public static final int COLLECT_SOUND = 1;

    // Vars for environment
    public long obstacleTime = TimeUtils.millis() - 1200;    // Time since last obstacle spawn
    public long buffTime = TimeUtils.millis() - 1200;    // Time since last buff/debuff spawn
    public boolean isDead = false;
    public boolean immunity = false;
    public int score = 0;
    public int velocityIncrement = 0;


    // Enum for obstacle, buff, debuff
    // buff/debuff category
    private static final int SPEED = ConstHub.BUSINESS_MAN_1_AI;
    private static final int SIZE = ConstHub.NUTRITION_MAJOR;
    private static final int JUMP = ConstHub.COFFEE;
    private static final int IMMUNE = ConstHub.DEAN;
    // buff
    private static final int BUSINESS_MAN_1_AI = ConstHub.BUSINESS_MAN_1_AI;
    private static final int NUTRITION_MAJOR = ConstHub.NUTRITION_MAJOR;
    private static final int COFFEE = ConstHub.COFFEE;
    private static final int DEAN = ConstHub.DEAN;
    // debuff
    private static final int SPORTS_SCIENCE_MAJOR = ConstHub.SPORTS_SCIENCE_MAJOR;
    private static final int CULINARY_MAJOR = ConstHub.CULINARY_MAJOR;
    private static final int BEER = ConstHub.BEER;

    /* Individual Buff Debuff
    * variables are declared here in logic model,
    * variables are edited in contact listener,
    * effects are activated and processed deactivated after x seconds in logic model (sorry, had to change to logic model as i'll be using the Body(s) in main screen, didn't feel like importing again to contact listener)
    */
    public long[] effectTime = new long[4];            // effect(buff and debuff of same category) start time
    public boolean[] effectActive = new boolean[4];    // effect(buff and debuff of same category) active or not 
    public boolean[] buffActive = new boolean[4];      // whether buff is active or not 
    public boolean[] debuffActive = new boolean[4];    // whether debuff is active or not (last one is a place holder to counter Dean buff)

    // enum for jump
    public int NORMAL = 115;
    public int HIGH = 135;
    public int LOW = 73;

    // tweak speed of obstacles
    /* Sports: obstacles move faster; Biz: obstacles move slower; Otherwise: regular */
    public float regular = -20f;
    public float fast = -40f;
    public float slow = -5f;


    // Contructor
    // world to keep all physical objects in the game
    public GameWorld(KeyboardController cont, GameAssetManager assetMan, GameScreen gScreen) {
        System.out.println("New Model Created.");

        controller = cont;
        gameScreen = gScreen;
        assMan = assetMan;
        locCHub = gScreen.locCHub;
        world = new World(new Vector2(0, -60f), true);
        world.setContactListener(new CollisionListener(this));

        // Init different type of Body classes
        floorClass = new Floor(this);
        playerClass = new Player(this);

        // load sounds into model
        assMan.queueAddSounds();
        assMan.manager.finishLoading();
        jump = assMan.manager.get("sounds/drop.wav");
        collect = assMan.manager.get("sounds/drop.wav");

        // Init BodyEditorLoader


        //Load width and height of largest obstacle
        stairsWidHei = assMan.obstacleWidHei[3];
        
        
        // Load scale of body of different category
        playerScale = assMan.playerScale;
        obstacleScale = assMan.obstacleScale;
        buffScale = assMan.buffScale;
        debuffScale = assMan.debuffScale;

        collideDeBuff = null;

        
        // Create floor and player of game 
        floorClass.createFloor();
        playerClass.createPlayer();
    }




    // Variables for jump method
    private boolean canJump = true; // always true when player touches ground
    private boolean jumped = false;
    private int jumpCount = 0;

    public void resetJump() {
        canJump = true;
        jumped = false;
        jumpCount = 0;
    }

    private void tweakJump(int y) {
        if (player.getPosition().y < 9 && canJump && jumpCount < 5) {
            player.applyLinearImpulse(0, y, player.getWorldCenter().x, player.getWorldCenter().y, true);
            jumpCount++;
        }
        else if (player.getPosition().y > 9) {
            canJump = false;
        }
    }




    // todo ensure player cannot jump outside of view
    // logic method to run logic part of the model
    public void logicStep(float delta) {
        if (buffActive[COFFEE]) {
            if (controller.space) {
                jumped = true;
                tweakJump(HIGH);
            }
            else if (!controller.space && jumped) {
                canJump = false;
           }
        }
        if (debuffActive[BEER]) {
            if (controller.space) {
                jumped = true;
                tweakJump(LOW);
            }
            else if (!controller.space && jumped) {
                canJump = false;
           }
        }

        if (controller.space) {
            jumped = true;
            tweakJump(NORMAL);
        }
        else if (!controller.space && jumped){
            canJump = false;
            System.out.printf("Toggled canJump: %b  jumped: %b\n", canJump, jumped);
        }


        if (collideDeBuff != null)
            removeCollidedDeBuff();

        // for loop goes through all buff debuff category 
        // Check if they are expired, or both active
        // If found expired or both active(cancel each other), turn them off 
        for (int i = 0; i < 4; i++){
            // Would exists where in same category, exact expire and obtain new buff/debuff, new buff/debuff would be cancelled, let it slip as it would be very computationaly expensive to handle
            if (effectActive[i] && TimeUtils.timeSinceMillis(effectTime[i]) > 10000){
                buffActive[i] = false;
                debuffActive[i] = false;
                effectActive[i] = false;    
                effectCancellation(i);
            }
      
            if (buffActive[i] && debuffActive[i]){
                buffActive[i] = false;
                debuffActive[i] = false;
                effectActive[i] = false;    
                effectCancellation(i);
            }
        }

        // Move on to process active buff/debuff and enable their effects
        // Change speed of obstacle logic 
        if (effectActive[SPEED]){
            if (buffActive[BUSINESS_MAN_1_AI]){
                setSpeed(velocityIncrement + (-14));
                main.minSpawnInterval = 1600;
            }
            else if (debuffActive[SPORTS_SCIENCE_MAJOR]){
                setSpeed(velocityIncrement + (-30));
                main.minSpawnInterval = 850;
            }
        }

        // Change size of obstacle logic 
        if (effectActive[SIZE]){
            if (buffActive[NUTRITION_MAJOR])
                setSize(0.0054f);
            else if (debuffActive[CULINARY_MAJOR])
                setSize(0.0082f);
        }

        // Enable immunity of player
        if (effectActive[IMMUNE])
            immunity = true;


        velocityIncrement = (int)(score / 10) * 3;


        world.step(delta, 3, 3); // tell Box2D world to move forward in time
    }

    private void effectCancellation(int effectType){
        switch (effectType) {
            case SPEED:
                setSpeed(velocityIncrement + (-20));
                main.minSpawnInterval = 1000;
                break;

            case SIZE:
                setSize(assMan.REFplayerScale);
                break;

            case JUMP:
                break;

            case IMMUNE:
                immunity = false;
                break;

            default:
                System.out.println("Some error has occured while cancelling effects.");
        }
    }

    private void setSpeed(int velocity){
        // Loop through all obstacles and set linear velocity to parameter (can be faster or slower, or regular)
        for (Body osbtacle : obstacles) 
            osbtacle.setLinearVelocity((float) velocity, 0);
    }

    private void setSize(float scale){
        // Change playerScale that MainScreen uses to render texture of player
        assMan.playerScale = scale;

        float lastPlayerPosY = player.getPosition().y;
        Vector2 lastPlayerVelocity = player.getLinearVelocity();
            
        setBodyObjectType(regularPlayer, "SLEEP_PLAYER");
        regularPlayer.setTransform(14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001), regularPlayer.getAngle());
        setBodyObjectType(smallPlayer, "SLEEP_PLAYER");
        smallPlayer.setTransform(14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001), smallPlayer.getAngle());
        setBodyObjectType(bigPlayer, "SLEEP_PLAYER");
        bigPlayer.setTransform(14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001), bigPlayer.getAngle());

       // Set player to different sizes depending on parameter
        if (scale == 0.0054f){
            player = smallPlayer;
            NORMAL = 78;
            HIGH = 100;
            LOW = 35;
        }
        else if (scale == 0.007f){
            player = regularPlayer;
            NORMAL = 115;
            HIGH = 135;
            LOW = 73;
        }

        else if (scale == 0.0082f){
            player = bigPlayer;
            NORMAL = 160;
            HIGH = 185;
            LOW = 115;
        }
        else 
            System.out.println("Some error has occured while changing sizes.");

        setBodyObjectType(player, "PLAYER");
        player.setTransform(-14f, lastPlayerPosY, player.getAngle());
        player.setLinearVelocity(lastPlayerVelocity);
    }




    public void passThrough(Body bod) {
        for (Fixture fix : bod.getFixtureList()) {
            fix.setSensor(true);
        }
    }


 



    public void spawnObstacles(float v) {
        obstacles.add(createObstacle(v));
        obstacleTime = TimeUtils.millis();
        main.obstacleSpawnInterval = main.minSpawnInterval + (300 * random.nextInt(6));
    }

    public void trackObstacles() {
        for (Iterator<Body> iter = obstacles.iterator(); iter.hasNext(); ) {
            Body obstacle = iter.next();
            if (obstacle.getPosition().x < -25) {  // -16 + (-9)  (9 is aprox max unit size of obstacle)
                System.out.println("Score: " + score);
                score++;
                iter.remove();
            }
        }
    }

    public void spawnBuffs() {
        buffs.add(createBuff());
        buffTime = TimeUtils.millis();
        main.buffSpawnInterval = (long)(main.minSpawnInterval * 3) + (300 * random.nextInt(6));
    }

    public void spawnDebuffs() {
        debuffs.add(createDebuff());
        buffTime = TimeUtils.millis();
        main.buffSpawnInterval = (long)(main.minSpawnInterval * 3) + (300 * random.nextInt(6));
    }

    // Check if buff/debuff is out of screen
    // If true, remove and discard
    public void trackBuffsDebuffs() {
        for (Iterator<Body> iter = buffs.iterator(); iter.hasNext(); ) {
            Body buff = iter.next();
            if (buff.getPosition().x < -21)  // -16 + (-5)  (5 is aprox max unit size of buff/debuff) 
                iter.remove();
        }
        for (Iterator<Body> iter = debuffs.iterator(); iter.hasNext(); ) {
            Body debuff = iter.next();
            if (debuff.getPosition().x < -21) 
                iter.remove();
        }
    }




    public void playSound(int sound) {
        switch(sound) {
            case JUMP_SOUND:
                jump.play();
                break;
            case COLLECT_SOUND:
                collect.play();
                break;
        }
    }




    // Takes Body
    // Returns BodyObjectType (player, obstacle, buff, debuff)
    // Used to check what Body it is (mostly in contact listener)
    public String getBodyObjectType(Body bod){
        return ((BodyData) bod.getUserData()).bodyObjectType;
    }

    // Takes Body, bodyObjectType
    // Sets bodyObjectType to BodyData (SLEEP_PLAYER, PLAYER)
    // Used to manipulate which body to use
    public void setBodyObjectType(Body bod, String bodObjType){
        ((BodyData) bod.getUserData()).bodyObjectType = bodObjType;
    }

    // Takes Body
    // Returns TextureId (int)
    // Used to check what texture Body is using (mostly for obstacle, buff, debuff) (mostly used for hitbox and rendering)
    public int getTextureId(Body bod){
        return ((BodyData) bod.getUserData()).textureId;
    }




    public void removeCollidedObstacle(){
        collideObstacle.setTransform(-27f, collideObstacle.getPosition().y, collideObstacle.getAngle());
    }

    public void resetImmune(){
        effectActive[DEAN] = false;
        buffActive[DEAN] = false;
        immunity = false;
    }

    public void removeCollidedDeBuff(){
        collideDeBuff.setTransform(-27f, collideDeBuff.getPosition().y, collideDeBuff.getAngle());
        collideDeBuff = null;
    }
}
