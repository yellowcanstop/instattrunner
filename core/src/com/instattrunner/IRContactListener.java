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


        if (fa.getBody().getUserData() == "FLOOR" && fb.getBody().getUserData() == "PLAYER" || fb.getBody().getUserData() == "FLOOR" && fa.getBody().getUserData() == "PLAYER") {
            System.out.printf("Player is on ground at %f\n", fa.getBody().getPosition().y);
            parent.resetJump();
            
            return;
        }


        // check collision with coffee
        if (fa.getBody().getUserData() == "COFFEE" || fb.getBody().getUserData() == "COFFEE") {
            this.triggerCoffee();
            return;
        }
        // check collision with beer
        if (fa.getBody().getUserData() == "BEER" || fb.getBody().getUserData() == "BEER") {
            this.triggerBeer();
            return;
        }
        /*
        // check collision with fattening
        if (fa.getBody().getUserData() == "FAT" || fb.getBody().getUserData() == "FAT") {
            this.triggerFat();
            return;
        }

         */
        // check collision with sports major
        if (fa.getBody().getUserData() == "SPORTS" || fb.getBody().getUserData() == "SPORTS") {
            this.triggerSportsMajor();
            return;
        }
    }

    // sports major increases speed of gameplay for 10 seconds
    private void triggerSportsMajor() {
        System.out.println("Speed up obstacles");
        parent.playSound(IRModel.COLLECT_SOUND);
        parent.speedUp = true;
        long suTime = TimeUtils.millis();
        if(TimeUtils.millis() - suTime > 2000) { // todo change to 10 secs
            parent.speedUp = false;
        }
    }

    /*
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

     */

    // activate buff effect (deactivated in MainScreen after x seconds)
    private void triggerCoffee() {
        System.out.println("Coffee collected");
        parent.playSound(IRModel.COLLECT_SOUND);
        parent.jumpHigh = true;
        parent.coffeeActive = true;
        parent.coffeeTime = TimeUtils.millis();
    }

    // activate debuff effect (deactivated in MainScreen after x seconds)
    public void triggerBeer() {
        System.out.println("Beer collected");
        parent.playSound(IRModel.COLLECT_SOUND);
        parent.jumpLow = true;
        parent.beerActive = true;
        parent.beerTime = TimeUtils.millis();
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
