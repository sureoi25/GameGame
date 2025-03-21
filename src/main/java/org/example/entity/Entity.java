package org.example.entity;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Entity {
    public float x, y, oldX, oldY;
    protected int width, height, direction;
    protected float speed = 1.0f;
    protected int aniTick, aniIndex, aniSpeed;
    protected int state;
    protected BufferedImage[][] animations;
    protected int entityDirection;
    protected boolean moving = false;
    protected boolean attacking = false;
    protected int attackCooldown = 0;
    protected boolean collisionDetected;
    protected final int ATTACK_DURATION = 20;

    // Hitbox properties
    protected int hitboxWidth;
    protected int hitboxHeight;
    public int hitboxOffsetX;
    public int hitboxOffsetY;

    // SolidArea for collision
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;

    // Combat attributes
    protected int maxHp, currentHp, attackDamage;
    protected boolean invulnerable = false;
    protected int invulnerabilityTime = 0;
    protected final int INVULNERABILITY_DURATION = 60;
    protected boolean alive = true;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.oldX = x;
        this.oldY = y;
        this.width = width;
        this.height = height;

        // Initialize hitbox (this is players hitbox)
        this.hitboxWidth = width / 2;
        this.hitboxHeight = height / 3;
        this.hitboxOffsetX = (width - hitboxWidth) / 2;
        this.hitboxOffsetY = height - hitboxHeight;

        // Initialize solidArea Rectangle
        this.solidAreaDefaultX = hitboxOffsetX;
        this.solidAreaDefaultY = hitboxOffsetY;
        this.solidArea = new Rectangle(solidAreaDefaultX, solidAreaDefaultY, hitboxWidth, hitboxHeight);
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
        if (attackCooldown > 0) attackCooldown--;

        if (invulnerable) {
            invulnerabilityTime++;
            if (invulnerabilityTime >= INVULNERABILITY_DURATION) {
                invulnerable = false;
                invulnerabilityTime = 0;
            }
        }
    }

    // Save position before movement for potential collision rollback
    public void saveOldPosition() {
        this.oldX = this.x;
        this.oldY = this.y;
    }

    public abstract void update();
    public abstract void render(Graphics g);
    protected abstract void updatePosition();
    protected abstract void setAnimation();
    protected abstract int getSpritesAmount(int state);

    // Hitbox methods
    public float getHitboxX() {
        return x + hitboxOffsetX;
    }

    public float getHitboxY() {
        return y + hitboxOffsetY;
    }

    public int getHitboxWidth() {
        return hitboxWidth;
    }

    public int getHitboxHeight() {
        return hitboxHeight;
    }

    public void setHitbox(int width, int height, int offsetX, int offsetY) {
        this.hitboxWidth = width;
        this.hitboxHeight = height;
        this.hitboxOffsetX = offsetX;
        this.hitboxOffsetY = offsetY;

        // Update solidArea when hitbox changes
        this.solidAreaDefaultX = offsetX;
        this.solidAreaDefaultY = offsetY;
        this.solidArea.width = width;
        this.solidArea.height = height;
    }

    // Get next position based on direction for collision checking
    public float getNextHitboxX() {
        float nextX = getHitboxX();
        if (entityDirection == 0) nextX -= speed; // LEFT
        else if (entityDirection == 2) nextX += speed; // RIGHT
        return nextX;
    }

    public float getNextHitboxY() {
        float nextY = getHitboxY();
        if (entityDirection == 1) nextY -= speed; // UP
        else if (entityDirection == 3) nextY += speed; // DOWN
        return nextY;
    }

    // Basic getters and setters
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getOldX() { return oldX; }
    public float getOldY() { return oldY; }

    public void setDirection(int direction) {
        this.entityDirection = direction;
        moving = true;
        saveOldPosition(); // Save position before moving
    }

    public int getDirection() { return entityDirection; }
    public void setMoving(boolean moving) { this.moving = moving; }
    public boolean isMoving() { return moving; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public boolean isAttacking() { return attacking; }

    public void setCollisionDetected(boolean collision) { this.collisionDetected = collision; }
    public boolean isCollisionDetected() { return collisionDetected; }

    public void attack() {
        if (!attacking && attackCooldown <= 0) {
            attacking = true;
            attackCooldown = 30;
        }
    }

    // Combat methods
    public void initStats(int maxHp, int attackDamage) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attackDamage = attackDamage;
    }

    public void takeDamage(int damage) {
        if (!invulnerable && alive) {
            currentHp -= damage;
            if (currentHp <= 0) {
                currentHp = 0;
                die();
            }
            invulnerable = true;
            invulnerabilityTime = 0;
        }
    }

    protected void die() {
        alive = false;
    }

    public void heal(int amount) {
        if (alive) {
            currentHp += amount;
            if (currentHp > maxHp) currentHp = maxHp;
        }
    }
    public void increaseHP(int amount){
        if(alive){
            maxHp += amount;
        }
    }
    public void increaseDamage(int damage){
        if(alive){
            attackDamage += damage;
        }
    }
    public void increaseSpeed(float amount){
        if(alive){
            speed += amount;
        }
    }

    public void attackEntity(Entity target) {
        if (attacking && alive) {
            float xDistance = Math.abs(this.getHitboxX() - target.getHitboxX());
            float yDistance = Math.abs(this.getHitboxY() - target.getHitboxY());
            float attackRange = width;

            if (xDistance < attackRange && yDistance < attackRange) {
                target.takeDamage(attackDamage);
            }
        }
    }

    // Combat getters and setters
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackDamage() { return attackDamage; }
    public void setAttackDamage(int attackDamage) { this.attackDamage = attackDamage; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public boolean isInvulnerable() { return invulnerable; }
}