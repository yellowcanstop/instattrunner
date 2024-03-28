package com.instattrunner;

import com.instattrunner.loader.ConstHub;

public class JumpController {
    private GameWorld parent;

    // Enum for obstacle, buff, debuff
    private static final int COFFEE = ConstHub.COFFEE;
    private static final int BEER = ConstHub.BEER;

    // Variables for jump method
    private boolean canJump = true; // always true when player touches ground
    private boolean jumped = false;
    private int jumpCount = 0;

    
    public JumpController(GameWorld gameWorld){
        parent = gameWorld;
    }


    public void jumpLogic() {
        if (parent.buffDebuffEffectsClass.buffActive[COFFEE]) {
            if (parent.controller.space) {
                jumped = true;
                tweakJump(parent.renderHighJump);
            } 
            else if (!parent.controller.space && jumped) {
                canJump = false;
            }
        }

        else if (parent.buffDebuffEffectsClass.debuffActive[BEER]) {
            if (parent.controller.space) {
                jumped = true;
                tweakJump(parent.renderLowJump);
            } 
            else if (!parent.controller.space && jumped) {
                canJump = false;
            }
        }

        else if (parent.controller.space) {
            jumped = true;
            tweakJump(parent.renderNormalJump);
        } 

        else if (!parent.controller.space && jumped) {
            canJump = false;
            System.out.printf("Toggled canJump: %b  jumped: %b\n", canJump, jumped);
        }
    }


    private void tweakJump(int y) {
        if (parent.player.getPosition().y < 9 && canJump && jumpCount < 5) {
            parent.player.applyLinearImpulse(0, y, parent.player.getWorldCenter().x, parent.player.getWorldCenter().y, true);
            jumpCount++;
        } 
        else if (parent.player.getPosition().y > 9) {
            canJump = false;
        }
    }


    public void resetJump() {
        canJump = true;
        jumped = false;
        jumpCount = 0;
    }
}