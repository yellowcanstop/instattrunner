package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;

public class Buff {
    private GameWorld parent;
    private Vector2 floorWidHei;
    private Vector2 stairsWidHei;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader buffLoader;

    // Will be used by BodyEditorLoader to load different complex polygons to FixtureDef based on image name
    private final String[] buffImagesName;

    private float buffScale;


    public Buff(GameWorld gameWorld){
        parent = gameWorld;
        floorWidHei = parent.locCHub.floorWidHei;
        stairsWidHei = parent.locCHub.obstacleWidHei[3];
        buffLoader = new BodyEditorLoader(Gdx.files.internal("buffComplexPolygons.json"));
        buffImagesName = parent.locCHub.buffImagesName;
        buffScale = parent.locCHub.buffScale;
    }  

    public Body createBuff() {
        int tempTextureId = parent.random.nextInt(4);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(16, (float)(parent.floor.getPosition().y + (floorWidHei.y / 2) + (stairsWidHei.y*parent.locCHub.obstacleScale) + 8*parent.random.nextFloat()));

        Body buff = parent.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        buffLoader.attachFixture(buff, buffImagesName[tempTextureId], fixtureDef, buffScale);

        buff.setLinearVelocity(-20f, 0);
        buff.setUserData(new BodyData("BUFF", tempTextureId));

        parent.passThrough(buff);

        return buff;
    }
}