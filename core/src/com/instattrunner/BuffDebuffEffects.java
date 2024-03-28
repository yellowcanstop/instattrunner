package com.instattrunner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.loader.ConstHub;

public class BuffDebuffEffects {
    private GameWorld parent;
    // Enum for obstacle, buff, debuff
    // buff/debuff category
    private static final int SPEED = ConstHub.BUSINESS_MAN_1_AI;
    private static final int SIZE = ConstHub.NUTRITION_MAJOR;
    private static final int JUMP = ConstHub.COFFEE;
    private static final int IMMUNE = ConstHub.DEAN;
    // buff
    private static final int BUSINESS_MAN_1_AI = ConstHub.BUSINESS_MAN_1_AI;
    private static final int NUTRITION_MAJOR = ConstHub.NUTRITION_MAJOR;
    private static final int DEAN = ConstHub.DEAN;
    // debuff
    private static final int SPORTS_SCIENCE_MAJOR = ConstHub.SPORTS_SCIENCE_MAJOR;
    private static final int CULINARY_MAJOR = ConstHub.CULINARY_MAJOR;
    
    /* Individual Buff Debuff
    * variables are declared here in logic model,
    * variables are edited in contact listener,
    * effects are activated and processed deactivated after x seconds in logic model (sorry, had to change to logic model as i'll be using the Body(s) in main screen, didn't feel like importing again to contact listener)
    */
    public long[] effectTime = new long[4];            // effect(buff and debuff of same category) start time
    public boolean[] effectActive = new boolean[4];    // effect(buff and debuff of same category) active or not 
    public boolean[] buffActive = new boolean[4];      // whether buff is active or not 
    public boolean[] debuffActive = new boolean[4];    // whether debuff is active or not (last one is a place holder to counter Dean buff)

    public boolean immunity = false;

    // Constructor
    public BuffDebuffEffects(GameWorld gameWorld){
        parent = gameWorld;
    }


    public void checkBuffDebuffExpire(){
        // for loop goes through all buff debuff category 
        // If found expired, turn them off 
        for (int i = 0; i < 4; i++){
            // Would exists where in same category, exact expire and obtain new buff/debuff, new buff/debuff would be cancelled, let it slip as it would be very computationaly expensive to handle
            if (effectActive[i] && TimeUtils.timeSinceMillis(effectTime[i]) > 10000){
                buffActive[i] = false;
                debuffActive[i] = false;
                effectActive[i] = false;    
                effectCancellation(i);
            }
        }
    }


    public void checkBuffDebuffPairs(){
        // for loop goes through all buff debuff category 
        // If found both active(cancel each other), turn them off 
        for (int i = 0; i < 4; i++){
            // Would exists where in same category, exact expire and obtain new buff/debuff, new buff/debuff would be cancelled, let it slip as it would be very computationaly expensive to handle
            if (buffActive[i] && debuffActive[i]){
                buffActive[i] = false;
                debuffActive[i] = false;
                effectActive[i] = false;    
                effectCancellation(i);
            }
        }
    }


    public void activateBuffDebuffEffect(){
        // Move on to process active buff/debuff and enable their effects
        // Change speed of obstacle logic
        if (effectActive[SPEED]) {
            if (buffActive[BUSINESS_MAN_1_AI]) 
                setSpeed(parent.velocityIncrement, ConstHub.slowMinSpawnInterval, ConstHub.slowSpeed);
            else if (debuffActive[SPORTS_SCIENCE_MAJOR])
                setSpeed(parent.velocityIncrement, ConstHub.fastMinSpawnInterval, ConstHub.fastSpeed);
        }

        // Change size of obstacle logic
        if (effectActive[SIZE]) {
            if (buffActive[NUTRITION_MAJOR])
                setSize(ConstHub.smallPlayerScale, parent.player, parent.smallPlayer, ConstHub.smallLowJump, ConstHub.smallNormalJump, ConstHub.smallHighJump);
            else if (debuffActive[CULINARY_MAJOR])
                setSize(ConstHub.bigPlayerScale, parent.player, parent.bigPlayer, ConstHub.bigLowJump, ConstHub.bigNormalJump, ConstHub.bigHighJump);
        }

        // Enable immunity of player
        if (effectActive[IMMUNE])
            immunity = true;
    }       


    private void effectCancellation(int effectType){
        switch (effectType) {
            case SPEED:
                setSpeed(parent.velocityIncrement, ConstHub.regularMinSpawnInterval, ConstHub.regularSpeed);
                break;

            case SIZE:
                setSize(ConstHub.regularPlayerScale, parent.player, parent.regularPlayer, ConstHub.regularLowJump, ConstHub.regularNormalJump, ConstHub.regularHighJump);
                break;

            case JUMP:
                break;

            case IMMUNE:
                immunity = false;
                break;

            default:
                System.out.println("Some error has occured while cancelling effects.");
        }
    }


    private void setSpeed(float velocity, long minSpawnInterval, float renderSpeed){
        // Set render variables to respective effect 
        parent.renderMinSpawnInterval = 850;
        parent.renderSpeed = renderSpeed;

        // Loop through all obstacles and set linear velocity to parameter (can be faster or slower, or regular)
        for (Body osbtacle : parent.obstacles) 
            osbtacle.setLinearVelocity(velocity + renderSpeed, 0);
    }


    private void setSize(float scale, Body currentPlayer, Body targetPlayer, int lowJump, int normalJump, int highJump){
        // Change playerScale that MainScreen uses to render texture of player
        parent.renderPlayerScale = scale;

        float lastPlayerPosY = currentPlayer.getPosition().y;
        Vector2 lastPlayerVelocity = currentPlayer.getLinearVelocity();
            
        // Put current player to sleep and move to right side of screen
        BodyData.setBodyObjectType(currentPlayer, "SLEEP_PLAYER");
        currentPlayer.setTransform(14f, (float)(parent.floor.getPosition().y + (ConstHub.floorWidHei.y / 2) + 0.001), currentPlayer.getAngle());

        // Wake up target player and move to left side of screen
        BodyData.setBodyObjectType(targetPlayer, "PLAYER");
        targetPlayer.setTransform(-14f, lastPlayerPosY, targetPlayer.getAngle());
        targetPlayer.setLinearVelocity(lastPlayerVelocity);

        // Set current player to target player
        parent.player = targetPlayer;
 
        // Set render low/normal/high jump to corresponding jump for body size
        parent.renderLowJump = lowJump;
        parent.renderNormalJump = normalJump;
        parent.renderHighJump = highJump;
    }

 
    public void resetImmune(){
        effectActive[DEAN] = false;
        buffActive[DEAN] = false;
        immunity = false;
    }
}

