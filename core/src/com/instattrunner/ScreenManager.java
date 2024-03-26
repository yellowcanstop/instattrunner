package com.instattrunner;

import com.badlogic.gdx.Game; // game class used to delegate between different screens
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.instattrunner.loader.ConstHub;
import com.instattrunner.loader.GameAssetManager;
import com.instattrunner.views.EndScreen;
import com.instattrunner.views.GameScreen;
import com.instattrunner.views.HelpScreen;
import com.instattrunner.views.MainScreen;
import com.instattrunner.views.MenuScreen;
import com.instattrunner.views.ScoreScreen;
import com.instattrunner.views.HighscoreScreen;

/* This class is parent of model, views, controllers:
- model for game data and logic processing
- views for displaying and rendering
- controllers for mapping user input to methods in model */
public class ScreenManager extends Game {
    // Constants Hub and Asset Manager
    public ConstHub constHub = new ConstHub();
    public GameAssetManager assMan = new GameAssetManager(this);

    // Screens
	private MenuScreen menuScreen;
    private GameScreen gameScreen;
	private HelpScreen helpScreen;
    private ScoreScreen scoreScreen;
	private EndScreen endScreen;






    // Background Music
	private Music bgMusic;
	private Music gmMusic;

	public int finalScore = 0; // set value when player dies

	public final static int VIEW_WIDTH = 800;
	public final static int VIEW_HEIGHT = 600;

	public final static int MENU = 0;
	public final static int PLAY = 1;
	public final static int HELP = 2;
	public final static int SCORE = 4;
	public final static int END = 3;




	// Set default screen when application opens
	@Override
	public void create() {
		menuScreen = new MenuScreen(this);
		setScreen(menuScreen);


		// Load and play music
		assetMan.queueAddMusic();
		assetMan.manager.finishLoading();
		bgMusic = assetMan.manager.get("music/rain.mp3");
		gmMusic = assetMan.manager.get("music/game.mp3");
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
				if (mainScreen == null) mainScreen = new MainScreen(this);
				bgMusic.stop();
				gmMusic.play();
				this.setScreen(mainScreen);
				break;
			case HELP:
				if (helpScreen == null) helpScreen = new HelpScreen(this);
				this.setScreen(helpScreen);
				break;
			case HIGHSCORE:
				if (highscorescreen == null) highscorescreen = new HighscoreScreen(this);
				this.setScreen(highscorescreen);
				break;

			case END:
				if (endScreen == null) endScreen = new EndScreen(this);
				mainScreen = null;
				this.setScreen(endScreen);
				gmMusic.stop();
				bgMusic.play();
				break;
		}
	}


	@Override
	public void dispose() {
		bgMusic.dispose();
		assetMan.manager.dispose();
	}

}
