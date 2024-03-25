package com.instattrunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ScoreManager {
       public int loadTextFile(){
        // Load the file using a FileHandle
        FileHandle fileHandle = Gdx.files.internal("score/HighScore.txt");

        // Read the contents of the file into a String
        String highScoreString = fileHandle.readString();

        int score=0;
        // Parse the String to an integer
        try {
            score = Integer.parseInt(highScoreString.trim());
        } catch (NumberFormatException e) {
            // Handle parsing error (e.g., file contents are not a valid integer)
            e.printStackTrace();
        }

        return score;
    } 
  
    private void updateHighScore(int newHighScore) {
        FileHandle fileHandle = Gdx.files.local("score/HighScore.txt");
        String stringHighScore = Integer.toString(newHighScore);
        fileHandle.writeString(stringHighScore, false);

        System.out.println("highscoreadded");
    }
}
