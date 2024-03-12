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

    // Images files
    public final String playerImage = "images/droplet.png";
    public final String[] obstacleImages = {"pic/Cat.png", "pic/Goose.png", "pic/Lake.png", "pic/Stairs.png"};
    public final String bgImage = "images/bg.jpg";
    public final String[] buffImages = {"pic/Alcohol.png", "Business man 2.png", "Coffee.png", "Culinary major.png", "Dean.png", "Nutrition major.png", "Sports science major.png"};

    // Sound effect files
    public final String jumpSound = "sounds/drop.wav";
    public final String collectSound = "sounds/drop.wav";

    // Music file
    public final String bgSound = "music/rain.mp3";
 
    // Texture file
    public final String skin = "skin/comic-ui.json";

    // Load images
    // Loop to loop through all value of string array which has all path of obstacles and buff
    public void queueAddImages(){
        manager.load(playerImage, Texture.class);
        for (String obstacle : obstacleImages)
            manager.load(obstacle, Texture.class);
        manager.load(bgImage, Texture.class);
        for (String buffImage : buffImages)
            manager.load(buffImage, Texture.class);
    }

    // Load sound effects
    public void queueAddSounds() {
        manager.load(jumpSound, Sound.class);
        manager.load(collectSound, Sound.class);
    }

    // Load music
    public void queueAddMusic() {
        manager.load(bgSound, Music.class);
    }

    // Load skin
    public void queueAddSkin() {
        SkinParameter params = new SkinParameter("skin/comic-ui.atlas");
        manager.load(skin, Skin.class, params);
    }
}
