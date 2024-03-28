package com.instattrunner;

import com.badlogic.gdx.physics.box2d.Body;

public class BodyData {
    public String bodyObjectType;
    public final int textureId;

    public BodyData (String bodObjType, int texId){
        bodyObjectType = bodObjType;
        textureId = texId;
    }
    

    // Takes Body
    // Returns BodyObjectType (player, obstacle, buff, debuff)
    // Used to check what Body it is (mostly in contact listener)
    public static String getBodyObjectType(Body bod){
        return ((BodyData) bod.getUserData()).bodyObjectType;
    }


    // Takes Body, bodyObjectType
    // Sets bodyObjectType to BodyData (SLEEP_PLAYER, PLAYER)
    // Used to manipulate which body to use
    public static void setBodyObjectType(Body bod, String bodObjType){
        ((BodyData) bod.getUserData()).bodyObjectType = bodObjType;
    }


    // Takes Body
    // Returns TextureId (int)
    // Used to check what texture Body is using (mostly for obstacle, buff, debuff) (mostly used for hitbox and rendering)
    public static int getTextureId(Body bod){
        return ((BodyData) bod.getUserData()).textureId;
    }
}