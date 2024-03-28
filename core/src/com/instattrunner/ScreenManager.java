package com.instattrunner;

import com.badlogic.gdx.Game; // game class used to delegate between different screens
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.instattrunner.loader.ConstHub;
import com.instattrunner.loader.GameAssetManager;
import com.instattrunner.views.EndScreen;
import com.instattrunner.views.GameScreen;
import com.instattrunner.views.HelpScreen;
import com.instattrunner.views.MenuScreen;
import com.instattrunner.views.ScoreScreen;

/* This class is parent of model, views, controllers:
- model for game data and logic processing
- views for displaying and rendering
- controllers for mapping user input to methods in model */
public class ScreenManager extends Game {
    // Constants Hub and Asset Manager
    public GameAssetManager assMan = new GameAssetManager();

    // Screens
	private MenuScreen menuScreen;
    private GameScreen gameScreen;
	private HelpScreen helpScreen;
    private ScoreScreen scoreScreen;
	private EndScreen endScreen;

    // Background Music
	private Music bgMusic;
	private Music gmMusic;

    // Enum for changeScreen method
	public final static int MENU = 0;
	public final static int PLAY = 1;
	public final static int HELP = 2;
	public final static int SCORE = 4;
	public final static int END = 3;

    // Background texture to pass to screens
    public Texture backgroundTexture;

    // Set size of screen
	public final int VIEW_WIDTH = 800;
	public final int VIEW_HEIGHT = 600;

	public int finalScore = 0; // set value when player dies

    
	// Set default screen when application opens
	@Override
	public void create() {
        // Load background for all other screens
        assMan.queueAddBackground();
        assMan.manager.finishLoading();
        backgroundTexture = assMan.manager.get(ConstHub.bgImageName);

		menuScreen = new MenuScreen(this);
		setScreen(menuScreen);

        // Load and play music
        assMan.queueAddMusic();
		assMan.manager.finishLoading();
		bgMusic = assMan.manager.get(ConstHub.bgMusicName);
		gmMusic = assMan.manager.get(ConstHub.gmMusicName);
		bgMusic.play();
    }


	// Method to swap between screens
	public void changeScreen(int screen) {
		switch(screen) {
			case MENU:
				if (menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case PLAY:
				if (gameScreen == null) gameScreen = new GameScreen(this);
				bgMusic.stop();
				gmMusic.play();
				this.setScreen(gameScreen);
				break;
			case HELP:
				if (helpScreen == null) helpScreen = new HelpScreen(this);
				this.setScreen(helpScreen);
				break;
			case SCORE:
				if (scoreScreen == null) scoreScreen = new ScoreScreen(this);
				this.setScreen(scoreScreen);
				break;

			case END:
				if (endScreen == null) endScreen = new EndScreen(this);
				gmMusic.stop();
				bgMusic.play();
				this.setScreen(endScreen);
				gameScreen = null;
				break;
		}
	}


	@Override
	public void dispose() {
		assMan.manager.dispose();
		bgMusic.dispose();
        gmMusic.dispose();
	}
}