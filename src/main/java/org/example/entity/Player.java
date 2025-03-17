package org.example.entity;

import static org.example.utils.constants.Directions.*;
import static org.example.utils.constants.PlayerConstants.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Player extends Entity {
    private BufferedImage spriteSheet;
    private int playerAction = IDLE_DOWN;
    private int lastNonAttackDirection = DOWN;
    private int attackTick = 0;
    private int attackCooldown = 30;

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        this.oldX = x;
        this.oldY = y;
        this.collisionDetected = false;
        loadAnimations();
        this.state = IDLE_DOWN;
        this.aniSpeed = 15;
        this.direction = 2;
        this.setSpeed(4.0f);
    }

    @Override
    public void update() {
        updateAnimationTick();
        updateCooldowns();
        updatePosition();
        setAnimation();
    }

    // New render method that takes camera position into account
    public void render(Graphics g, int cameraX, int cameraY) {
        BufferedImage currentFrame = animations[state][aniIndex];

        // Calculate screen position by subtracting camera offset
        int screenX = (int)x - cameraX;
        int screenY = (int)y - cameraY;

        // Flip the image if facing left and using a side animation
        if (playerDirection == LEFT &&
                (state == RUNNING_SIDE || state == IDLE_SIDE || state == ATTACK_SIDE)) {
            g.drawImage(currentFrame, screenX + width, screenY, -width, height, null);
        } else {
            g.drawImage(currentFrame, screenX, screenY, width, height, null);
        }
    }

    // Original render method for backward compatibility
    @Override
    public void render(Graphics g) {
        render(g, 0, 0);
    }

    @Override
    protected void updatePosition() {
        // Don't attempt to move if attacking
        if (moving && !attacking) {
            oldX = x;
            oldY = y;

            switch (playerDirection) {
                case LEFT:
                    x -= speed;
                    break;
                case UP:
                    y -= speed;
                    break;
                case RIGHT:
                    x += speed;
                    break;
                case DOWN:
                    y += speed;
                    break;
            }

            // These will be checked by GamePanel
            collisionDetected = false;
        }
    }

    public float getOldX() {
        return oldX;
    }

    public float getOldY() {
        return oldY;
    }

    @Override
    public void setDirection(int direction) {
        if (!attacking) {
            this.playerDirection = direction;
            this.lastNonAttackDirection = direction;
        }
    }

    public void attack() {
        if (!attacking) {
            attacking = true;
            attackTick = 0;
        }
    }

    public boolean isAttacking() {
        return attacking;
    }

    @Override
    protected void updateCooldowns() {
        // Handle attack cooldown
        if (attacking) {
            attackTick++;
            if (attackTick >= attackCooldown) {
                attacking = false;
                attackTick = 0;
                // Reset the player direction to the last non-attack direction
                playerDirection = lastNonAttackDirection;
            }
        }
    }

    @Override
    protected void setAnimation() {
        int startAni = state;

        if (attacking) {
            switch (playerDirection) {
                case LEFT:
                case RIGHT:
                    state = ATTACK_SIDE;
                    break;
                case UP:
                    state = ATTACK_UP;
                    break;
                case DOWN:
                    state = ATTACK_DOWN;
                    break;
            }
        } else if (moving) {
            switch (playerDirection) {
                case LEFT:
                case RIGHT:
                    state = RUNNING_SIDE;
                    break;
                case UP:
                    state = RUNNING_UP;
                    break;
                case DOWN:
                    state = RUNNING_DOWN;
                    break;
            }
        } else {
            switch (playerDirection) {
                case LEFT:
                case RIGHT:
                    state = IDLE_SIDE;
                    break;
                case UP:
                    state = IDLE_UP;
                    break;
                case DOWN:
                    state = IDLE_DOWN;
                    break;
            }
        }

        // Reset animation index when changing animations
        if (startAni != state) {
            aniTick = 0;
            aniIndex = 0;
        }
    }

    @Override
    protected int getSpritesAmount(int state) {
        return GetSpriteAmount(state);
    }

    private void loadAnimations() {
        InputStream is = getClass().getResourceAsStream("/sprites/player.png");

        try {
            spriteSheet = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        animations = new BufferedImage[10][6];

        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                if (i < GetSpriteAmount(j)) {
                    animations[j][i] = spriteSheet.getSubimage(i * 48, j * 48, 48, 48);
                }
            }
        }
    }
}