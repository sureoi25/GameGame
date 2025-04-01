package org.example;

import org.example.entity.SlimeManager;
import org.example.objects.SuperObject;
import org.example.objects.ObjectFactory;
import org.example.utils.CollisionChecker;
import org.example.entity.Player;
import org.example.inputs.KeyBoardInputs;
import org.example.inputs.MouseInputs;
import org.example.tile.TileManager;
import org.example.HUD; // Import the new HUD class

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    public Player player;
    public TileManager tileManager;
    public CollisionChecker collisionChecker;
    public SuperObject[] obj = new SuperObject[10];
    public ObjectFactory objectFactory;
    public Sound sound = new Sound();
    public SlimeManager slimeManager;
    public HUD hud; // Add HUD instance

    // Screen dimensions
    public final int PANEL_WIDTH = 960;
    public final int PANEL_HEIGHT = 540;
    private final int PLAYER_SIZE = 64;

    // World settings
    public final int WORLD_WIDTH_TILES = 40;
    public final int WORLD_HEIGHT_TILES = 40;
    public final int TILE_SIZE = 48; // Should match TileManager

    // Camera
    private int cameraX;
    private int cameraY;

    private boolean paused = false;
    private boolean gameCompleted = false;

    public GamePanel() {
        setPanelSize();
        initializeSystems();
        setupGame();
    }

    private void setPanelSize() {
        Dimension size = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
        setPreferredSize(size);
    }

    private void initializeSystems() {
        tileManager = new TileManager(this);
        collisionChecker = new CollisionChecker(tileManager);
        objectFactory = new ObjectFactory(this);
        player = new Player(288, 96, PLAYER_SIZE, PLAYER_SIZE, this);
        setupInputs();
        slimeManager = new SlimeManager(this);

        // Initialize HUD after player is created
        hud = new HUD(this, player,slimeManager);
    }

    private void setupInputs() {
        mouseInputs = new MouseInputs(this);
        addKeyListener(new KeyBoardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        setFocusable(true);
    }

    public void setupGame() {
        // Place objects
        obj[0] = objectFactory.createObject("Key", 13 * TILE_SIZE, 13 * TILE_SIZE);
        obj[1] = objectFactory.createObject("Key", 27 * TILE_SIZE, 34 * TILE_SIZE);
        obj[2] = objectFactory.createObject("chest", 33 * TILE_SIZE, 25 * TILE_SIZE);
        obj[3] = objectFactory.createObject("chest", 22 * TILE_SIZE, 8 * TILE_SIZE);
        obj[4] = objectFactory.createObject("pork", 39 * TILE_SIZE, 2 * TILE_SIZE);
        obj[5] = objectFactory.createObject("chicken", 3 * TILE_SIZE, 39 * TILE_SIZE);
        obj[6] = objectFactory.createObject("blue mushroom", 25 * TILE_SIZE, 37 * TILE_SIZE);
        obj[7] = objectFactory.createObject("red mushroom", 33 * TILE_SIZE, 15 * TILE_SIZE);

        playMusic(3);
    }
    public void setupRestart(){
        obj[0] = objectFactory.createObject("Key", 13 * TILE_SIZE, 13 * TILE_SIZE);
        obj[1] = objectFactory.createObject("Key", 27 * TILE_SIZE, 34 * TILE_SIZE);
        obj[2] = objectFactory.createObject("chest", 33 * TILE_SIZE, 25 * TILE_SIZE);
        obj[3] = objectFactory.createObject("chest", 22 * TILE_SIZE, 8 * TILE_SIZE);
        obj[4] = objectFactory.createObject("pork", 39 * TILE_SIZE, 2 * TILE_SIZE);
        obj[5] = objectFactory.createObject("chicken", 3 * TILE_SIZE, 39 * TILE_SIZE);
        obj[6] = objectFactory.createObject("blue mushroom", 25 * TILE_SIZE, 37 * TILE_SIZE);
        obj[7] = objectFactory.createObject("red mushroom", 33 * TILE_SIZE, 15 * TILE_SIZE);

    }

    public void updateGame() {
        if (!player.isDead() && !paused) {
            player.update();

            // Add attack logic for player
            if (player.isAttacking()) {
                player.attackNearbyEntities(slimeManager);
            }

            slimeManager.update();

            if (slimeManager.areWavesComplete()) {
                System.out.println("All waves completed: " + slimeManager.getCurrentWave() +
                        " > " + slimeManager.WAVE_SLIME_COUNTS.length);

                if (!gameCompleted) {
                    gameCompleted = true;
                }
            }

            updateCamera();
        }
    }

    public void setPaused(boolean pauseState) {
        this.paused = pauseState;

        // Optional: Add pause sound effect or visual indicator
        if (paused) {
            sound.stop(); // Optionally pause background music
        } else {
            playMusic(3); // Resume background music
        }
    }

    public void restartGame() {
        if (player.isDead() || gameCompleted) {
            sound.stop();

            // Reset player
            player.restart();

            // Reset slime manager
            slimeManager = new SlimeManager(this);
            gameCompleted = false;
            // Respawn objects
            setupRestart();

            // Ensure game is unpaused
            paused = false;
        }
    }

    private void renderPauseScreen(Graphics g) {
        if (paused) {
            // Semi-transparent overlay
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

            // Pause text
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.WHITE);
            String pauseText = "PAUSED";
            int textWidth = g.getFontMetrics().stringWidth(pauseText);
            g.drawString(pauseText, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2);

            // Optional: Add instructions
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String instructionText = "Press P to resume";
            int instructionWidth = g.getFontMetrics().stringWidth(instructionText);
            g.drawString(instructionText, (PANEL_WIDTH - instructionWidth) / 2, PANEL_HEIGHT / 2 + 50);
        }
    }

    private void updateCamera() {
        cameraX = (int)player.getX() - PANEL_WIDTH / 2;
        cameraY = (int)player.getY() - PANEL_HEIGHT / 2;

        // Clamp camera to world bounds
        int maxCameraX = WORLD_WIDTH_TILES * TILE_SIZE - PANEL_WIDTH;
        int maxCameraY = WORLD_HEIGHT_TILES * TILE_SIZE - PANEL_HEIGHT;
        cameraX = Math.max(0, Math.min(cameraX, maxCameraX));
        cameraY = Math.max(0, Math.min(cameraY, maxCameraY));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw world
        tileManager.draw(g, cameraX, cameraY);

        // Draw objects
        for (SuperObject o : obj) {
            if (o != null) o.draw(g2d, this);
        }

        // Draw entities
        player.render(g, cameraX, cameraY);
        slimeManager.render(g, cameraX, cameraY);

        // Draw HUD (always on top, not affected by camera)
        hud.draw(g2d);

        // Render game over or pause screens
        if (player.isDead()) {
            renderGameOverScreen(g);
        } else if (paused) {
            renderPauseScreen(g);
        } else if (gameCompleted){
            renderSuccessScreen(g);
        }
    }
    private void renderSuccessScreen(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        // Game Over text
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.GREEN);
        String gameOverText = "You WON!";
        int textWidth = g.getFontMetrics().stringWidth(gameOverText);
        g.drawString(gameOverText, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2);

        // Restart instructions
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        String restartText = "Press R to Restart";
        int restartWidth = g.getFontMetrics().stringWidth(restartText);
        g.drawString(restartText, (PANEL_WIDTH - restartWidth) / 2, PANEL_HEIGHT / 2 + 50);

    }

    private void renderGameOverScreen(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        // Game Over text
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.RED);
        String gameOverText = "GAME OVER";
        int textWidth = g.getFontMetrics().stringWidth(gameOverText);
        g.drawString(gameOverText, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2);

        // Restart instructions
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        String restartText = "Press R to Restart";
        int restartWidth = g.getFontMetrics().stringWidth(restartText);
        g.drawString(restartText, (PANEL_WIDTH - restartWidth) / 2, PANEL_HEIGHT / 2 + 50);

    }

    // Audio methods
    public void playMusic(int i) {
        if (!player.isDead()) {
            sound.setFile(i);
            sound.play();
            sound.loop();
        }
    }
    public void playSE(int i) { sound.setFile(i); sound.play(); }
    public void stopMusic(){ sound.stop();}
    // Input methods
    public void setDirection(int direction) { player.setDirection(direction); }
    public void setMoving(boolean moving) { player.setMoving(moving); }
    public void attack() { player.attack(); }
    public void interact() { player.interact(); }

    // Camera access
    public int getCameraX() { return cameraX; }
    public int getCameraY() { return cameraY; }
    //game complete
    public boolean isGameCompleted() {
        return gameCompleted;
    }

}