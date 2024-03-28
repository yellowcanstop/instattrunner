package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;
import com.instattrunner.loader.ConstHub;

public class Buff {
    private GameWorld parent;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader buffLoader;


    public Buff(GameWorld gameWorld){
        parent = gameWorld;
        buffLoader = new BodyEditorLoader(Gdx.files.internal("buffComplexPolygons.json"));
    }  

    public Body createBuff() {
        int tempTextureId = parent.random.nextInt(4);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(16, (float)(parent.floor.getPosition().y + (ConstHub.floorWidHei.y / 2) + (ConstHub.obstacleWidHei[3].y*ConstHub.obstacleScale) + 8*parent.random.nextFloat()));

        Body buff = parent.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        buffLoader.attachFixture(buff, ConstHub.buffImagesName[tempTextureId], fixtureDef, ConstHub.buffScale);

        buff.setLinearVelocity(-20f, 0);
        buff.setUserData(new BodyData("BUFF", tempTextureId));

        parent.passThrough(buff);

        return buff;
    }
}