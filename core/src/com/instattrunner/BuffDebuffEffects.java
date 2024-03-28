package com.instattrunner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.instattrunner.loader.ConstHub;

public class BuffDebuffEffects {
    // Enum for obstacle, buff, debuff
    // buff/debuff category
    private static final int SPEED = ConstHub.BUSINESS_MAN_1_AI;
    private static final int SIZE = ConstHub.NUTRITION_MAJOR;
    private static final int JUMP = ConstHub.COFFEE;
    private static final int IMMUNE = ConstHub.DEAN;
    // buff
    private static final int BUSINESS_MAN_1_AI = ConstHub.BUSINESS_MAN_1_AI;
    private static final int NUTRITION_MAJOR = ConstHub.NUTRITION_MAJOR;
    private static final int COFFEE = ConstHub.COFFEE;
    private static final int DEAN = ConstHub.DEAN;
    // debuff
    private static final int SPORTS_SCIENCE_MAJOR = ConstHub.SPORTS_SCIENCE_MAJOR;
    private static final int CULINARY_MAJOR = ConstHub.CULINARY_MAJOR;
    private static final int BEER = ConstHub.BEER;
    
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
    public BuffDebuffEffects(){

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
            if (buffActive[BUSINESS_MAN_1_AI]) {
                setSpeed(velocityIncrement + (-14));
                main.minSpawnInterval = 1600;
            } else if (debuffActive[SPORTS_SCIENCE_MAJOR]) {
                setSpeed(velocityIncrement + (-30));
                main.minSpawnInterval = 850;
            }
        }

        // Change size of obstacle logic
        if (effectActive[SIZE]) {
            if (buffActive[NUTRITION_MAJOR])
                setSize(0.0054f);
            else if (debuffActive[CULINARY_MAJOR])
                setSize(0.0082f);
        }

        // Enable immunity of player
        if (effectActive[IMMUNE])
            immunity = true;
    }       


    private void effectCancellation(int effectType){
        switch (effectType) {
            case SPEED:
                setSpeed(velocityIncrement + (-20));
                main.minSpawnInterval = 1000;
                break;

            case SIZE:
                setSize(assMan.REFplayerScale);
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

    private void setSpeed(int velocity){
        // Loop through all obstacles and set linear velocity to parameter (can be faster or slower, or regular)
        for (Body osbtacle : obstacles) 
            osbtacle.setLinearVelocity((float) velocity, 0);
    }

    private void setSize(float scale){
        // Change playerScale that MainScreen uses to render texture of player
        assMan.playerScale = scale;

        float lastPlayerPosY = player.getPosition().y;
        Vector2 lastPlayerVelocity = player.getLinearVelocity();
            
        setBodyObjectType(regularPlayer, "SLEEP_PLAYER");
        regularPlayer.setTransform(14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001), regularPlayer.getAngle());
        setBodyObjectType(smallPlayer, "SLEEP_PLAYER");
        smallPlayer.setTransform(14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001), smallPlayer.getAngle());
        setBodyObjectType(bigPlayer, "SLEEP_PLAYER");
        bigPlayer.setTransform(14f, (float)(floor.getPosition().y + (floorWidHei.y / 2) + 0.001), bigPlayer.getAngle());

       // Set player to different sizes depending on parameter
        if (scale == 0.0054f){
            player = smallPlayer;
            NORMAL = 78;
            HIGH = 100;
            LOW = 35;
        }
        else if (scale == 0.007f){
            player = regularPlayer;
            NORMAL = 115;
            HIGH = 135;
            LOW = 73;
        }

        else if (scale == 0.0082f){
            player = bigPlayer;
            NORMAL = 160;
            HIGH = 185;
            LOW = 115;
        }
        else 
            System.out.println("Some error has occured while changing sizes.");

        setBodyObjectType(player, "PLAYER");
        player.setTransform(-14f, lastPlayerPosY, player.getAngle());
        player.setLinearVelocity(lastPlayerVelocity);
    }

 
    public void resetImmune(){
        effectActive[DEAN] = false;
        buffActive[DEAN] = false;
        immunity = false;
    }
}

