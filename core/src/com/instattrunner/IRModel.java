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

import static com.instattrunner.BodyFactory.*;

// Controls all logic in game
// static body: floor, wall
// static: not affected by gravity or other bodies, no move
// dynamic body: runner, buff, obstacles
// dynamic: affected by gravity and other bodies
// kinematic: like static, but can move
public class IRModel {
    public World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private KeyboardController controller;
    public boolean isSwimming = false; // changed by contact listener
    private Body bodyd; // dynamic body
    private Body bodys; // static body
    //private Body bodyk; // kinematic body
    public Body player;
    public boolean isDead = false;
    public boolean jumpHigh = false; // todo: jumpLow

    // todo update score everytime obstacle hits origin w/o collision
    public int score = 0;
    public Body water;
    public Body obstacle;
    // todo: buff be separate class? boolean isCollected. cycle through buffs
    // todo: random choice of buff, obstacles spawned using MathUtils.random(20001);
    public Body buff;
    private IRAssetManager irAM;
    private Sound jump;
    private Sound collect;
    public static final int JUMP_SOUND = 0;
    public static final int COLLECT_SOUND = 1;
    public Array obstacles = new Array<Body>();
    public long lastTime; // use long to store nanoseconds



    // world to keep all physical objects in the game
    public IRModel(KeyboardController cont, OrthographicCamera cam, IRAssetManager assetMan) {
        controller = cont;
        camera = cam;
        irAM = assetMan;

        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new IRContactListener(this));




        // todo: use create functions for kinematic. set obstacles to move
        createFloor();
        createPlayer();
        //spawnObstacles();
        //createObstacle();
        //createBuff();
        //createDebuff();

        //createObject();
        //createMovingObject();

        // get our body factory singleton and store it in bodyFactory
        BodyFactory bodyFactory = BodyFactory.getInstance(world);


        // add a player - todo: rubber
        //player = bodyFactory.makeCirclePolyBody(1, 1, 2, BodyFactory.STONE, BodyDef.BodyType.DynamicBody,false);
        //player.setUserData("PLAYER");

        /*
        // add an obstacle and give it a unique identifier
        water = bodyFactory.makeBoxPolyBody(1, -8, 40, 4, BodyFactory.STONE, BodyDef.BodyType.StaticBody, false);
        water.setUserData("IAMTHESEA");

        // todo: diff obstacles. link with death
        obstacle = bodyFactory.makeBoxPolyBody(5, 5, 5, 5, BodyFactory.STEEL, BodyDef.BodyType.KinematicBody, false);
        obstacle.setUserData("OBSTACLE");


        // todo: diff buffs. dynamic, rubber, link with score?
        buff = bodyFactory.makeCirclePolyBody(3, 3, 3, BodyFactory.RUBBER, BodyDef.BodyType.KinematicBody, false);
        buff.setUserData("BUFF");
        bodyFactory.makeAllFixturesSensors(buff);

        // make water a sensor so does not obstruct player
        //bodyFactory.makeAllFixturesSensors(water);

         */

        // load sounds into model
        assetMan.queueAddSounds();
        assetMan.manager.finishLoading();
        jump = assetMan.manager.get("sounds/drop.wav");
        // todo: get diff sound for collect
        collect = assetMan.manager.get("sounds/drop.wav");



    }

    // logic method to run logic part of the model
    public void logicStep(float delta) {
        if (controller.spacebar) {
            //player.applyForceToCenter(0, 200, true);
            // todo: set state jumping so can only jump once? so check if state jump is false
            player.applyLinearImpulse(0, 40, player.getWorldCenter().x, player.getWorldCenter().y, true);
        }
        if (jumpHigh) {
            player.applyLinearImpulse(0, 80, player.getWorldCenter().x, player.getWorldCenter().y, true);
            // todo: passage of time to set jumpHigh to false again
            jumpHigh = false;
        }
        /*
        if (isSwimming) {
            // make player act like swimming
            player.applyForceToCenter(0, 0, true);
        }

         */
        // tell Box2D world to move forward in time
        world.step(delta, 3, 3);

    }

    // creates dynamic body
    private void createPlayer(){
        // bodydef: data for the physical object
        // type of body, location, speed, rotation etc.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-14, -8);
        bodyDef.fixedRotation = false; // body factory
        // add it to the world
        player = world.createBody(bodyDef);
        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        CircleShape shape = new CircleShape();
        shape.setRadius(2);
        // set the properties of the object ( shape, weight, restitution(bouncyness)
        // a fixtureFed: data for a physical bdy part. can have more parts with diff density
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        //fixtureDef.density = 1f;
        //fixtureDef.friction = 0.9f;
        //fixtureDef.restitution = 0.01f;
        // create the physical object in our body)
        // without this our body would just be data in the world
        // "add that fixture to the body"

        //player.createFixture(fixtureDef);
        player.createFixture(makeFixture(STONE, shape));

        // we no longer use the shape object here so dispose of it.
        shape.dispose();
        player.setUserData("PLAYER");

    }


    // creates static body
    private void createFloor() {
        // create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);
        // add it to the world
        bodys = world.createBody(bodyDef);
        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);
        // create the physical object in our body)
        // without this our body would just be data in the world
        bodys.createFixture(shape, 0.0f);
        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }




    // creates kinematic body
    private Body createMovingObject(){
        //create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15,-8);
        // add it to the world
        Body bodyk = world.createBody(bodyDef);
        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        // set the properties of the object ( shape, weight, restitution(bouncyness)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        // create the physical object in our body)
        // without this our body would just be data in the world
        bodyk.createFixture(shape, 0.0f);
        // we no longer use the shape object here so dispose of it.
        shape.dispose();
        // todo tweak speed
        bodyk.setLinearVelocity(-5f, 0);
        return bodyk;
    }

    private Body createObstacle() {
        Body obstacle = createMovingObject();
        // speed and user data
        //obstacle.setLinearVelocity(-10f, 0);
        obstacle.setUserData("OBSTACLE");
        return obstacle;
    }


    public void spawnObstacles() {
        Body obstacle = createObstacle();
        obstacles.add(obstacle);
        lastTime = TimeUtils.millis();
    }

    private void createBuff() {
        Body buff = createMovingObject();
        // speed and user data
        //buff.setLinearVelocity(-10f, 0);
        buff.setUserData("BUFF");
    }

    private void createDebuff() {
        Body debuff = createMovingObject();
        // speed and user data
        //buff.setLinearVelocity(-10f, 0);
        debuff.setUserData("DEBUFF");
    }



    // switch between sounds to play in model
    // event trigger for the sounds is in contact listener
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
