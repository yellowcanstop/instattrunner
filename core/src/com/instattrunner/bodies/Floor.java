package com.instattrunner.bodies;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.instattrunner.BodyData;
import com.instattrunner.GameWorld;
import com.instattrunner.loader.ConstHub;

public class Floor {
    private GameWorld parent;


    public Floor(GameWorld gameWorld){
        parent = gameWorld;
    }


    public void createFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0f, -10.5f);    // Max floor height is y + hy = -9    ;    Here, rectangle is set to pos in center of rectangle

        parent.floor = parent.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ConstHub.floorWidHei.x / 2, ConstHub.floorWidHei.y / 2);    //Divided by 2 as .setAsBox takes half width and half height

        parent.floor.createFixture(shape, 0f);
        parent.floor.setUserData(new BodyData("FLOOR", 0));

        shape.dispose();
    }    
}