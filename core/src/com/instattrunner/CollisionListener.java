package com.instattrunner;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;

// contact listener to react to collisions in world
// everytime two bodies collide, beginContact is run
public class CollisionListener implements ContactListener {
    private GameWorld parent;
    private SoundEffect soundEffectClass;

    // For output to terminal for debugging
    public final String[] buffTypes = {"Business man 1 (AI)", "Nutrition major", "Coffee", "Dean"};
    public final String[] debuffTypes = {"Sports science major", "Culinary major", "Beer"};

    // Just to temporary hold id of buff/debuff
    private int tempTextureId;


    // Constructor
    public CollisionListener(GameWorld parent) {
        this.parent = parent;
        soundEffectClass = new SoundEffect(parent.assMan);
    }


    // each body has a fixture which is the colliding part (store in fa, fb)
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();


        // Check player collide with obstacle
        if ((BodyData.getBodyObjectType(fa.getBody()) == "OBSTACLE" && BodyData.getBodyObjectType(fb.getBody()) == "PLAYER") || (BodyData.getBodyObjectType(fb.getBody()) == "OBSTACLE" && BodyData.getBodyObjectType(fa.getBody()) == "PLAYER")) {
            System.out.println("Player hit obstacle");
            if (BodyData.getBodyObjectType(fa.getBody()) == "OBSTACLE")
                parent.collideObstacle = fa.getBody();
            else 
                parent.collideObstacle = fb.getBody();

            parent.isDead = true; // triggers change to end screen from render() in main
            return;
        }


        // Check player collide with ground to determine whether player on ground or not 
        if ((BodyData.getBodyObjectType(fa.getBody()) == "FLOOR" && BodyData.getBodyObjectType(fb.getBody()) == "PLAYER") || (BodyData.getBodyObjectType(fb.getBody()) == "FLOOR" && BodyData.getBodyObjectType(fa.getBody()) == "PLAYER")) {
            System.out.printf("Player is on ground at %f\n", fa.getBody().getPosition().y);
            parent.jumpControllerClass.resetJump();
            return;
        }


        // Check player collide with buff and produce relevant effect 
        if ((BodyData.getBodyObjectType(fa.getBody()) == "BUFF" && BodyData.getBodyObjectType(fb.getBody()) == "PLAYER") || (BodyData.getBodyObjectType(fb.getBody()) == "BUFF" && BodyData.getBodyObjectType(fa.getBody()) == "PLAYER")) {
            // Get TextureId of buff
            if (BodyData.getBodyObjectType(fa.getBody()) == "BUFF") {
                tempTextureId = BodyData.getTextureId(fa.getBody());
                parent.collideDeBuff = fa.getBody();
            }
            
            else {   
                tempTextureId = BodyData.getTextureId(fb.getBody());
                parent.collideDeBuff = fb.getBody();
            }
            
            System.out.printf("Buff : %s\n", buffTypes[tempTextureId]);
            soundEffectClass.playSound(SoundEffect.COLLECT_SOUND);
            parent.buffDebuffEffectsClass.effectTime[tempTextureId] = TimeUtils.millis();
            parent.buffDebuffEffectsClass.effectActive[tempTextureId] = true;
            parent.buffDebuffEffectsClass.buffActive[tempTextureId] = true;
            return;
        }


        // Check player collide with debuff and produce relevant effect 
        if ((BodyData.getBodyObjectType(fa.getBody()) == "DEBUFF" && BodyData.getBodyObjectType(fb.getBody()) == "PLAYER") || (BodyData.getBodyObjectType(fb.getBody()) == "DEBUFF" && BodyData.getBodyObjectType(fa.getBody()) == "PLAYER")) {
            // Get TextureId of buff
            if (BodyData.getBodyObjectType(fa.getBody()) == "DEBUFF"){
                tempTextureId = BodyData.getTextureId(fa.getBody());
                parent.collideDeBuff = fa.getBody();
            }

            else {
                tempTextureId = BodyData.getTextureId(fb.getBody());
                parent.collideDeBuff = fb.getBody();
            }

            System.out.printf("Debuff : %s\n", debuffTypes[tempTextureId]);
            soundEffectClass.playSound(SoundEffect.COLLECT_SOUND);
            parent.buffDebuffEffectsClass.effectTime[tempTextureId] = TimeUtils.millis();
            parent.buffDebuffEffectsClass.effectActive[tempTextureId] = true;
            parent.buffDebuffEffectsClass.debuffActive[tempTextureId] = true;
            return;
        }
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