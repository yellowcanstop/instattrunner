package com.instattrunner;

import com.instattrunner.loader.ConstHub;

public class JumpController {
    private GameWorld containter;

    // Enum for obstacle, buff, debuff
    private static final int COFFEE = ConstHub.COFFEE;
    private static final int BEER = ConstHub.BEER;

    // Variables for jump method
    private boolean canJump = true; // always true when player touches ground
    private boolean jumped = false;
    private int jumpCount = 0;

    
    public JumpController(GameWorld gameWorld){
        containter = gameWorld;
    }


    public void jumpLogic() {
        if (containter.buffDebuffEffectsClass.buffActive[COFFEE]) {
            if (containter.controller.space) {
                jumped = true;
                tweakJump(containter.renderHighJump);
            } 
            else if (!containter.controller.space && jumped) {
                canJump = false;
            }
        }

        else if (containter.buffDebuffEffectsClass.debuffActive[BEER]) {
            if (containter.controller.space) {
                jumped = true;
                tweakJump(containter.renderLowJump);
            } 
            else if (!containter.controller.space && jumped) {
                canJump = false;
            }
        }

        else if (containter.controller.space) {
            jumped = true;
            tweakJump(containter.renderNormalJump);
        } 

        else if (!containter.controller.space && jumped) {
            canJump = false;
            System.out.printf("Toggled canJump: %b  jumped: %b\n", canJump, jumped);
        }
    }


    private void tweakJump(int y) {
        if (containter.player.getPosition().y < 9 && canJump && jumpCount < 5) {
            containter.player.applyLinearImpulse(0, y, containter.player.getWorldCenter().x, containter.player.getWorldCenter().y, true);
            jumpCount++;
        } 
        else if (containter.player.getPosition().y > 9) {
            canJump = false;
        }
    }


    public void resetJump() {
        canJump = true;
        jumped = false;
        jumpCount = 0;
    }
}