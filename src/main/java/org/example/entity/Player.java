package org.example.entity;

import org.example.GamePanel;
import org.example.objects.SuperObject;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;

import static org.example.utils.constants.Directions.*;
import static org.example.utils.constants.PlayerConstants.*;
import static org.example.utils.constants.SlimeConstants.DEATH;

public class Player extends Entity {
    private BufferedImage spriteSheet;
    private int lastNonAttackDirection = DOWN;
    private int attackTick = 0;
    private int attackCooldown = 30;
    public int worldX;
    public int worldY;
    public final int screenX;
    public final int screenY;
    private GamePanel gp;
    private SuperObject inventory[] = new SuperObject[50];
    private int inventorySize = 0;
    private boolean hasKey = false;
    private Random random = new Random();
    private int nearbyObjectIndex = 999;
    private boolean dead = false;
    private boolean attackSoundPlayed = false;
    private boolean damageAppliedThisAttack = false;

    public Player(float x, float y, int width, int height, GamePanel gp) {
        super(x, y, width, height);
        this.gp = gp;
        this.oldX = x;
        this.oldY = y;
        this.worldX = (int)x;
        this.worldY = (int)y;
        this.screenX = gp.PANEL_WIDTH / 2;
        this.screenY = gp.PANEL_HEIGHT / 2;
        this.collisionDetected = false;

        // Player hitbox
        this.hitboxWidth = width / 2;
        this.hitboxHeight = height / 3;
        this.hitboxOffsetX = (width - hitboxWidth) / 2;
        this.hitboxOffsetY = height - hitboxHeight;

        // Initialize solidArea Rectangle for collision detection
        this.solidAreaDefaultX = hitboxOffsetX;
        this.solidAreaDefaultY = hitboxOffsetY;
        this.solidArea = new Rectangle(solidAreaDefaultX, solidAreaDefaultY, hitboxWidth, hitboxHeight);

        loadAnimations();
        this.state = IDLE_DOWN;
        this.aniSpeed = 15;
        this.direction = 2;
        this.setSpeed(2.0f);

        // Initialize player stats
        initStats(100, 10);
    }

    @Override
    public void update() {
        updateAnimationTick();
        updateCooldowns();

        // Check tile collision first
        boolean tileCollision = gp.collisionChecker.checkTile(this);

        if (!tileCollision) {
            // Only check object collision if no tile collision was detected
            nearbyObjectIndex = gp.collisionChecker.checkObject(this, true);
        }

        // Only update position if no collisions detected
        if (!collisionDetected) {
            updatePosition();
        } else {
            // Reset position if collision detected
            x = oldX;
            y = oldY;
            worldX = (int)x;
            worldY = (int)y;
            collisionDetected = false;
        }

        setAnimation();
    }

    public void interact() {
        if (nearbyObjectIndex != 999) {
            interactWithObject(nearbyObjectIndex);
        }
    }

    private void interactWithObject(int index) {
        if (gp.obj[index] != null) {
            String objectName = gp.obj[index].name;

            switch (objectName) {
                case "Key":
                    addToInventory(gp.obj[index]);
                    hasKey = true;
                    gp.playSE(5);
                    gp.obj[index] = null;
                    collisionDetected = false; // Ensure no collision after pickup
                    break;

                case "chest":
                    if (hasKey) {
                        gp.playSE(5);
                        openChest(index);
                        removeKey();
                        collisionDetected = false; // Ensure no collision after opening
                    } else {
                        System.out.println("You need a key to open this chest!");
                        if (gp.obj[index].collision) {
                            collisionDetected = true;
                        }
                    }
                    break;

                case "chicken":
                    heal(20);
                    gp.playSE(4);
                    gp.obj[index] = null;
                    collisionDetected = false;
                    break;
                case "blue mushroom":
                    increaseSpeed(0.5f);
                    gp.playSE(6);
                    gp.obj[index] = null;
                    collisionDetected = false;
                    break;
                case "red mushroom":
                    increaseHP(10);
                    gp.playSE(6);
                    gp.obj[index] = null;
                    collisionDetected = false;
                    break;
                case "pork":
                    increaseDamage(10);
                    gp.playSE(4);
                    gp.obj[index] = null;
                    collisionDetected = false;
                    break;
                default:
                    if (gp.obj[index].collision) {
                        collisionDetected = true;
                    } else {
                        collisionDetected = false;
                    }
                    break;
            }
        }
    }

    private void addToInventory(SuperObject obj) {
        if (inventorySize < inventory.length) {
            inventory[inventorySize] = obj;
            inventorySize++;
            System.out.println(obj.name + " added to inventory!");
        } else {
            System.out.println("Inventory full!");
        }
    }

    private void removeKey() {
        boolean keyRemoved = false;
        for (int i = 0; i < inventorySize; i++) {
            if (inventory[i] != null && inventory[i].name.equals("Key")) {
                // Remove key by shifting inventory items
                for (int j = i; j < inventorySize - 1; j++) {
                    inventory[j] = inventory[j + 1];
                }
                inventory[inventorySize - 1] = null;
                inventorySize--;
                keyRemoved = true;
                break;
            }
        }

        if (keyRemoved) {
            System.out.println("Used a key to open the chest!");
            hasKey = hasKeyInInventory();
        }
    }

    private boolean hasKeyInInventory() {
        for (int i = 0; i < inventorySize; i++) {
            if (inventory[i] != null && inventory[i].name.equals("Key")) {
                return true;
            }
        }
        return false;
    }

    private void openChest(int chestIndex) {
        String[] possibleItems = {"chicken", "blue mushroom", "red mushroom", "pork"};
        int randomIndex = random.nextInt(possibleItems.length);
        String randomItem = possibleItems[randomIndex];

        int itemX = gp.obj[chestIndex].worldX;
        int itemY = gp.obj[chestIndex].worldY;

        gp.obj[chestIndex] = null;

        SuperObject newItem = gp.objectFactory.createObject(randomItem, itemX, itemY);

        if (newItem == null) {
            System.err.println("ERROR: Failed to create item: " + randomItem);
            newItem = gp.objectFactory.createObject("Key", itemX, itemY);
            if (newItem == null) {
                System.err.println("ERROR: Failed to create fallback item. Chest will remain empty.");
                return;
            }
        }

        newItem.worldX = itemX;
        newItem.worldY = itemY;
        newItem.collision = false;
        gp.obj[chestIndex] = newItem;
    }

    public void render(Graphics g, int cameraX, int cameraY) {
        BufferedImage currentFrame = animations[state][aniIndex];
        int screenX = (int)x - cameraX;
        int screenY = (int)y - cameraY;

        if (entityDirection == LEFT &&
                (state == RUNNING_SIDE || state == IDLE_SIDE || state == ATTACK_SIDE)) {
            g.drawImage(currentFrame, screenX + width, screenY, -width, height, null);
        } else {
            g.drawImage(currentFrame, screenX, screenY, width, height, null);
        }
    }

    @Override
    public void render(Graphics g) {
        render(g, 0, 0);
    }

    @Override
    protected void updatePosition() {
        if (moving && !attacking) {
            oldX = x;
            oldY = y;

            if (!collisionDetected) {
                switch (entityDirection) {
                    case LEFT: x -= speed; break;
                    case UP: y -= speed; break;
                    case RIGHT: x += speed; break;
                    case DOWN: y += speed; break;
                }

                worldX = (int)x;
                worldY = (int)y;
            }
        }
    }

    @Override
    public void setDirection(int direction) {
        if (!attacking) {
            this.entityDirection = direction;
            this.lastNonAttackDirection = direction;
            this.moving = true;
            oldX = x;
            oldY = y;
        }
    }

    public void attack() {
        if (!attacking) {
            attacking = true;
            attackTick = 0;
        }
    }

    @Override
    protected void updateCooldowns() {
        if (attacking) {
            attackTick++;
            if (attackTick >= attackCooldown) {
                attacking = false;
                attackTick = 0;
                entityDirection = lastNonAttackDirection;
                attackSoundPlayed = false; // Reset sound for next attack
                damageAppliedThisAttack = false; // Reset damage for next attack
            }
        } else {
            attackSoundPlayed = false; // Reset if attack is interrupted
            damageAppliedThisAttack = false;
        }
        super.updateCooldowns();
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
                if (is != null) is.close();
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

    public boolean isDead() {
        return dead;
    }

    @Override
    protected void die() {
        super.die();
        this.dead = true;
        gp.sound.stop();
        gp.playSE(8);
        state = DEATH;
        aniIndex = 0;
        aniTick = 0;
        moving = false;
    }

    public void restart() {
        this.initStats(100,10);
        this.setSpeed(2.0f);
        this.dead = false;
        this.state = IDLE_DOWN;
        this.x = 288;
        this.y = 96;
        this.worldX = (int)x;
        this.worldY = (int)y;
        this.alive = true;
    }

    public void takeDamage(int damage) {
        gp.playSE(2);
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

    public void attackNearbyEntities(SlimeManager slimeManager) {
        if (!attackSoundPlayed) {
            gp.playSE(7);
            attackSoundPlayed = true;
        }
        if (damageAppliedThisAttack) return;
        int cameraX = gp.getCameraX();
        int cameraY = gp.getCameraY();
        int screenWidth = gp.PANEL_WIDTH;
        int screenHeight = gp.PANEL_HEIGHT;
        for (Slime slime : slimeManager.getSlimes()) {
            float slimeX = slime.getX();
            float slimeY = slime.getY();
            // Only check slimes that are on screen
            if (slimeX + slime.getWidth() < cameraX || slimeX > cameraX + screenWidth ||
                slimeY + slime.getHeight() < cameraY || slimeY > cameraY + screenHeight) {
                continue;
            }
            float xDistance = Math.abs(this.getHitboxX() - slime.getHitboxX());
            float yDistance = Math.abs(this.getHitboxY() - slime.getHitboxY());
            float attackRange = width;
            if (xDistance < attackRange && yDistance < attackRange && attacking) {
                slime.takeDamage(this.attackDamage);
            }
        }
        damageAppliedThisAttack = true;
    }
}
