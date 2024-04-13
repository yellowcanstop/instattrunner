package com.instattrunner.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.instattrunner.BodyData;
import com.instattrunner.BodyEditorLoader;
import com.instattrunner.GameWorld;
import com.instattrunner.loader.ConstHub;

public class Buff extends Body{
    private GameWorld container;

    // BodyEditorLoader for loading complex polygons to FixtureDef to Body
    private BodyEditorLoader buffLoader;


    public Buff(GameWorld gameWorld, Long addr){
        super(gameWorld.world, addr);
        container = gameWorld;
        buffLoader = new BodyEditorLoader(Gdx.files.internal("buffComplexPolygons.json"));
    }  

    public Body createBuff() {
        int tempTextureId = container.random.nextInt(4);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(16, (float)(container.floor.getPosition().y + (ConstHub.floorWidHei.y / 2) + (ConstHub.obstacleWidHei[3].y*ConstHub.obstacleScale) + 8*container.random.nextFloat()));

        Body buff = container.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        buffLoader.attachFixture(buff, ConstHub.buffImagesName[tempTextureId], fixtureDef, ConstHub.buffScale);

        buff.setLinearVelocity(-20f, 0);
        buff.setUserData(new BodyData("BUFF", tempTextureId));

        container.passThrough(buff);

        return buff;
    }
}