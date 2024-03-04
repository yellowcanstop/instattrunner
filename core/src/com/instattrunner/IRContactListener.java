package com.instattrunner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

// contact listener to react to collisions in world
// everytime two bodies collide, beginContact is run
public class IRContactListener implements ContactListener {
    private IRModel parent;

    public IRContactListener(IRModel parent) {
        this.parent = parent;
    }

    // all collisions are checked against player.
    // runs when some bodies collide
    // each body has a fixture which is the colliding part (store in fa, fb)
    @Override
    public void beginContact(Contact contact) {
        // temp print to console when sth collides
        System.out.println("Contact");
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        System.out.println(fa.getBody().getUserData() + "has hit" + fb.getBody().getUserData());


        // todo: don't need this effect
        // todo: collide with buff/debuff == bullet logic
        // todo: collide with obstacle == DEATH
        // todo: missed collision with obstacle == POINT++

        /*
        // use unique identifier to check collision with water
        if (fa.getBody().getUserData() == "IAMTHESEA") {
            parent.isSwimming = true;
            return; // skip static body collision logic below
        } else if (fb.getBody().getUserData() == "IAMTHESEA") {
            parent.isSwimming = true;
            return;
        }

         */

        // check collision with obstacle
        if (fa.getBody().getUserData() == "OBSTACLE") {
            parent.isDead = true; // triggers change to end screen from render() in main
            return;
        } else if (fb.getBody().getUserData() == "OBSTACLE") {
            parent.isDead = true;
            return;
        }

        // check collision with buff
        if (fa.getBody().getUserData() == "BUFF") {
            this.triggerBuff(fa, fb);
            return;
        } else if (fb.getBody().getUserData() == "BUFF") {
            this.triggerBuff(fb, fa);
            return;
        }


        /*
        // when hit static floor, shoot up in the air
        if (fa.getBody().getType() == BodyDef.BodyType.StaticBody) {
            this.shootUpInAir(fa, fb);
        } else if (fb.getBody().getType() == BodyDef.BodyType.StaticBody) {
            this.shootUpInAir(fb, fa);
        } else {
            // neither fa nor fb are static so do nothing
        }

         */
    }

    // when hit static floor, shoot up in the air
    private void shootUpInAir(Fixture staticFixture, Fixture otherFixture) {
        System.out.println("Adding Force");
        otherFixture.getBody().applyForceToCenter(new Vector2(-100000, -100000), true);
        parent.playSound(IRModel.JUMP_SOUND);
    }

    // collection of buff
    private void triggerBuff(Fixture one, Fixture other) {
        System.out.println("Buff collected");
        // todo temporary jumpHigh
        parent.jumpHigh = true;
        parent.playSound(IRModel.COLLECT_SOUND);
    }

    @Override
    public void endContact(Contact contact) {
        System.out.println("Contact");
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        /*
        // tell model that player is out of the water
        if (fa.getBody().getUserData() == "IAMTHESEA") {
            parent.isSwimming = false;
        } else if (fb.getBody().getUserData() == "IAMTHESEA") {
            parent.isSwimming = false;
        }

         */


    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
