package org.example.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.example.utils.constants.SlimeConstants;

public class Slime extends Entity {
    private BufferedImage spritesheet;

    public Slime(float x, float y) {
        super(x, y, 32, 32); // Updated to 32x32 pixels

        // Adjust hitbox to match smaller sprite
        this.hitboxWidth = 16;
        this.hitboxHeight = 16;
        this.hitboxOffsetX = (width - hitboxWidth) / 2;
        this.hitboxOffsetY = height - hitboxHeight;

        // Reinitialize solidArea with new hitbox
        this.solidArea.width = hitboxWidth;
        this.solidArea.height = hitboxHeight;
        this.solidArea.x = hitboxOffsetX;
        this.solidArea.y = hitboxOffsetY;

        // Initialize stats
        initStats(20, 3); // 20 HP, 3 attack damage
        speed = 0.5f; // Slower movement

        // Load spritesheet
        try {
            spritesheet = ImageIO.read(getClass().getResourceAsStream("/sprites/slime.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize animations
        animations = new BufferedImage[13][]; // Based on SlimeConstants states
        loadAnimations();

        // Set initial animation state
        state = SlimeConstants.IDLE_DOWN;
        aniSpeed = 15; // Animation speed (lower is faster)
    }

    private void loadAnimations() {
        // Idle animations
        loadAnimationSet(SlimeConstants.IDLE_DOWN, 0, 4);
        loadAnimationSet(SlimeConstants.IDLE_SIDE, 1, 4);
        loadAnimationSet(SlimeConstants.IDLE_UP, 2, 4);

        // Running animations
        loadAnimationSet(SlimeConstants.RUNNING_DOWN, 3, 6);
        loadAnimationSet(SlimeConstants.RUNNING_SIDE, 4, 6);
        loadAnimationSet(SlimeConstants.RUNNING_UP, 5, 6);

        // Attack animations
        loadAnimationSet(SlimeConstants.ATTACK_DOWN, 6, 7);
        loadAnimationSet(SlimeConstants.ATTACK_SIDE, 7, 7);
        loadAnimationSet(SlimeConstants.ATTACK_UP, 8, 7);

        // Death animation
        loadAnimationSet(SlimeConstants.DEATH, 12, 5);
    }

    private void loadAnimationSet(int state, int row, int frameCount) {
        animations[state] = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            // Updated to 32x32 pixel sprites
            animations[state][i] = spritesheet.getSubimage(i * 32, row * 32, 32, 32);
        }
    }

    @Override
    public void update() {
        updateAnimationTick();
        updateCooldowns();
        updatePosition();
        setAnimation();
    }

    @Override
    public void render(Graphics g) {
        if (alive) {
            BufferedImage currentFrame = animations[state][aniIndex];
            g.drawImage(currentFrame, (int)x, (int)y, width, height, null);
        }
    }

    @Override
    protected void updatePosition() {
        if (moving) {
            switch (entityDirection) {
                case 0: // LEFT
                    x -= speed;
                    break;
                case 1: // UP
                    y -= speed;
                    break;
                case 2: // RIGHT
                    x += speed;
                    break;
                case 3: // DOWN
                    y += speed;
                    break;
            }
        }
    }

    @Override
    protected void setAnimation() {
        int previousState = state;

        if (attacking) {
            // Set attack animation based on direction
            switch (entityDirection) {
                case 0: state = SlimeConstants.ATTACK_SIDE; break;
                case 1: state = SlimeConstants.ATTACK_UP; break;
                case 2: state = SlimeConstants.ATTACK_SIDE; break;
                case 3: state = SlimeConstants.ATTACK_DOWN; break;
            }
        } else if (moving) {
            // Set running animation based on direction
            switch (entityDirection) {
                case 0: state = SlimeConstants.RUNNING_SIDE; break;
                case 1: state = SlimeConstants.RUNNING_UP; break;
                case 2: state = SlimeConstants.RUNNING_SIDE; break;
                case 3: state = SlimeConstants.RUNNING_DOWN; break;
            }
        } else {
            // Set idle animation based on direction
            switch (entityDirection) {
                case 0: state = SlimeConstants.IDLE_SIDE; break;
                case 1: state = SlimeConstants.IDLE_UP; break;
                case 2: state = SlimeConstants.IDLE_SIDE; break;
                case 3: state = SlimeConstants.IDLE_DOWN; break;
            }
        }

        // Reset animation index if state changed
        if (previousState != state) {
            aniIndex = 0;
            aniTick = 0;
        }
    }

    @Override
    protected int getSpritesAmount(int state) {
        return SlimeConstants.GetSpriteAmount(state);
    }

    @Override
    protected void die() {
        super.die();
        state = SlimeConstants.DEATH;
        aniIndex = 0;
        aniTick = 0;
        moving = false;
    }
    public void renderWithCamera(Graphics g, int cameraX, int cameraY) {
        if (alive) {
            BufferedImage currentFrame = animations[state][aniIndex];
            g.drawImage(currentFrame,
                    (int)x - cameraX,
                    (int)y - cameraY,
                    width, height, null);
        }
    }
}