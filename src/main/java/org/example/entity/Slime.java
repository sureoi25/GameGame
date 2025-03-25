package org.example.entity;

import org.example.GamePanel;
import org.example.entity.Ai.SlimeAI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import static org.example.utils.constants.Directions.*;
import static org.example.utils.constants.SlimeConstants.*;

public class Slime extends Entity {
    private final GamePanel gp;
    private final SlimeAI ai;
    private BufferedImage[][] animations;
    private boolean debugMode = false;
    private boolean isWandering = true;
    private boolean isChasing = false;// Added debug mode flag

    public Slime(float x, float y, int width, int height, GamePanel gp) {
        super(x, y, width, height);
        this.gp = gp;
        this.ai = new SlimeAI(this, gp);

        // Configure hitbox (smaller than sprite)
        this.hitboxWidth = width / 2;
        this.hitboxHeight = height / 2;
        this.hitboxOffsetX = (width - hitboxWidth) / 2;
        this.hitboxOffsetY = height - hitboxHeight;

        // Initialize stats
        this.setSpeed(1.5f);
        this.initStats(50, 5);

        // Load animations
        loadAnimations();
        this.state = IDLE_DOWN;
    }

    @Override
    public void update() {
        updateAnimationTick();
        updateCooldowns();
        ai.makeDecision();
        updatePosition();
        setAnimation();
    }

    @Override
    protected void updatePosition() {
        if (moving && !attacking) {
            saveOldPosition();

            switch (entityDirection) {
                case LEFT: x -= speed; break;
                case UP: y -= speed; break;
                case RIGHT: x += speed; break;
                case DOWN: y += speed; break;
            }

            // Reset position if collision was detected
            if (collisionDetected) {
                x = oldX;
                y = oldY;
                collisionDetected = false;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        BufferedImage frame = animations[state][aniIndex];

        if (frame != null) {
            // Handle left-facing sprites
            if (entityDirection == LEFT &&
                    (state == RUNNING_SIDE || state == IDLE_SIDE || state == ATTACK_SIDE)) {
                g.drawImage(frame, width, 0, -width, height, null);
            } else {
                g.drawImage(frame, 0, 0, width, height, null);
            }
        } else {
            // Fallback rendering
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, width, height);
        }

        // Debug: Draw hitbox
        if (debugMode) {
            g.setColor(Color.RED);
            g.drawRect(hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight);
        }
    }

    @Override
    protected void setAnimation() {
        int startAni = state;

        if (attacking) {
            switch (entityDirection) {
                case LEFT:
                case RIGHT: state = ATTACK_SIDE; break;
                case UP: state = ATTACK_UP; break;
                case DOWN: state = ATTACK_DOWN; break;
            }
        } else if (moving) {
            switch (entityDirection) {
                case LEFT:
                case RIGHT: state = RUNNING_SIDE; break;
                case UP: state = RUNNING_UP; break;
                case DOWN: state = RUNNING_DOWN; break;
            }
        } else {
            switch (entityDirection) {
                case LEFT:
                case RIGHT: state = IDLE_SIDE; break;
                case UP: state = IDLE_UP; break;
                case DOWN: state = IDLE_DOWN; break;
            }
        }

        if (startAni != state) {
            resetAnimation();
        }
    }

    @Override
    protected int getSpritesAmount(int state) {
        switch(state) {
            case IDLE_DOWN:
            case IDLE_SIDE:
            case IDLE_UP:
            case RUNNING_DOWN:
            case RUNNING_SIDE:
            case RUNNING_UP:
            case ATTACK_DOWN:
            case ATTACK_SIDE:
            case ATTACK_UP:
                return 3;
            default:
                return 0;
        }
    }
    public boolean isWandering() { return isWandering; }
    public void setWandering(boolean wandering) {
        this.isWandering = wandering;
        if (wandering) this.isChasing = false;
    }

    public boolean isChasing() { return isChasing; }
    public void setChasing(boolean chasing) {
        this.isChasing = chasing;
        if (chasing) this.isWandering = false;
    }
    private void loadAnimations() {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/sprites/slime.png"));
            animations = new BufferedImage[9][3]; // 9 states, 3 frames each

            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 3; x++) {
                    animations[y][x] = sheet.getSubimage(x * 32, y * 32, 32, 32);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading slime sprites: " + e.getMessage());
            createPlaceholderGraphics();
        }
    }

    private void createPlaceholderGraphics() {
        animations = new BufferedImage[9][3];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 3; j++) {
                animations[i][j] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                Graphics g = animations[i][j].getGraphics();
                g.setColor(new Color(0, 255, 0, 150));
                g.fillOval(0, 0, 32, 32);
                g.dispose();
            }
        }
    }

    // Helper method to reset animation
    private void resetAnimation() {
        aniTick = 0;
        aniIndex = 0;
    }

    // Toggle debug mode
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}