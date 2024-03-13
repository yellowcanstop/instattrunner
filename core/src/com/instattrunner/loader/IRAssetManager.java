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
public class IRAssetManager {
    public final AssetManager manager = new AssetManager();
    // Load images
    public final String playerImage = "images/droplet.png";
    public final String obstacleImage = "images/bucket.png";
    public final String bgImage = "images/bg.jpg";
    public final String coffeeImage = "images/test_coffee.png";
    public final String beerImage = "images/test_beer.png";
    public final String sportsMajorImage = "images/test_sports.png";
    public final String bizMajorImage = "images/test_biz.png";
    public void queueAddImages(){
        manager.load(playerImage, Texture.class);
        manager.load(obstacleImage, Texture.class);
        manager.load(bgImage, Texture.class);
        manager.load(coffeeImage, Texture.class);
        manager.load(beerImage, Texture.class);
        manager.load(sportsMajorImage, Texture.class);
        manager.load(bizMajorImage, Texture.class);
    }
    // Load sound effects
    public final String jumpSound = "sounds/drop.wav";
    public final String collectSound = "sounds/drop.wav";
    public void queueAddSounds() {
        manager.load(jumpSound, Sound.class);
        manager.load(collectSound, Sound.class);
    }
    // Load music
    public final String bgSound = "music/rain.mp3";
    public void queueAddMusic() {
        manager.load(bgSound, Music.class);
    }
    // Load skin
    public final String skin = "skin/comic-ui.json";
    public void queueAddSkin() {
        SkinParameter params = new SkinParameter("skin/comic-ui.atlas");
        manager.load(skin, Skin.class, params);
    }
}
