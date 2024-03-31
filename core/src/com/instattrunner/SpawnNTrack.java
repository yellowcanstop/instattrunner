package com.instattrunner;

import java.util.Iterator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;


public class SpawnNTrack {
    private GameWorld containter;


    public SpawnNTrack(GameWorld gameWorld){
        containter = gameWorld;
    }


    public void spawnObstacles(float v) {
        containter.obstacles.add(containter.obstacleClass.createObstacle(v));
        containter.obstacleTimestamp = TimeUtils.millis();
        containter.obstacleSpawnInterval = containter.renderMinSpawnInterval + (300 * containter.random.nextInt(6));
    }


    public void spawnBuffs() {
        containter.buffs.add(containter.buffClass.createBuff());
        containter.buffDebuffTimestamp = TimeUtils.millis();
        containter.buffDebuffSpawnInterval = (containter.renderMinSpawnInterval * 4) + (300 * containter.random.nextInt(6));
    }


    public void spawnDebuffs() {
        containter.debuffs.add(containter.debuffClass.createDebuff());
        containter.buffDebuffTimestamp = TimeUtils.millis();
        containter.buffDebuffSpawnInterval = (containter.renderMinSpawnInterval * 4) + (300 * containter.random.nextInt(6));
    }


    public void trackObstacles() {
        for (Iterator<Body> iter = containter.obstacles.iterator(); iter.hasNext(); ) {
            Body obstacle = iter.next();
            if (obstacle.getPosition().x < -25) {  // -16 + (-9)  (9 is aprox max unit size of obstacle)
                System.out.println("Score: " + containter.score);
                containter.score++;
                iter.remove();
                // Set velocity increment
                containter.velocityIncrement = (int) (containter.score / 10) * 7;
            }
        }
    }


    // Check if buff/debuff is out of screen
    // If true, remove and discard
    public void trackBuffsDebuffs() {
        for (Iterator<Body> iter = containter.buffs.iterator(); iter.hasNext(); ) {
            Body buff = iter.next();
            if (buff.getPosition().x < -21)  // -16 + (-5)  (5 is aprox max unit size of buff/debuff) 
                iter.remove();
        }
        for (Iterator<Body> iter = containter.debuffs.iterator(); iter.hasNext(); ) {
            Body debuff = iter.next();
            if (debuff.getPosition().x < -21) 
                iter.remove();
        }
    }
}