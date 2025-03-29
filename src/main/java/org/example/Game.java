package org.example;

public class Game implements Runnable {

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private boolean gameStarted = false;

    public Game() {
        // Create GameWindow with reference to this Game instance
        gameWindow = new GameWindow(this);
    }

    public void startGame() {
        gameStarted = true;
        gamePanel = new GamePanel();
        gameWindow.startGame(gamePanel);
        gamePanel.setupGame();
        startGameLoop();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaF >= 1) {
                if (gameStarted) {
                    gamePanel.updateGame();
                    gamePanel.repaint();
                }
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }
}