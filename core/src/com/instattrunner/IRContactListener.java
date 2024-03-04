package com.instattrunner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;

// contact listener to react to collisions in world
// everytime two bodies collide, beginContact is run
public class IRContactListener implements ContactListener {
    private IRModel parent;
    public IRContactListener(IRModel parent) {
        this.parent = parent;
    }

    // each body has a fixture which is the colliding part (store in fa, fb)
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        // check collision with obstacle
        if ((fa.getBody().getUserData() == "OBSTACLE" && fb.getBody().getUserData() == "PLAYER") || (fb.getBody().getUserData() == "OBSTACLE" && fa.getBody().getUserData() == "PLAYER")) {
            System.out.println("Player hit obstacle");
            parent.isDead = true; // triggers change to end screen from render() in main
            return;
        }


        // check collision with buff
        if (fa.getBody().getUserData() == "BUFF" || fb.getBody().getUserData() == "BUFF") {
            this.triggerBuff();
            return;
        }
        // check collision with debuff
        if (fa.getBody().getUserData() == "DEBUFF" || fb.getBody().getUserData() == "DEBUFF") {
            this.triggerDebuff();
            return;
        }
        // check collision with fattening
        if (fa.getBody().getUserData() == "FAT" || fb.getBody().getUserData() == "FAT") {
            this.triggerFat();
            return;
        }
        // check collision with speedup
        if (fa.getBody().getUserData() == "SPEED" || fb.getBody().getUserData() == "SPEED") {
            this.triggerSpeed();
            return;
        }
    }

    // tweak velocity of obstacles
    private void triggerSpeed() {
        System.out.println("Speed up");
        parent.playSound(IRModel.COLLECT_SOUND);
        parent.speedUp = true;
        long suTime = TimeUtils.millis();
        if(TimeUtils.millis() - suTime > 10) {
            parent.speedUp = false;
        }
    }


    // trigger heaviness
    private void triggerFat() {
        System.out.println("Fattened");
        parent.playSound(IRModel.COLLECT_SOUND);
        long fatTime = TimeUtils.millis();
        effectFat(10f);
        if(TimeUtils.millis() - fatTime > 2000) {
            effectFat(5f);
        }
    }

    private void effectFat(float density) {
        for (Fixture fix : parent.player.getFixtureList()) {
            fix.setDensity(density);
            parent.player.resetMassData();
        }
    }

    // buff effect valid for 2 seconds
    private void triggerBuff() {
        System.out.println("Buff collected");
        parent.playSound(IRModel.COLLECT_SOUND);
        parent.jumpHigh = true;
        long collectTime = TimeUtils.millis();
        if(TimeUtils.millis() - collectTime > 2000) { parent.jumpHigh = false; }
    }

    // debuff effect valid for 2 seconds
    private void triggerDebuff() {
        System.out.println("Debuff collected");
        parent.playSound(IRModel.COLLECT_SOUND);
        parent.jumpLow = true;
        long collectTime = TimeUtils.millis();
        if(TimeUtils.millis() - collectTime > 2000) { parent.jumpLow = false; }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
