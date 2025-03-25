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
    public TileManager tileManager;
    public CollisionChecker collisionChecker;
    public SuperObject[] obj = new SuperObject[10];
    public ObjectFactory objectFactory;
    public Sound sound = new Sound();

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

    public void updateGame() {
        player.update();
        updateCamera();
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw world
        tileManager.draw(g, cameraX, cameraY);

        // Draw objects
        for (SuperObject o : obj) {
            if (o != null) o.draw((Graphics2D)g, this);
        }

        // Draw entities
        player.render(g, cameraX, cameraY);
    }

    // Audio methods
    public void playMusic(int i) { sound.setFile(i); sound.play(); sound.loop(); }
    public void stopMusic() { sound.stop(); }
    public void playSE(int i) { sound.setFile(i); sound.play(); }

    // Input methods
    public void setDirection(int direction) { player.setDirection(direction); }
    public void setMoving(boolean moving) { player.setMoving(moving); }
    public void attack() { player.attack(); }
    public void interact() { player.interact(); }

    // Camera access
    public int getCameraX() { return cameraX; }
    public int getCameraY() { return cameraY; }
}