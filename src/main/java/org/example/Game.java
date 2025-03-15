package org.example;

public class Game {

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    public Game(){
        gameWindow = new GameWindow(gamePanel);
        gamePanel = new GamePanel();
        gamePanel.requestFocus();
    }
}
