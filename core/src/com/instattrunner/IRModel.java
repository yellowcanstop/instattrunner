package com.instattrunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.controller.KeyboardController;
import com.instattrunner.loader.IRAssetManager;
import com.instattrunner.BodyEditorLoader;


import com.instattrunner.BodyData;

import java.util.Iterator;

// Controls all logic in game
public class IRModel {
    public World world;
    private OrthographicCamera camera;
    private KeyboardController controller;

    // Load sound asset from AssetManager to be used during gameplay
    private IRAssetManager irAM;
    private BodyEditorLoader obstacleLoader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygon.json"));
    private BodyEditorLoader buffLoader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygon.json"));
    private BodyEditorLoader debuffLoader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygon.json"));
    private Sound jump;
    private Sound collect;

    public final String[] obstacleImages = irAM.obstacleImages;
    public final String[] buffImages = irAM.buffImages;
    public final String[] debuffImages = irAM.debuffImages;


    // Bodies (yes bodies, just not human)
    public Body player;
    public Body playerStand;
    public Body floor;
    public Array<Body> obstacles = new Array<Body>();
    public Array<Body> buffs = new Array<Body>();
    public Array<Body> debuffs = new Array<Body>();


    // Vars for environment
    public long lastTime;    // Time since last obstacle spawn
    public long buffTime;    // Time for buff (not sure yet)
    public boolean isDead = false;
    public int score = 0;


    // tweak player jump
    public boolean jumpHigh = false;
    public boolean jumpLow = false;


    // tweak speed of obstacles
    public boolean speedUp = false;
    public float fast = -20f;
    public float regular = -10f;


    // ENUM
    // enum for jump
    public static int NORMAL = 100;
    public static int HIGH = 60;
    public static int LOW = 20;

    // enum for sound 
    public static final int JUMP_SOUND = 0;
    public static final int COLLECT_SOUND = 1;

    public Vector2 playerWC;
    public Vector2 playerStandWC;
    public Vector2 avgWC;






    // Contructor
    // world to keep all physical objects in the game
    public IRModel(KeyboardController cont, OrthographicCamera cam, IRAssetManager assetMan) {
        controller = cont;
        camera = cam;
        irAM = assetMan;
        world = new World(new Vector2(0, -50f), true);
        world.setContactListener(new IRContactListener(this));

        createFloor();
        createPlayer();

        //createFat();
        //createSpeed();

        // get our body factory singleton and store it in bodyFactory
        //BodyFactory bodyFactory = BodyFactory.getInstance(world);

        // load sounds into model
        assetMan.queueAddSounds();
        assetMan.manager.finishLoading();
        jump = assetMan.manager.get("sounds/drop.wav");
        collect = assetMan.manager.get("sounds/drop.wav");

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
        if (jumpHigh) {
            if (controller.space) {
                jumped = true;
                tweakJump(HIGH);
            }
            else if (!controller.space && jumped) {
                canJump = false;
           }
        }
        if (jumpLow) {
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

        world.step(delta, 3, 3); // tell Box2D world to move forward in time
    }

    


    private void passThrough(Body bod) {
        for (Fixture fix : bod.getFixtureList()) {
            fix.setSensor(true);
        }
    }


    private void createPlayer() {
        // Create new BodyDef for both player Body and playerStand Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(-13, -6);
        bodyDef.fixedRotation = true;

        // Create new FixtureDef for both playerStand and player Body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f; // bounciness

        // Declaring scale for rendering the Body
        float scale = .008f;  // around half of goose scale

        // Create new BodyEditorLoader and load convex polygon using .json file
        // Has complex polygon combo for both player and playerStand
        // Passes Body to BodyEditorLoader 
        // BodyEditorLoader creates multiple convex polygon using .json file 
        // 1 convex polygon, 1 FixtureDef
        // Each FixtureDef is .createFixture to Body
        // All done in BodyEditorLoader through method .attachFixture
        // Scale is scale of shape (scale is same as stand)
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygon.json"));


        // STAND BODY
        // Create new Body of playerStand in World
        playerStand = world.createBody(bodyDef);

        // Load and createFixture with polygons to playerStand Body
        loader.attachFixture(playerStand, "SpriteStand", fixtureDef, scale);    // Name is the name set when making complex polygon. For all, all is image file name (without pic/)

        // Set UserData of the particular Body 
        // Used to identify the body
        playerStand.setUserData(new BodyData("STAND", 0));


        // PLAYER BODY 
        // Create new Body of player in World
        player = world.createBody(bodyDef);

        // Load and createFixture with polygons to player Body
        loader.attachFixture(player, "Sprite.png", fixtureDef, scale);    // Name is the name set when making complex polygon. For all, all is image file name (without pic/)

        // Set UserData of the particular Body of player
        // Used to identify the body
        player.setUserData(new BodyData("PLAYER", 0));
   }


//    to rearrange and comment later
    private void createFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);

        floor = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);

        floor.createFixture(shape, 0.0f);
        floor.setUserData(new BodyData("FLOOR", 0));

        shape.dispose();
    }


    private Body createObstacle(float v) {
        // Generate random int from 0 to 3
        // int is id for texture declared (in IRAssetManager and MainScreen)
        int tempTextureId = MathUtils.random(0, 3);

        // Create new BodyDef 
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,-8);

        // Create new Body in World
        Body obstacle = world.createBody(bodyDef);

        // Create new FixtureDef
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        obstacle.setLinearVelocity(v, 0);
        obstacle.setUserData(new BodyData("OBSTACLE", tempTextureId));


        passThrough(obstacle);
        
        return obstacle;
    }

    private Body createBuff() {
        int tempTextureId = MathUtils.random(0, 3);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,4);
        Body bodyk = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        bodyk.createFixture(shape, 0.0f);
        shape.dispose();
        bodyk.setLinearVelocity(-20f, 0);
        bodyk.setUserData(new BodyData("BUFF", tempTextureId));
        passThrough(bodyk);
        return bodyk;
    }

    private Body createDebuff() {
        int tempTextureId = MathUtils.random(0, 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,4);
        Body bodyk = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        bodyk.createFixture(shape, 0.0f);
        shape.dispose();
        bodyk.setLinearVelocity(-20f, 0);
        bodyk.setUserData(new BodyData("DEBUFF", tempTextureId));
        passThrough(bodyk);
        return bodyk;
    }

    // todo: random choice of buff, obstacles spawned using MathUtils.random(20001), choosing from an array?
    public void spawnObstacles(float v) {
        Body obstacle = createObstacle(v);
        obstacles.add(obstacle);
        lastTime = TimeUtils.millis();
    }

    public void trackObstacles() {
        for (Iterator<Body> iter = obstacles.iterator(); iter.hasNext(); ) {
            Body obstacle = iter.next();
            if (obstacle.getPosition().x < -16) {
                System.out.println("Score: " + score);
                score++;
                iter.remove();
            }
        }
    }


    public void spawnBuffs() {
        Body buff = createBuff();
        buffs.add(buff);
        buffTime = TimeUtils.millis();
    }

    public void spawnDebuffs() {
        Body debuff = createDebuff();
        debuffs.add(debuff);
        buffTime = TimeUtils.millis();
    }



    // test for debuff which increases player density
    private Body createFat() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,4);
        Body bodyk = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        bodyk.createFixture(shape, 0.0f);
        shape.dispose();
        bodyk.setLinearVelocity(-10f, 0);
        bodyk.setUserData("FAT");
        passThrough(bodyk);
        return bodyk;
    }

    // test for speed up which increases velocity of obstacles
    private Body createSpeed() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,4);
        Body bodyk = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        bodyk.createFixture(shape, 0.0f);
        shape.dispose();
        bodyk.setLinearVelocity(-10f, 0);
        bodyk.setUserData("SPEED");
        passThrough(bodyk);
        return bodyk;
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

    // Takes Body
    // Returns TextureId (int)
    // Used to check what texture Body is using (mostly for obstacle, buff, debuff) (mostly used for hitbox and rendering)
    public int getTextureId(Body bod){
        return ((BodyData) bod.getUserData()).textureId;
    }
}
