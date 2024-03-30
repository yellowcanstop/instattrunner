package com.instattrunner.bodies;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;
import com.instattrunner.loader.ConstHub;

public class Obstacle {
    private GameWorld container;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader obstacleLoader;

    // ArrayList for spawn randomization
    private ArrayList<Integer> obstacleSpawnUnused = new ArrayList<>(Arrays.asList(0,1,2,3));
    private ArrayList<Integer> obstacleSpawnUsed = new ArrayList<>();


    public Obstacle(GameWorld gameWorld){
        container = gameWorld;
        obstacleLoader = new BodyEditorLoader(Gdx.files.internal("obstacleComplexPolygons.json"));
    }


    public Body createObstacle(float v) {
        // Generate random int from 0 to size of arraylist unused - 1
        // To pick element from unused list
        // int is id for texture declared (in IRAssetManager)
        if (obstacleSpawnUnused.isEmpty()){
            ArrayList<Integer> temp = obstacleSpawnUnused;
            obstacleSpawnUnused = obstacleSpawnUsed;
            obstacleSpawnUsed = temp;
        }
        int tempTextureId = obstacleSpawnUnused.remove(container.random.nextInt(obstacleSpawnUnused.size()));
        obstacleSpawnUsed.add(tempTextureId);

        // Create new BodyDef 
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(16, (float)(container.floor.getPosition().y + (ConstHub.floorWidHei.y / 2)));
        // Create new Body in World
        Body obstacle = container.world.createBody(bodyDef);

        // Create new FixtureDef
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        // Load and createFixture with polygons to player Body
        // Load with respect to scale declared in asset manager
        obstacleLoader.attachFixture(obstacle, ConstHub.obstacleImagesName[tempTextureId], fixtureDef, ConstHub.obstacleScale);

        // Set obstacle to move with constant velocity of v
        obstacle.setLinearVelocity(v, 0);
        // Set custom class BodyData to UserData of Body of player to store bodyType and textureId
        obstacle.setUserData(new BodyData("OBSTACLE", tempTextureId));

        container.passThrough(obstacle);
        
        return obstacle;
    }
}