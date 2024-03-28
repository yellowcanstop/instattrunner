package com.instattrunner.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/* load items asynchronously, keep all assets together,
stop loading duplicate resources,
keep ref of used assets until no other assets ref it
hence help reduce memory usage */
public class GameAssetManager {
    public final AssetManager manager = new AssetManager();


    // Load background image
    public void queueAddBackground(){
        manager.load(ConstHub.bgImageName, Texture.class);
    }


    // Load images
    // Loop to loop through all value of string array which has all path of obstacles and buff
    public void queueAddImages(){
        manager.load(ConstHub.floorImageName, Texture.class);
        manager.load(ConstHub.playerImageName, Texture.class);

        for (String obstacleImage : ConstHub.obstacleImagesName)
            manager.load(obstacleImage, Texture.class);

        for (String buffImage : ConstHub.buffImagesName)
            manager.load(buffImage, Texture.class);

        for (String debuffImage : ConstHub.debuffImagesName)
            manager.load(debuffImage, Texture.class);
    }


    // Load sounds
    public void queueAddSounds() {
        manager.load(ConstHub.jumpSoundName, Sound.class);
        manager.load(ConstHub.collectSoundName, Sound.class);
    }


    // Load music
    public void queueAddMusic() {
        manager.load(ConstHub.bgMusicName, Music.class);
        manager.load(ConstHub.gmMusicName, Music.class);
    }


    // Load skin
    public void queueAddSkin() {
        SkinParameter params = new SkinParameter("skin/comic-ui.atlas");
        manager.load(ConstHub.skinName, Skin.class, params);
    }
}