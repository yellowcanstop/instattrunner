package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;

public class Debuff {
    private GameWorld parent;
    private Vector2 floorWidHei;
    private Vector2 stairsWidHei;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader debuffLoader;

    // Will be used by BodyEditorLoader to load different complex polygons to FixtureDef based on image name
    private final String[] debuffImagesName;

    private float debuffScale;


    public Debuff(GameWorld gameWorld){
        parent = gameWorld;
        floorWidHei = parent.locCHub.floorWidHei;
        stairsWidHei = parent.locCHub.obstacleWidHei[3];
        debuffLoader = new BodyEditorLoader(Gdx.files.internal("debuffComplexPolygons.json"));
        debuffImagesName = parent.locCHub.debuffImagesName;
        debuffScale = parent.locCHub.debuffScale;
    }  


    public Body createDebuff() {
        int tempTextureId = parent.random.nextInt(3);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(16, (float)(parent.floor.getPosition().y + (floorWidHei.y / 2) + (stairsWidHei.y*parent.locCHub.obstacleScale) + 8*parent.random.nextFloat()));

        Body debuff = parent.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        debuffLoader.attachFixture(debuff, debuffImagesName[tempTextureId], fixtureDef, debuffScale);

        debuff.setLinearVelocity(-20f, 0);
        debuff.setUserData(new BodyData("DEBUFF", tempTextureId));

        parent.passThrough(debuff);

        return debuff;
    }
}