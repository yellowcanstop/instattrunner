package com.instattrunner.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

// load items asynchronously, keep all assets together,
// stop loading duplicate resources,
// keep ref of used assets until no other assets ref it
// hence help reduce memory usage
public class IRAssetManager {
    public final AssetManager manager = new AssetManager();

    // Textures
    public final String playerImage = "images/droplet.png";
    public final String obstacleImage = "images/bucket.png";
    public final String bgImage = "images/bg.jpg";

    // states images we want to load are textures
    // and should be queued for loading
    public void queueAddImages(){
        manager.load(playerImage, Texture.class);
        manager.load(obstacleImage, Texture.class);
        manager.load(bgImage, Texture.class);
    }

    // Sounds: played in the game (loaded into model)
    public final String jumpSound = "sounds/drop.wav";
    // todo: diff sound for collect
    public final String collectSound = "sounds/drop.wav";
    public void queueAddSounds() {
        manager.load(jumpSound, Sound.class);
        manager.load(collectSound, Sound.class);
    }

    // Music: played as soon as open the game (loaded into parent's create())
    public final String bgSound = "music/rain.mp3";
    public void queueAddMusic() {
        manager.load(bgSound, Music.class);
    }

    // load skin into asset manager (multiple files hence need to add parameter)
    public final String skin = "skin/comic-ui.json";
    public void queueAddSkin() {
        SkinParameter params = new SkinParameter("skin/comic-ui.atlas");
        manager.load(skin, Skin.class, params);
    }
}
