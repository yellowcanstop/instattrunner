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

import java.util.Iterator;

// Controls all logic in game
public class IRModel {
    public World world;
    private OrthographicCamera camera;
    private KeyboardController controller;
    public Body player;
    public boolean isDead = false;
    public int score = 0;
    private IRAssetManager irAM;
    private Sound jump;
    private Sound collect;
    public static final int JUMP_SOUND = 0;
    public static final int COLLECT_SOUND = 1;
    public Array obstacles = new Array<Body>();
    public long lastTime;
    public Array buffs = new Array<Body>();
    public long buffTime;
    // tweak player jump
    public boolean jumpHigh = false;
    public boolean jumpLow = false;
    public static int NORMAL = 250;
    public static int HIGH = 60;
    public static int LOW = 20;
    // tweak speed of obstacles
    public boolean speedUp = false;
    public float fast = -20f;
    public float regular = -10f;


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

    private void tweakJump(int y) {
        player.applyLinearImpulse(0, y, player.getWorldCenter().x, player.getWorldCenter().y, true);
    }

    // todo ensure player cannot jump outside of view
    // logic method to run logic part of the model
    public void logicStep(float delta) {
        if (jumpHigh) {
            if (controller.space) {
                tweakJump(HIGH);
            }
        }
        if (jumpLow) {
            if (controller.space) {
                tweakJump(LOW);
            }
        }
        if (controller.space) {
            tweakJump(NORMAL);
        }
        world.step(delta, 3, 3); // tell Box2D world to move forward in time
    }

    private void createPlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-14, -8);
        bodyDef.fixedRotation = false;
        player = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 4f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f; // bounciness
        player.createFixture(fixtureDef);
        shape.dispose();
        player.setUserData("PLAYER");
    }

    private void createFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);
        Body floor = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);
        floor.createFixture(shape, 0.0f);
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
        shape.dispose();
        bodyk.setLinearVelocity(v, 0);
        bodyk.setUserData("OBSTACLE");
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
