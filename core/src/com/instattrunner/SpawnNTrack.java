package com.instattrunner;

import java.util.Iterator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;


public class SpawnNTrack {
    private GameWorld container;

    // To save whether velocity is incremented, to determine whether to change speed of all obstacles
    private boolean incremented;


    public SpawnNTrack(GameWorld gameWorld){
        container = gameWorld;
    }


    public void spawnObstacles(float v) {
        container.obstacles.add(container.obstacleClass.createObstacle(v));
        container.obstacleTimestamp = TimeUtils.millis();
        container.obstacleSpawnInterval = container.renderMinSpawnInterval + ((long) (container.renderMinSpawnInterval / 5) * container.random.nextInt(4));
    }


    public void spawnBuffs() {
        container.buffs.add(container.buffClass.createBuff());
        container.buffDebuffTimestamp = TimeUtils.millis();
        container.buffDebuffSpawnInterval = (container.renderMinSpawnInterval * 4) + (300 * container.random.nextInt(6));
    }


    public void spawnDebuffs() {
        container.debuffs.add(container.debuffClass.createDebuff());
        container.buffDebuffTimestamp = TimeUtils.millis();
        container.buffDebuffSpawnInterval = (container.renderMinSpawnInterval * 4) + (300 * container.random.nextInt(6));
    }


    public void trackObstacles() {
        for (Iterator<Body> iter = container.obstacles.iterator(); iter.hasNext(); ) {
            Body obstacle = iter.next();
            if (obstacle.getPosition().x < -25) {  // -16 + (-9)  (9 is aprox max unit size of obstacle)
                System.out.println("Score: " + container.score);
                container.score++;
                iter.remove();
                // Set velocity increment
                container.velocityIncrement = ((int) (container.score / 10)) * 6;
                container.spawnIntervalDecrement = container.velocityIncrement * 45;
                incremented = true;
            }
        }
        if (incremented){
            container.buffDebuffEffectsClass.setSpeed(container.velocityIncrement, container.renderMinSpawnInterval, container.renderSpeed);
            incremented = false;
        }
    }


    // Check if buff/debuff is out of screen
    // If true, remove and discard
    public void trackBuffsDebuffs() {
        for (Iterator<Body> iter = container.buffs.iterator(); iter.hasNext(); ) {
            Body buff = iter.next();
            if (buff.getPosition().x < -21)  // -16 + (-5)  (5 is aprox max unit size of buff/debuff) 
                iter.remove();
        }
        for (Iterator<Body> iter = container.debuffs.iterator(); iter.hasNext(); ) {
            Body debuff = iter.next();
            if (debuff.getPosition().x < -21) 
                iter.remove();
        }
    }
}