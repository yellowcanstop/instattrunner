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
    public static final String bgImageName = "pic/background.jpg";
    public static final String floorImageName = "pic/Floor.png";
    public static final String playerImageName = "pic/Sprite.png";
    public static final String[] obstacleImagesName = {"pic/Cat.png", "pic/Goose.png", "pic/Lake.png", "pic/Stairs.png"};
    public static final String[] buffImagesName = {"pic/Business man 1 (AI).png", "pic/Nutrition major.png", "pic/Coffee.png", "pic/Dean.png"};
    public static final String[] debuffImagesName = {"pic/Sports science major.png", "pic/Culinary major.png", "pic/Beer.png"};

    // Body height and width with respect to import
    // Multiply by scale when used
    public static final Vector2 floorWidHei = new Vector2(32f, 3f);
    public static final Vector2 playerWidHei = new Vector2(580.0f, 886.0f);
    public static final Vector2[] obstacleWidHei = new Vector2[] {new Vector2(388.0f, 239.0f), new Vector2(554.0f, 427.0f), new Vector2(792.0f, 351.0f), new Vector2(499.0f, 616.0f)};
    public static final Vector2[] buffWidHei = new Vector2[] {new Vector2(308.0f, 942.0f), new Vector2(487.0f, 935.0f), new Vector2(301.0f, 467.0f), new Vector2(334.0f, 935.0f)};
    public static final Vector2[] debuffWidHei = new Vector2[] {new Vector2(809.0f, 467.0f), new Vector2(364.0f, 935.0f), new Vector2(360.0f, 468.0f)};

    // Scale of category of body
    public static final float obstacleScale = 0.009f;
    public static final float buffScale = 0.006f;
    public static final float debuffScale = 0.0073f;

    // Scale of player body when size change
    public static final float smallPlayerScale = 0.0054f;
    public static final float regularPlayerScale = 0.007f;
    public static final float bigPlayerScale = 0.0082f;

    // Impulse of jump when body size change
    public static final int smallLowJump = 55;
    public static final int smallNormalJump = 70;
    public static final int smallHighJump = 85;

    public static final int regularLowJump = 98;
    public static final int regularNormalJump = 111; 
    public static final int regularHighJump = 130;

    public static final int bigLowJump = 140;
    public static final int bigNormalJump = 158;
    public static final int bigHighJump = 182;

    // Speed of obstacle when speed change
    public static final float slowSpeed = -15f;
    public static final float regularSpeed = -20f;
    public static final float fastSpeed = -23f;

    // Minimum spawn time interval when speed change
    public static final long slowMinSpawnInterval = 1400;
    public static final long regularMinSpawnInterval = 1200;
    public static final long fastMinSpawnInterval = 900;

    // Sound effect files
    public static final String jumpSoundName = "sounds/drop.wav";
    public static final String collectSoundName = "sounds/drop.wav";

    // Music file
    public static final String bgMusicName = "music/rain.mp3";
    public static final String gmMusicName = "music/game.mp3";
 
    // Texture file
    public static final String skinName = "skin/comic-ui.json";
}