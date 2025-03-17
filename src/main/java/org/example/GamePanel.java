package org.example;

import org.example.utils.CollisionChecker;
import org.example.entity.Player;
import org.example.inputs.KeyBoardInputs;
import org.example.inputs.MouseInputs;
import org.example.tile.TileManager;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;

public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private Player player;
    private TileManager tileManager;
    private CollisionChecker collisionChecker;

    // Panel dimensions
    public final int PANEL_WIDTH = 960;
    public final int PANEL_HEIGHT = 540;

    // Player dimensions
    private final int PLAYER_WIDTH = 48;
    private final int PLAYER_HEIGHT = 48;

    // World settings
    public final int WORLD_WIDTH = 40; // 40 tiles wide
    public final int WORLD_HEIGHT = 40; // 40 tiles high

    // Camera position
    private int cameraX;
    private int cameraY;

    public GamePanel() {
        setPanelSize();
        initializeInputs();
        tileManager = new TileManager(this);
        collisionChecker = new CollisionChecker(tileManager);
        player = new Player(100, 100, PLAYER_WIDTH, PLAYER_HEIGHT);
        setFocusable(true);
        requestFocus();
    }

    private void setPanelSize() {
        Dimension size = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    private void initializeInputs() {
        mouseInputs = new MouseInputs(this);
        addKeyListener(new KeyBoardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    public void updateGame() {
        //check sa if pwede ba mu move sa tile iya gi face
        if (player.isMoving()) {
            boolean collision = collisionChecker.checkTile(player);
            if (collision) {
                player.setMoving(false);
                player.setPosition(player.getOldX(), player.getOldY()); // if dili kay balik sa old position
            }
        }

        // Then update the player state
        player.update();

        // Update camera position to follow player
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        // Center the camera on the player
        cameraX = (int)player.getX() - PANEL_WIDTH / 2;
        cameraY = (int)player.getY() - PANEL_HEIGHT / 2;

        // Clamp the camera position to the world boundaries
        int maxCameraX = tileManager.TILE_SIZE * WORLD_WIDTH - PANEL_WIDTH;
        int maxCameraY = tileManager.TILE_SIZE * WORLD_HEIGHT - PANEL_HEIGHT;

        cameraX = Math.max(0, Math.min(cameraX, maxCameraX));
        cameraY = Math.max(0, Math.min(cameraY, maxCameraY));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the tilemap with camera offset
        tileManager.draw(g, cameraX, cameraY);

        // Draw the player at the camera-adjusted position
        player.render(g, cameraX, cameraY);
    }

    // Methods to delegate input handling to player
    public void setDirection(int direction) {
        player.setDirection(direction);
    }

    public void setMoving(boolean moving) {
        player.setMoving(moving);
    }

    public void attack() {
        player.attack();
    }

    // Method for mouse interaction
    public void setPlayerPosition(int x, int y) {
        if (!player.isAttacking()) {
            // Convert screen coordinates to world coordinates
            int worldX = x + cameraX;
            int worldY = y + cameraY;

            player.setDirection((worldX < player.getX()) ? 0 : 2); // LEFT or RIGHT
            player.setMoving(true);
        }
    }

    // Getter for camera position
    public int getCameraX() {
        return cameraX;
    }

    public int getCameraY() {
        return cameraY;
    }
}