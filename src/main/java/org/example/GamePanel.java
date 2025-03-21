package org.example;

import org.example.objects.SuperObject;
import org.example.objects.ObjectFactory;
import org.example.utils.CollisionChecker;
import org.example.entity.Player;
import org.example.inputs.KeyBoardInputs;
import org.example.inputs.MouseInputs;
import org.example.tile.TileManager;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    public Player player;
    private TileManager tileManager;
    public CollisionChecker collisionChecker;
    public SuperObject obj[] = new SuperObject[10];
    public ObjectFactory objectFactory;

    // Panel dimensions
    public final int PANEL_WIDTH = 960;
    public final int PANEL_HEIGHT = 540;
    private final int PLAYER_WIDTH = 64;
    private final int PLAYER_HEIGHT = 64;

    // World settings
    public final int WORLD_WIDTH = 40;
    public final int WORLD_HEIGHT = 40;

    // Camera position
    private int cameraX;
    private int cameraY;

    public GamePanel() {
        setPanelSize();
        initializeInputs();
        tileManager = new TileManager(this);
        collisionChecker = new CollisionChecker(tileManager);
        objectFactory = new ObjectFactory(this);
        player = new Player(288, 96, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        setFocusable(true);
        requestFocus();
    }

    public void setupGame() {
        // Place objects in the world using objectFactory
        obj[0] = objectFactory.createObject("Key", 13 * 48, 13 * 48);
        obj[1] = objectFactory.createObject("Key", 27 * 48, 34 * 48);
        obj[2] = objectFactory.createObject("chest", 33 * 48, 25 * 48);
        obj[3] = objectFactory.createObject("chest", 22 * 48, 8 * 48);
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
        if (player.isMoving()) {
            boolean collision = collisionChecker.checkTile(player);
            if (collision) {
                player.setMoving(false);
                player.setPosition(player.getOldX(), player.getOldY());
            }
        }

        player.update();
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        // Center the camera on the player
        cameraX = (int)player.getX() - PANEL_WIDTH / 2;
        cameraY = (int)player.getY() - PANEL_HEIGHT / 2;

        // Clamp to world boundaries
        int maxCameraX = tileManager.TILE_SIZE * WORLD_WIDTH - PANEL_WIDTH;
        int maxCameraY = tileManager.TILE_SIZE * WORLD_HEIGHT - PANEL_HEIGHT;

        cameraX = Math.max(0, Math.min(cameraX, maxCameraX));
        cameraY = Math.max(0, Math.min(cameraY, maxCameraY));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        tileManager.draw(g, cameraX, cameraY);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < obj.length; ++i) {
            if (obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }
        player.render(g, cameraX, cameraY);
    }

    // Input handling methods
    public void setDirection(int direction) {
        player.setDirection(direction);
    }

    public void setMoving(boolean moving) {
        player.setMoving(moving);
    }

    public void attack() {
        player.attack();
    }
    public void interact() {
        // Call the player's interact method to check for nearby objects
        player.interact();
    }

    // Camera getters
    public int getCameraX() { return cameraX; }
    public int getCameraY() { return cameraY; }
}