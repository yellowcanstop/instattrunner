package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;

public class Player {
    private GameWorld parent;
    private Vector2 floorWidHei;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader playerLoader;

    // Will be used by BodyEditorLoader to load different complex polygons to FixtureDef based on image name
    private final String playerImageName;

    private float regularPlayerScale;
    private float smallPlayerScale;
    private float bigPlayerScale;


    public Player(GameWorld gameWorld){
        parent = gameWorld;
        floorWidHei = parent.locCHub.floorWidHei;
        playerLoader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygons.json"));
        playerImageName = parent.locCHub.playerImageName;
        regularPlayerScale = parent.locCHub.regularPlayerScale;
        smallPlayerScale = parent.locCHub.smallPlayerScale;
        bigPlayerScale = parent.locCHub.bigPlayerScale;
    }

   
    public void createPlayer() {
        // Create new BodyDef for player Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(-14f, (float)(parent.floor.getPosition().y + (floorWidHei.y / 2) + 0.001));    // Complex polygon, pos is set to lower left.  Get center of floor and add with half height to get max height of floor, add 0.001 as buffer to avoid clipping
        bodyDef.fixedRotation = true;

        // Create new Body of player in World
        parent.regularPlayer = parent.world.createBody(bodyDef);
        // Change position to right for small and big player
        bodyDef.position.set(14f, (float)(parent.floor.getPosition().y + (floorWidHei.y / 2) + 0.001));    // Complex polygon, pos is set to lower left.  Get center of floor and add with half height to get max height of floor, add 0.001 as buffer to avoid clipping
        parent.smallPlayer = parent.world.createBody(bodyDef);
        parent.bigPlayer = parent.world.createBody(bodyDef);

        // Create new FixtureDef for player Body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.9f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f; // bounciness

        // Create new BodyEditorLoader (in declaration part) and load convex polygon using .json file
        // Has complex polygon combo for player 
        // Passes Body to BodyEditorLoader 
        // BodyEditorLoader creates multiple convex polygon using .json file 
        // 1 convex polygon, 1 FixtureDef
        // Each FixtureDef is .createFixture to Body
        // All done in BodyEditorLoader through method .attachFixture
        // Scale is scale of shape 
        // Load and createFixture with polygons to player Body
        // Load with respect to scale declared in asset manager
        playerLoader.attachFixture(parent.regularPlayer, playerImageName, fixtureDef, regularPlayerScale);    // Name is the name set when making complex polygon. For all, all is image file name
        playerLoader.attachFixture(parent.smallPlayer, playerImageName, fixtureDef, smallPlayerScale);    // Name is the name set when making complex polygon. For all, all is image file name
        playerLoader.attachFixture(parent.bigPlayer, playerImageName, fixtureDef, bigPlayerScale);    // Name is the name set when making complex polygon. For all, all is image file name

        // Set custom class BodyData to UserData of Body of player to store bodyType and textureId
        parent.regularPlayer.setUserData(new BodyData("PLAYER", 0));
        parent.smallPlayer.setUserData(new BodyData("SLEEP_PLAYER", 0));
        parent.bigPlayer.setUserData(new BodyData("SLEEP_PLAYER", 0));

        parent.player = parent.regularPlayer;
    } 
}