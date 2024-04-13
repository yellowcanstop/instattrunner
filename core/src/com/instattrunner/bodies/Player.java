package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;
import com.instattrunner.loader.ConstHub;

public class Player extends Body {
    private GameWorld container;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader playerLoader;


    // Constructor
    public Player(GameWorld gameWorld, Long addr){
        super(gameWorld.world, addr);
        container = gameWorld;
        playerLoader = new BodyEditorLoader(Gdx.files.internal("playerComplexPolygons.json"));
    }

   
    public void createPlayer() {
        // Create new BodyDef for player Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(-14f, (float)(container.floor.getPosition().y + (ConstHub.floorWidHei.y / 2) + 0.001));    // Complex polygon, pos is set to lower left.  Get center of floor and add with half height to get max height of floor, add 0.001 as buffer to avoid clipping
        bodyDef.fixedRotation = true;

        // Create new Body of player in World
        container.regularPlayer = container.world.createBody(bodyDef);
        // Change position to right for small and big player
        bodyDef.position.set(14f, (float)(container.floor.getPosition().y + (ConstHub.floorWidHei.y / 2) + 0.001));    // Complex polygon, pos is set to lower left.  Get center of floor and add with half height to get max height of floor, add 0.001 as buffer to avoid clipping
        container.smallPlayer = container.world.createBody(bodyDef);
        container.bigPlayer = container.world.createBody(bodyDef);

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
        playerLoader.attachFixture(container.regularPlayer, ConstHub.playerImageName, fixtureDef, ConstHub.regularPlayerScale);    // Name is the name set when making complex polygon. For all, all is image file name
        playerLoader.attachFixture(container.smallPlayer, ConstHub.playerImageName, fixtureDef, ConstHub.smallPlayerScale);    // Name is the name set when making complex polygon. For all, all is image file name
        playerLoader.attachFixture(container.bigPlayer, ConstHub.playerImageName, fixtureDef, ConstHub.bigPlayerScale);    // Name is the name set when making complex polygon. For all, all is image file name

        // Set custom class BodyData to UserData of Body of player to store bodyType and textureId
        container.regularPlayer.setUserData(new BodyData("PLAYER", 0));
        container.smallPlayer.setUserData(new BodyData("SLEEP_PLAYER", 0));
        container.bigPlayer.setUserData(new BodyData("SLEEP_PLAYER", 0));

        container.player = container.regularPlayer;
    } 
}