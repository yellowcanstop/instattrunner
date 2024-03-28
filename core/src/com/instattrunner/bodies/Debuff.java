package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;
import com.instattrunner.loader.ConstHub;

public class Debuff {
    private GameWorld parent;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader debuffLoader;


    public Debuff(GameWorld gameWorld){
        parent = gameWorld;
        debuffLoader = new BodyEditorLoader(Gdx.files.internal("debuffComplexPolygons.json"));
    }  


    public Body createDebuff() {
        int tempTextureId = parent.random.nextInt(3);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(16, (float)(parent.floor.getPosition().y + (ConstHub.floorWidHei.y / 2) + (ConstHub.obstacleWidHei[3].y*ConstHub.obstacleScale) + 8*parent.random.nextFloat()));

        Body debuff = parent.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        debuffLoader.attachFixture(debuff, ConstHub.debuffImagesName[tempTextureId], fixtureDef, ConstHub.debuffScale);

        debuff.setLinearVelocity(-20f, 0);
        debuff.setUserData(new BodyData("DEBUFF", tempTextureId));

        parent.passThrough(debuff);

        return debuff;
    }
}