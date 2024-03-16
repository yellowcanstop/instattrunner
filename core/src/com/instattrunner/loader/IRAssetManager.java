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
public class IRAssetManager {
    public final AssetManager manager = new AssetManager();

    // Images files
    public final String playerImage = "pic/Sprite.png";
    public final String bgImage = "images/bg.jpg";
    public final String[] obstacleImages = {"pic/Cat.png", "pic/Goose.png", "pic/Lake.png", "pic/Stairs.png"};
    public final String[] buffImages = {"pic/Business man 1 (AI).png", "pic/Nutrition major.png", "pic/Dean.png", "pic/Coffee.png"};
    public final String[] debuffImages = {"pic/Sports science major.png", "pic/Culinary major.png", "pic/Beer.png"};

    // Body height and width with respect to import
    // Multiply by scale when used
    public final Vector2 playerWidHei = new Vector2(580.0f, 886.0f);
    public final Vector2[] obstacleWidHei = new Vector2[] {new Vector2(388.0f, 239.0f), new Vector2(554.0f, 427.0f), new Vector2(792.0f, 351.0f), new Vector2(499.0f, 616.0f)};
    public final Vector2[] buffWidHei = new Vector2[] {new Vector2(308.0f, 942.0f), new Vector2(487.0f, 935.0f), new Vector2(334.0f, 935.0f), new Vector2(301.0f, 467.0f)};
    public final Vector2[] debuffWidHei = new Vector2[] {new Vector2(809.0f, 467.0f), new Vector2(364.0f, 935.0f), new Vector2(360.0f, 468.0f)};

    // Scale of category of body
    public final float playerScale = 0.007f;
    public final float obstacleScale = 0.011f;
    public final float buffScale = 0.006f;
    public final float debuffScale = 0.006f;

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
        manager.load(bgImage, Texture.class);
        for (String obstacleImage : obstacleImages)
            manager.load(obstacleImage, Texture.class);
        for (String buffImage : buffImages)
            manager.load(buffImage, Texture.class);
        for (String debuffImage : debuffImages)
            manager.load(debuffImage, Texture.class);
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
