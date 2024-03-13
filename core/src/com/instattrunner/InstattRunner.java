package com.instattrunner;

import com.badlogic.gdx.Game; // game class used to delegate between different screens
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.instattrunner.loader.IRAssetManager;
import com.instattrunner.views.EndScreen;
import com.instattrunner.views.HelpScreen;
import com.instattrunner.views.MainScreen;
import com.instattrunner.views.MenuScreen;

/* This class is parent of model, views, controllers:
- model for game data and logic processing
- views for displaying and rendering
- controllers for mapping user input to methods in model */
public class InstattRunner extends Game {
	private MenuScreen menuScreen;
	private MainScreen mainScreen;
	private HelpScreen helpScreen;
	private EndScreen endScreen;
	public IRAssetManager assetMan = new IRAssetManager();
	public OrthographicCamera gameCam = new OrthographicCamera();
	private Music bgMusic;
	public int finalScore = 0; // set value when player dies

	public final static int VIEW_WIDTH = 800;
	public final static int VIEW_HEIGHT = 480;


	public final static int MENU = 0;
	public final static int PLAY = 1;
	public final static int HELP = 2;
	public final static int END = 3;


	// Method to swap between screens
	public void changeScreen(int screen) {
		switch(screen) {
			case MENU:
				if (menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case PLAY:
				if (mainScreen == null) mainScreen = new MainScreen(this);
				this.setScreen(mainScreen);
				break;
			case HELP:
				if (helpScreen == null) helpScreen = new HelpScreen(this);
				this.setScreen(helpScreen);
				break;
			case END:
				if (endScreen == null) endScreen = new EndScreen(this);
				mainScreen = null;
				this.setScreen(endScreen);
				break;
		}
	}

	// Set default screen when application opens
	@Override
	public void create() {
		menuScreen = new MenuScreen(this);
		setScreen(menuScreen);


		// Load and play music 
		assetMan.queueAddMusic();
		assetMan.manager.finishLoading();
		bgMusic = assetMan.manager.get("music/rain.mp3");
		bgMusic.play();
	}

	@Override
	public void dispose() {
		bgMusic.dispose();
		assetMan.manager.dispose();
	}

}
