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

    // Enum for obstacle, buff, debuff
    // obstacle
    public static final int CAT = 0;
    public static final int GOOSE = 1;
    public static final int LAKE = 2;
    public static final int STAIRS = 3;
    // buff
    public static final int BUSINESS_MAN_1_AI = 0;
    public static final int NUTRITION_MAJOR = 1;
    public static final int COFFEE = 2;
    public static final int DEAN = 3;
    // debuff
    public static final int SPORTS_SCIENCE_MAJOR = 0;
    public static final int CULINARY_MAJOR = 1;
    public static final int BEER = 2;

    // Images files
    public final String floorImage = "pic/Floor.png";
    public final String playerImage = "pic/Sprite.png";
    public final String bgImage = "images/bg.jpg";
    public final String[] obstacleImages = {"pic/Cat.png", "pic/Goose.png", "pic/Lake.png", "pic/Stairs.png"};
    public final String[] buffImages = {"pic/Business man 1 (AI).png", "pic/Nutrition major.png", "pic/Coffee.png", "pic/Dean.png"};
    public final String[] debuffImages = {"pic/Sports science major.png", "pic/Culinary major.png", "pic/Beer.png"};

    // Body height and width with respect to import
    // Multiply by scale when used
    public final Vector2 floorWidHei = new Vector2(32f, 3f);
    public final Vector2 playerWidHei = new Vector2(580.0f, 886.0f);
    public final Vector2[] obstacleWidHei = new Vector2[] {new Vector2(388.0f, 239.0f), new Vector2(554.0f, 427.0f), new Vector2(792.0f, 351.0f), new Vector2(499.0f, 616.0f)};
    public final Vector2[] buffWidHei = new Vector2[] {new Vector2(308.0f, 942.0f), new Vector2(487.0f, 935.0f), new Vector2(301.0f, 467.0f), new Vector2(334.0f, 935.0f)};
    public final Vector2[] debuffWidHei = new Vector2[] {new Vector2(809.0f, 467.0f), new Vector2(364.0f, 935.0f), new Vector2(360.0f, 468.0f)};

    // Scale of category of body
    public final float REFplayerScale = 0.007f;    // Used to store a reference copy (constant) of playerScale as playerScale will be changed throughout the game
    public float playerScale = REFplayerScale;
    public final float obstacleScale = 0.009f;
    public final float buffScale = 0.006f;
    public final float debuffScale = 0.0073f;

    // Sound effect files
    public final String jumpSound = "sounds/drop.wav";
    public final String collectSound = "sounds/drop.wav";

    // Music file
    public final String bgSound = "music/rain.mp3";
    public final String gmSound = "music/game.mp3";
 
    // Texture file
    public final String skin = "skin/comic-ui.json";

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
