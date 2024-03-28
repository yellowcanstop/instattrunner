package com.instattrunner;

import java.util.Iterator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;

public class SpawnNTrack {
    private GameWorld parent;


    public SpawnNTrack(GameWorld gameWorld){
        parent = gameWorld;
    }


    public void spawnObstacles(float v) {
        parent.obstacles.add(parent.obstacleClass.createObstacle(v));
        parent.obstacleTimestamp = TimeUtils.millis();
        parent.obstacleSpawnInterval = parent.renderMinSpawnInterval + (300 * parent.random.nextInt(6));
    }


    public void spawnBuffs() {
        parent.buffs.add(parent.buffClass.createBuff());
        parent.buffDebuffTimestamp = TimeUtils.millis();
        parent.buffDebuffSpawnInterval = (parent.renderMinSpawnInterval * 4) + (300 * parent.random.nextInt(6));
    }


    public void spawnDebuffs() {
        parent.debuffs.add(parent.debuffClass.createDebuff());
        parent.buffDebuffTimestamp = TimeUtils.millis();
        parent.buffDebuffSpawnInterval = (parent.renderMinSpawnInterval * 4) + (300 * parent.random.nextInt(6));
    }


    public void trackObstacles() {
        for (Iterator<Body> iter = parent.obstacles.iterator(); iter.hasNext(); ) {
            Body obstacle = iter.next();
            if (obstacle.getPosition().x < -25) {  // -16 + (-9)  (9 is aprox max unit size of obstacle)
                System.out.println("Score: " + parent.score);
                parent.score++;
                iter.remove();
                // Set velocity increment
                parent.velocityIncrement = (int) (parent.score / 10) * 3;
            }
        }
    }


    // Check if buff/debuff is out of screen
    // If true, remove and discard
    public void trackBuffsDebuffs() {
        for (Iterator<Body> iter = parent.buffs.iterator(); iter.hasNext(); ) {
            Body buff = iter.next();
            if (buff.getPosition().x < -21)  // -16 + (-5)  (5 is aprox max unit size of buff/debuff) 
                iter.remove();
        }
        for (Iterator<Body> iter = parent.debuffs.iterator(); iter.hasNext(); ) {
            Body debuff = iter.next();
            if (debuff.getPosition().x < -21) 
                iter.remove();
        }
    }
}