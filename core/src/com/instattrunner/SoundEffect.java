package com.instattrunner;

import com.badlogic.gdx.audio.Sound;
import com.instattrunner.loader.GameAssetManager;

public class SoundEffect {
    // Declare object Sound to store sound loaded from asset manager
    public Sound jump;
    public Sound collect;

    // enum for sound 
    public static final int JUMP_SOUND = 0;
    public static final int COLLECT_SOUND = 1;


    public SoundEffect(GameAssetManager assetMan){
         // load sounds into model
        assetMan.queueAddSounds();
        assetMan.manager.finishLoading();
        jump = assetMan.manager.get("sounds/drop.wav");
        collect = assetMan.manager.get("sounds/drop.wav");
    }


    public void playSound(int sound) {
        switch(sound) {
            case JUMP_SOUND:
                jump.play();
                break;
            case COLLECT_SOUND:
                collect.play();
                break;
        }
    }
}