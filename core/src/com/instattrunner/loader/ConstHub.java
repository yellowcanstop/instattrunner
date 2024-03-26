package com.instattrunner.loader;

import com.badlogic.gdx.math.Vector2;


public class ConstHub {
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


    // Images file names
    public final String bgImageName = "pic/background.jpg";
    public final String floorImageName = "pic/Floor.png";
    public final String playerImageName = "pic/Sprite.png";
    public final String[] obstacleImagesName = {"pic/Cat.png", "pic/Goose.png", "pic/Lake.png", "pic/Stairs.png"};
    public final String[] buffImagesName = {"pic/Business man 1 (AI).png", "pic/Nutrition major.png", "pic/Coffee.png", "pic/Dean.png"};
    public final String[] debuffImagesName = {"pic/Sports science major.png", "pic/Culinary major.png", "pic/Beer.png"};

    // Body height and width with respect to import
    // Multiply by scale when used
    public final Vector2 floorWidHei = new Vector2(32f, 3f);
    public final Vector2 playerWidHei = new Vector2(580.0f, 886.0f);
    public final Vector2[] obstacleWidHei = new Vector2[] {new Vector2(388.0f, 239.0f), new Vector2(554.0f, 427.0f), new Vector2(792.0f, 351.0f), new Vector2(499.0f, 616.0f)};
    public final Vector2[] buffWidHei = new Vector2[] {new Vector2(308.0f, 942.0f), new Vector2(487.0f, 935.0f), new Vector2(301.0f, 467.0f), new Vector2(334.0f, 935.0f)};
    public final Vector2[] debuffWidHei = new Vector2[] {new Vector2(809.0f, 467.0f), new Vector2(364.0f, 935.0f), new Vector2(360.0f, 468.0f)};

    // Scale of category of body
    public final float REFplayerScale = 0.007f;    // Used to store a reference copy (constant) of playerScale as playerScale will be changed throughout the game
    public float renderPlayerScale = REFplayerScale;
    public final float regularPlayerScale = 0.007f;
    public final float smallPlayerScale = 0.0054f;
    public final float bigPlayerScale = 0.0082f;
    public final float obstacleScale = 0.009f;
    public final float buffScale = 0.006f;
    public final float debuffScale = 0.0073f;

    // Sound effect files
    public final String jumpSoundName = "sounds/drop.wav";
    public final String collectSoundName = "sounds/drop.wav";

    // Music file
    public final String bgMusicName = "music/rain.mp3";
    public final String gmMusicName = "music/game.mp3";
 
    // Texture file
    public final String skinName = "skin/comic-ui.json";
}
