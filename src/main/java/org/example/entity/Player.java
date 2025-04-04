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
    public int worldX;    // Current position in world coordinates
    public int worldY;
    public final int screenX; // Screen center position
    public final int screenY;
    private GamePanel gp;
    private SuperObject inventory[] = new SuperObject[50];
    private int inventorySize = 0;
    private boolean hasKey = false;
    private Random random = new Random();
    private int nearbyObjectIndex = 999;
    private boolean dead = false;


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
        initStats(100, 10); // Example values: 100 HP, 10 attack damage
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

            // We no longer directly interact here
            // The interaction will happen when 'E' is pressed
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

    // Add this new method to handle interactions when 'E' is pressed
    public void interact() {
        // Only interact if there's a nearby object
        if (nearbyObjectIndex != 999) {
            interactWithObject(nearbyObjectIndex);
        }
    }

    private void interactWithObject(int index) {
        if (gp.obj[index] != null) {
            String objectName = gp.obj[index].name;

            switch (objectName) {
                case "Key":
                    // Add key to inventory
                    addToInventory(gp.obj[index]);
                    hasKey = true;
                    gp.playSE(5);
                    gp.obj[index] = null; // Remove from map after picking up
                    break;

                case "chest":
                    // Only open if player has a key
                    if (hasKey) {
                        gp.playSE(5);
                        openChest(index);
                        removeKey(); // Consume key
                    } else {
                        System.out.println("You need a key to open this chest!");
                    }
                    break;

                case "chicken":
                    heal(20);
                    gp.playSE(4);
                    gp.obj[index] = null; // Remove after use
                    break;
                case "blue mushroom":
                    increaseSpeed(0.5f);
                    gp.playSE(6);
                    gp.obj[index] = null; // Remove after use
                    break;
                case "red mushroom":
                    increaseHP(10);
                    gp.playSE(6);
                    gp.obj[index] = null; // Remove after use
                    break;
                case "pork":
                    increaseDamage(10);
                    gp.playSE(4);
                    gp.obj[index] = null;
                    break;
                default:
                    // Handle generic object collision
                    if (gp.obj[index].collision) {
                        collisionDetected = true;
                    }
                    break;
            }
        }
    }

    // Add an object to player's inventory
    private void addToInventory(SuperObject obj) {
        if (inventorySize < inventory.length) {
            inventory[inventorySize] = obj;
            inventorySize++;
            System.out.println(obj.name + " added to inventory!");
        } else {
            System.out.println("Inventory full!");
        }
    }

    // Remove a key from inventory after use
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

        // Update key status
        if (keyRemoved) {
            System.out.println("Used a key to open the chest!");
            hasKey = hasKeyInInventory();
        }
    }

    // Check if player still has any keys in inventory
    private boolean hasKeyInInventory() {
        for (int i = 0; i < inventorySize; i++) {
            if (inventory[i] != null && inventory[i].name.equals("Key")) {
                return true;
            }
        }
        return false;
    }

    // Open chest and spawn a random item
    private void openChest(int chestIndex) {
        // Define possible loot items (excluding key and chest)
        String[] possibleItems = {"chicken", "blue mushroom", "red mushroom", "pork"};

        // Generate random item
        int randomIndex = random.nextInt(possibleItems.length);
        String randomItem = possibleItems[randomIndex];

        // Get chest position before removing it
        int itemX = gp.obj[chestIndex].worldX;
        int itemY = gp.obj[chestIndex].worldY;

        // Debug output
        System.out.println("Opening chest at position: " + itemX + "," + itemY);
        System.out.println("Selected random item: " + randomItem);

        // Remove chest
        gp.obj[chestIndex] = null;

        // Use the ObjectFactory to create the new item
        SuperObject newItem = gp.objectFactory.createObject(randomItem, itemX, itemY);

        // Validate the new item
        if (newItem == null) {
            System.err.println("ERROR: Failed to create item: " + randomItem);
            // Create a fallback item if possible
            newItem = gp.objectFactory.createObject("Key", itemX, itemY);
            if (newItem == null) {
                System.err.println("ERROR: Failed to create fallback item. Chest will remain empty.");
                return;
            }
        }

        // Ensure the new item has proper coordinates and image
        newItem.worldX = itemX;
        newItem.worldY = itemY;

        // Make sure the item has collision set appropriately (usually false for pickups)
        newItem.collision = false;

        // Place new item in the world (in the same slot as the chest)
        gp.obj[chestIndex] = newItem;

        System.out.println("Successfully spawned " + randomItem + " at position: " + itemX + "," + itemY);
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
            // Save old position before moving
            oldX = x;
            oldY = y;

            // Only move if no collision detected
            if (!collisionDetected) {
                switch (entityDirection) {
                    case LEFT: x -= speed; break;
                    case UP: y -= speed; break;
                    case RIGHT: x += speed; break;
                    case DOWN: y += speed; break;
                }

                // Update world coordinates
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

            // Save old position when changing direction
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
            }
        }

        super.updateCooldowns(); // Call parent method for invulnerability timer
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
        // Reset player state
        this.currentHp = 100; // Default max health
        this.dead = false;
        this.state = IDLE_DOWN;
        this.x = 288; // Default spawn point
        this.y = 96;
        this.worldX = (int)x;
        this.worldY = (int)y;
        this.alive = true;

    }
    public void takeDamage(int damage) {
        gp.playSE(2);
        if (!invulnerable && alive) {
            System.out.println("Player taking damage: " + damage);
            System.out.println("Current HP before damage: " + currentHp);

            currentHp -= damage;

            System.out.println("Current HP after damage: " + currentHp);

            if (currentHp <= 0) {
                currentHp = 0;
                System.out.println("Player died!");
                die();
            }

            invulnerable = true;
            invulnerabilityTime = 0;

            System.out.println("Player is now invulnerable for a short time");
        } else if (invulnerable) {
            System.out.println("Player is currently invulnerable");
        }
    }
    public void attackNearbyEntities(SlimeManager slimeManager) {
        gp.playSE(7);
        for (Slime slime : slimeManager.getSlimes()) {
            // Check if slime is within attack range
            float xDistance = Math.abs(this.getHitboxX() - slime.getHitboxX());
            float yDistance = Math.abs(this.getHitboxY() - slime.getHitboxY());
            float attackRange = width; // Adjust as needed

            if (xDistance < attackRange && yDistance < attackRange && attacking) {
                slime.takeDamage(this.attackDamage);
                System.out.println("Player attacked slime! Damage: " + this.attackDamage);
            }
        }
    }
}