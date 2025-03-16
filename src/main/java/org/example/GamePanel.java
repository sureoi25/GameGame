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
        // First check if the player can move in the intended direction
        if (player.isMoving()) {
            boolean collision = collisionChecker.checkTile(player);
            if (collision) {
                player.setMoving(false);
                player.setPosition(player.getOldX(), player.getOldY()); // Reset to old position
            }
        }

        // Then update the player state
        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        tileManager.draw(g);
        player.render(g);
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
            player.setDirection((x < player.getX()) ? 0 : 2); // LEFT or RIGHT
            player.setMoving(true);
        }
    }
}