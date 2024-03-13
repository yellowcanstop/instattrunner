package com.instattrunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.controller.KeyboardController;
import com.instattrunner.loader.IRAssetManager;
import com.instattrunner.BodyData;

import java.util.Iterator;

// Controls all logic in game
public class IRModel {
    public World world;
    private OrthographicCamera camera;
    private KeyboardController controller;

    // Load sound asset from AssetManager to be used to play when 
    private IRAssetManager irAM;
    private Sound jump;
    private Sound collect;


    // Bodies (yes bodies, just not human)
    public Body player;
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

    private void createPlayer() {
        // Create BodyDef for new Body for player
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-14, -8);
        bodyDef.fixedRotation = false;

        // Create new Body of player in World
        player = world.createBody(bodyDef);

        // Create new CircleShape for the Fixture of the Body of player
        CircleShape shape = new CircleShape();
        shape.setRadius(2);

        // Create new FixtureDef for Body of player
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f; // bounciness

        // Set Fixture of Body of player to fixtureDef
        player.createFixture(fixtureDef);
        // Set UserData of the particular Body of player
        // Used to identify the body
        player.setUserData(new BodyData("PLAYER", 0));

        // Dispose shape to prevent memory leak
        shape.dispose();
    }

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

    private void passThrough(Body bod) {
        for (Fixture fix : bod.getFixtureList()) {
            fix.setSensor(true);
        }
    }

    private Body createObstacle(float v) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,-8);

        Body bodyk = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        bodyk.createFixture(shape, 0.0f);
        bodyk.setLinearVelocity(v, 0);
        bodyk.setUserData(new BodyData("OBSTACLE", MathUtils.random(0, 3)));

        shape.dispose();

        return bodyk;
    }

    private Body createBuff() {
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
        bodyk.setUserData("BUFF");
        passThrough(bodyk);
        return bodyk;
    }

    private Body createDebuff() {
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
        bodyk.setUserData("DEBUFF");
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
}
