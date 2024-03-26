package com.instattrunner.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/* load items asynchronously, keep all assets together,
stop loading duplicate resources,
keep ref of used assets until no other assets ref it
hence help reduce memory usage */
public class GameAssetManager {
    public final AssetManager manager = new AssetManager();

   
    // Load images
    // Loop to loop through all value of string array which has all path of obstacles and buff
    public void queueAddImages(){
        manager.load(floorImage, Texture.class);
        manager.load(playerImage, Texture.class);
        manager.load(bgImage, Texture.class);
        for (String obstacleImage : obstacleImages)
            manager.load(obstacleImage, Texture.class);
        for (String buffImage : buffImages)
            manager.load(buffImage, Texture.class);
        for (String debuffImage : debuffImages)
            manager.load(debuffImage, Texture.class);
    }

    // Load sounds
    public void queueAddSounds() {
        manager.load(jumpSound, Sound.class);
        manager.load(collectSound, Sound.class);
    }

    // Load music
    public void queueAddMusic() {
        manager.load(bgSound, Music.class);
        manager.load(gmSound, Music.class);
    }
    // Load skin
    public void queueAddSkin() {
        SkinParameter params = new SkinParameter("skin/comic-ui.atlas");
        manager.load(skin, Skin.class, params);
    }
}
