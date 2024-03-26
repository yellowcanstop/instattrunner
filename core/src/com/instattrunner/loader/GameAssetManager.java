package com.instattrunner.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.instattrunner.ScreenManager;

/* load items asynchronously, keep all assets together,
stop loading duplicate resources,
keep ref of used assets until no other assets ref it
hence help reduce memory usage */
public class GameAssetManager {
    public final AssetManager manager = new AssetManager();
    private ConstHub locCHub;


    // Constructor
    public GameAssetManager(ScreenManager scMan) {
        locCHub = scMan.constHub;
    }


    // Load images
    // Loop to loop through all value of string array which has all path of obstacles and buff
    public void queueAddImages(){
        manager.load(locCHub.floorImage, Texture.class);
        manager.load(locCHub.playerImage, Texture.class);
        manager.load(locCHub.bgImage, Texture.class);

        for (String obstacleImage : locCHub.obstacleImages)
            manager.load(obstacleImage, Texture.class);

        for (String buffImage : locCHub.buffImages)
            manager.load(buffImage, Texture.class);

        for (String debuffImage : locCHub.debuffImages)
            manager.load(debuffImage, Texture.class);
    }


    // Load sounds
    public void queueAddSounds() {
        manager.load(locCHub.jumpSound, Sound.class);
        manager.load(locCHub.collectSound, Sound.class);
    }


    // Load music
    public void queueAddMusic() {
        manager.load(locCHub.bgSound, Music.class);
        manager.load(locCHub.gmSound, Music.class);
    }


    // Load skin
    public void queueAddSkin() {
        SkinParameter params = new SkinParameter("skin/comic-ui.atlas");
        manager.load(locCHub.skin, Skin.class, params);
    }
}