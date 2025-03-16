package org.example.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    protected int direction;
    protected float speed = 1.0f;
    protected int aniTick, aniIndex, aniSpeed;
    protected int state;
    protected BufferedImage[][] animations;
    protected int playerDirection;
    protected boolean moving = false;
    protected boolean attacking = false;
    protected int attackCooldown = 0;
    protected float oldX, oldY;
    protected boolean collisionDetected;
    protected final int ATTACK_DURATION = 20;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getSpritesAmount(state)) {
                aniIndex = 0;
                if (attacking) {
                    attacking = false;
                }
            }
        }
    }

    protected void updateCooldowns() {
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }

    public abstract void update();
    public abstract void render(Graphics g);
    protected abstract void updatePosition();
    protected abstract void setAnimation();
    protected abstract int getSpritesAmount(int state);

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setDirection(int direction) {
        this.playerDirection = direction;
        moving = true;
    }
    public int getDirection() {
        return direction;
    }
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isMoving() {
        return moving;
    }

    public void attack() {
        if (!attacking && attackCooldown <= 0) {
            attacking = true;
            attackCooldown = 30;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}