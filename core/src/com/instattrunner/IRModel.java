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
import java.util.Vector;

// Controls all logic in game
public class IRModel {
    public World world;
    private OrthographicCamera camera;
    private KeyboardController controller;

    // Bodies (yes bodies, just not human bodies, although we have a player BODY)
    public Body player;
    public Body floor;
    public Array<Body> obstacles = new Array<Body>();
    public Array<Body> buffs = new Array<Body>();
    public Array<Body> debuffs = new Array<Body>();

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    // Declared here (only obstacle, buff, debuff) as repeatedly called and used (player is only used once, hence not here)
    private BodyEditorLoader obstacleLoader;
    private BodyEditorLoader buffLoader;
    private BodyEditorLoader debuffLoader;

    // Declare array to store name of images
    // Will be used by BodyEditorLoader to load different complex polygons to FixtureDef based on image name
    private final String playerImage;
    private final String[] obstacleImages;
    private final String[] buffImages;
    private final String[] debuffImages;

    // Declare array to store width and height of different player, obstacle, buff and debuff
    // Declare width and height of floor for computation
    private final Vector2 floorWidHei = new Vector2(32f, 3f);

    // Scale of category of body
    private final float playerScale;
    private final float obstacleScale;
    private final float buffScale;
    private final float debuffScale;

    // Declare object Sound to store sound loaded from asset manager
    private Sound jump;
    private Sound collect;

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
    public static int NORMAL = 140;
    public static int HIGH = 100;
    public static int LOW = 60;

    // enum for sound 
    public static final int JUMP_SOUND = 0;
    public static final int COLLECT_SOUND = 1;




    // Contructor
    // world to keep all physical objects in the game
    public IRModel(KeyboardController cont, OrthographicCamera cam, IRAssetManager assetMan) {
        controller = cont;
        camera = cam;
        world = new World(new Vector2(0, -60f), true);
        world.setContactListener(new IRContactListener(this));

    
        //createFat();
        //createSpeed();

        // get our body factory singleton and store it in bodyFactory
        //BodyFactory bodyFactory = BodyFactory.getInstance(world);

        // load sounds into model
        assetMan.queueAddSounds();
        assetMan.manager.finishLoading();
        jump = assetMan.manager.get("sounds/drop.wav");
        collect = assetMan.manager.get("sounds/drop.wav");

        // Init BodyEditorLoader
        obstacleLoader = new BodyEditorLoader(Gdx.files.internal("obstacleComplexPolygons.json"));
        buffLoader = new BodyEditorLoader(Gdx.files.internal("buffComplexPolygons.json"));
        debuffLoader = new BodyEditorLoader(Gdx.files.internal("debuffComplexPolygons.json"));

        // Load names of obstacle, buff and debuff images
        playerImage = assetMan.playerImage;
        obstacleImages = assetMan.obstacleImages;
        buffImages = assetMan.buffImages;
        debuffImages = assetMan.debuffImages;

        // Load width and height of obstacle, buff and debuff 
        playerWidHei = assetMan.playerWidHei;
        obstacleWidHei = assetMan.obstacleWidHei;
        buffWidHei = assetMan.buffWidHei;
        debuffWidHei = assetMan.debuffWidHei; 

        // Load scale of body of different category
        playerScale = assetMan.playerScale;
        obstacleScale = assetMan.obstacleScale;
        buffScale = assetMan.buffScale;
        debuffScale = assetMan.debuffScale;

        createFloor();
        createPlayer();
        // dum(player);

        // for (int i = 0; i < 4; i++)
        //     dum(createObstacle(1, i));

        // for (int i = 0; i < 4; i++)
        //     dum(createBuff(i));

        // for (int i = 0; i < 3; i++)
        //     dum(createDebuff(i));
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

//    to rearrange and comment later
    private void createFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0f, -10.5f);    // Max floor height is y + hy = -9    ;    Here, rectangle is set to pos in center of rectangle

        floor = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(floorWidHei.x / 2, floorWidHei.y / 2);    //Divided by 2 as .setAsBox takes half width and half height

        floor.createFixture(shape, 0f);
        floor.setUserData(new BodyData("FLOOR", 0));

        shape.dispose();
    }


    private void createPlayer() {
        // Create new BodyDef for player Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(-14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001));    // Complex polygon, pos is set to lower left.  Get center of floor and add with half height to get max height of floor, add 0.001 as buffer to avoid clipping
        bodyDef.fixedRotation = true;
        // Create new Body of player in World
        player = world.createBody(bodyDef);

        // Create new FixtureDef for player Body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.9f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f; // bounciness

        // Create new BodyEditorLoader and load convex polygon using .json file
        // Has complex polygon combo for player 
        // Passes Body to BodyEditorLoader 
        // BodyEditorLoader creates multiple convex polygon using .json file 
        // 1 convex polygon, 1 FixtureDef
        // Each FixtureDef is .createFixture to Body
        // All done in BodyEditorLoader through method .attachFixture
        // Scale is scale of shape 
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygons.json"));
    
        // Load and createFixture with polygons to player Body
        // Load with respect to scale declared in asset manager
        loader.attachFixture(player, playerImage, fixtureDef, playerScale);    // Name is the name set when making complex polygon. For all, all is image file name

        // Set custom class BodyData to UserData of Body of player to store bodyType and textureId
        player.setUserData(new BodyData("PLAYER", 0));
        System.out.println(player.getPosition());
    }

    private void dum(Body bod) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        // Iterate through each fixture attached to the body
        for (Fixture fixture : bod.getFixtureList()) {
            Shape shape = fixture.getShape();
            if (shape.getType() == Shape.Type.Polygon) {
                PolygonShape polygon = (PolygonShape) shape;
                // Get the number of vertices in the polygon shape
                int vertexCount = polygon.getVertexCount();
                // Iterate through each vertex to find the minimum and maximum extents
                for (int i = 0; i < vertexCount; i++) {
                    Vector2 vertex = new Vector2();
                    polygon.getVertex(i, vertex);
                    // Update the minimum and maximum extents along the x and y axes
                    minX = Math.min(minX, vertex.x);
                    minY = Math.min(minY, vertex.y);
                    maxX = Math.max(maxX, vertex.x);
                    maxY = Math.max(maxY, vertex.y);
                }
            } else if (shape.getType() == Shape.Type.Circle) {
                CircleShape circle = (CircleShape) shape;
        // Get the position of the circle shape (center)
        Vector2 center = circle.getPosition();
        // Calculate the radius
        float radius = circle.getRadius();
        // Update the minimum and maximum extents based on the circle's bounding box
        minX = Math.min(minX, center.x - radius);
        minY = Math.min(minY, center.y - radius);
        maxX = Math.max(maxX, center.x + radius);
        maxY = Math.max(maxY, center.y + radius);
            }
        }

        // Calculate the width and height of the body based on the extents
        float width = maxX - minX;
        float height = maxY - minY;

        System.out.println("Width: " + width);
        System.out.println("Height: " + height);



    }


    private Body createObstacle(float v) {
        // Generate random int from 0 to 3
        // int is id for texture declared (in IRAssetManager)
        int tempTextureId = MathUtils.random(0, 3);

        // Create new BodyDef 
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15, (float)(floor.getPosition().y + (floorWidHei.y / 2)));
        // Create new Body in World
        Body obstacle = world.createBody(bodyDef);

        // Create new FixtureDef
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        // Load and createFixture with polygons to player Body
        // Load with respect to scale declared in asset manager
        obstacleLoader.attachFixture(obstacle, obstacleImages[tempTextureId], fixtureDef, obstacleScale);

        // Set obstacle to move with constant velocity of v
        obstacle.setLinearVelocity(v, 0);
        // Set custom class BodyData to UserData of Body of player to store bodyType and textureId
        obstacle.setUserData(new BodyData("OBSTACLE", tempTextureId));
        
        return obstacle;
    }

    private Body createBuff() {
        int tempTextureId = MathUtils.random(0, 3);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 10));

        Body buff = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        buffLoader.attachFixture(buff, buffImages[tempTextureId], fixtureDef, buffScale);

        buff.setLinearVelocity(-20f, 0);
        buff.setUserData(new BodyData("BUFF", tempTextureId));

        passThrough(buff);

        return buff;
    }

    private Body createDebuff() {
        int tempTextureId = MathUtils.random(0, 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(15, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 10));

        Body debuff = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        debuffLoader.attachFixture(debuff, debuffImages[tempTextureId], fixtureDef, debuffScale);

        debuff.setLinearVelocity(-20f, 0);
        debuff.setUserData(new BodyData("DEBUFF", tempTextureId));

        passThrough(debuff);

        return debuff;
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
