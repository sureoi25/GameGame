package org.example.entity;

import org.example.GamePanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SlimeManager {
    private GamePanel gamePanel;
    private ArrayList<Slime> slimes;
    private Random random;

    // Wave-based spawning
    private int currentWave = 0;
    private int totalSlimesInWave = 0;
    private int killedSlimesInWave = 0;
    private boolean waveActive = false;
    private int waveDisplayTick = 0;
    private final int WAVE_DISPLAY_TIME = 180; // 3 seconds at 60 FPS

    // Wave configurations
    public final int[] WAVE_SLIME_COUNTS = {10, 12, 15};

    // World and spawn parameters
    private final int WORLD_WIDTH_TILES;
    private final int WORLD_HEIGHT_TILES;
    private final int TILE_SIZE;

    private final int DETECTION_RANGE = 300;
    private final int DESPAWN_RANGE = 1000; // Increased despawn range
    private final int SPAWN_SAFE_DISTANCE = 400; // Increased safe spawn distance
    private boolean playerHasTakenDamage = false;
    private int playerDamageCooldown = 60; // 1 second at 60 FPS
    private int playerDamageTick = 0;

    public SlimeManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.slimes = new ArrayList<>();
        this.random = new Random();
        this.currentWave = 0;
        this.killedSlimesInWave = 0;
        this.totalSlimesInWave = 0;

        // Get world dimensions from GamePanel
        this.WORLD_WIDTH_TILES = gamePanel.WORLD_WIDTH_TILES;
        this.WORLD_HEIGHT_TILES = gamePanel.WORLD_HEIGHT_TILES;
        this.TILE_SIZE = gamePanel.TILE_SIZE;
    }

    public void startNextWave() {
        if (currentWave >= WAVE_SLIME_COUNTS.length) {
            return;
        }

        // Reset wave tracking
        killedSlimesInWave = 0;
        totalSlimesInWave = WAVE_SLIME_COUNTS[currentWave];
        waveActive = true;
        waveDisplayTick = WAVE_DISPLAY_TIME;

        // Clear any remaining slimes from previous wave
        slimes.clear();

        // Spawn slimes for this wave - improved spawning reliability
        for (int i = 0; i < totalSlimesInWave; i++) {
            Slime spawned = spawnSlimeAtRandomLocation();
            if (spawned != null) {
                System.out.println("DEBUG: Spawned slime " + (i + 1) + "/" + totalSlimesInWave);
            } else {
                // Retry if spawn failed
                i--;
            }
        }

        // Increment wave
        currentWave++;
    }

    public Slime spawnSlimeAtRandomLocation() {
        int spawnAttempts = 0;
        int maxSpawnAttempts = 1000;
        int x, y;

        // Define size for the spawn area - stay away from edges
        int safeMargin = TILE_SIZE;
        int maxX = (WORLD_WIDTH_TILES * TILE_SIZE) - (2 * safeMargin);
        int maxY = (WORLD_HEIGHT_TILES * TILE_SIZE) - (2 * safeMargin);

        while (spawnAttempts < maxSpawnAttempts) {
            // Generate position within safe bounds to avoid edge issues
            x = safeMargin + random.nextInt(maxX);
            y = safeMargin + random.nextInt(maxY);

            spawnAttempts++;

            // Debug output
            if (spawnAttempts % 50 == 0) {
                System.out.println("DEBUG: Attempting to spawn slime at (" + x + "," + y + ") - attempt " + spawnAttempts);
            }

            if (isValidSpawnLocation(x, y)) {
                Slime slime = new Slime(x, y);
                slimes.add(slime);
                System.out.println("DEBUG: Slime spawned successfully at (" + x + "," + y + ")");
                return slime;
            }
        }

        // If we failed with random spawning, use a grid-based approach
        System.out.println("DEBUG: Using grid-based fallback spawning");

        // Try a more structured approach by scanning the map in a grid pattern
        int gridStep = TILE_SIZE * 2; // Look at every second tile for efficiency

        for (int gridX = safeMargin; gridX < (WORLD_WIDTH_TILES * TILE_SIZE) - safeMargin; gridX += gridStep) {
            for (int gridY = safeMargin; gridY < (WORLD_HEIGHT_TILES * TILE_SIZE) - safeMargin; gridY += gridStep) {
                // Add some randomness to the grid positions
                x = gridX + random.nextInt(TILE_SIZE);
                y = gridY + random.nextInt(TILE_SIZE);

                if (isValidSpawnLocation(x, y)) {
                    Slime slime = new Slime(x, y);
                    slimes.add(slime);
                    System.out.println("DEBUG: Slime spawned successfully using grid method at (" + x + "," + y + ")");
                    return slime;
                }
            }
        }

        System.out.println("DEBUG: Failed to spawn slime after exhaustive search");
        return null;
    }

    private boolean isValidSpawnLocation(int x, int y) {
        Player player = gamePanel.player;
        float playerX = player.getX();
        float playerY = player.getY();

        // Check if inside map boundaries with margin
        int margin = TILE_SIZE / 2;
        int mapWidth = WORLD_WIDTH_TILES * TILE_SIZE;
        int mapHeight = WORLD_HEIGHT_TILES * TILE_SIZE;
        if (x < margin || x >= (mapWidth - margin) || y < margin || y >= (mapHeight - margin)) {
            return false;
        }

        // Check distance from player
        float distanceToPlayer = (float) Math.sqrt(
                Math.pow(x - playerX, 2) +
                        Math.pow(y - playerY, 2)
        );

        // Check if spawn is far enough from player
        if (distanceToPlayer <= SPAWN_SAFE_DISTANCE) {
            return false;
        }

        // Check if spawn point is on a non-collision tile
        return !gamePanel.collisionChecker.checkTileAtLocation(x, y, TILE_SIZE, TILE_SIZE);
    }

    public void update() {
        // Damage cooldown for player
        if (playerHasTakenDamage) {
            playerDamageTick++;
            if (playerDamageTick >= playerDamageCooldown) {
                playerHasTakenDamage = false;
            }
        }

        // Wave display timer
        if (waveDisplayTick > 0) {
            waveDisplayTick--;
        }

        // Check for wave completion
        if (waveActive && killedSlimesInWave >= totalSlimesInWave) {
            System.out.println("DEBUG: Wave " + currentWave + " completed!");
            waveActive = false;
            waveDisplayTick = WAVE_DISPLAY_TIME;

            // Start next wave if available
            if (currentWave < WAVE_SLIME_COUNTS.length) {
                System.out.println("DEBUG: Starting wave " + (currentWave + 1));
                startNextWave();
            } else {
                System.out.println("DEBUG: All waves completed!");
            }
        }

        // Ensure first wave starts
        if (currentWave == 0 && !waveActive) {
            System.out.println("DEBUG: Starting first wave");
            startNextWave();
        }

        // Get camera and screen info for culling
        int cameraX = gamePanel.getCameraX();
        int cameraY = gamePanel.getCameraY();
        int screenWidth = gamePanel.PANEL_WIDTH;
        int screenHeight = gamePanel.PANEL_HEIGHT;
        float playerX = gamePanel.player.getX();
        float playerY = gamePanel.player.getY();
        float cullRadius = 1.5f * screenWidth; // Update slimes within 1.5x screen width of player

        // Update existing slimes
        Iterator<Slime> iterator = slimes.iterator();
        while (iterator.hasNext()) {
            Slime slime = iterator.next();

            // Handle null slime (shouldn't happen but just in case)
            if (slime == null) {
                System.err.println("DEBUG: Null slime found in list!");
                iterator.remove();
                continue;
            }

            // Track killed slimes
            if (!slime.isAlive()) {
                iterator.remove();
                killedSlimesInWave++;
                System.out.println("DEBUG: Slime died. Killed slimes: " + killedSlimesInWave + "/" + totalSlimesInWave);
                continue;
            }

            // Only update slimes that are on screen or near the player
            float slimeX = slime.getX();
            float slimeY = slime.getY();
            boolean onScreen = !(slimeX + slime.getWidth() < cameraX || slimeX > cameraX + screenWidth ||
                                 slimeY + slime.getHeight() < cameraY || slimeY > cameraY + screenHeight);
            float distToPlayer = (float)Math.hypot(slimeX - playerX, slimeY - playerY);
            if (onScreen || distToPlayer < cullRadius) {
                updateSlimeBehavior(slime);
                slime.update();
            }
            // Always keep slimes within bounds after movement
            keepSlimeInBounds(slime);
        }
    }

    // New method to keep slimes within the map boundaries
    private void keepSlimeInBounds(Slime slime) {
        int mapWidth = WORLD_WIDTH_TILES * TILE_SIZE;
        int mapHeight = WORLD_HEIGHT_TILES * TILE_SIZE;
        int margin = 5; // Small margin to prevent getting exactly on the edge

        float x = slime.getX();
        float y = slime.getY();

        // Calculate bounds accounting for slime size
        int slimeWidth = slime.getWidth();
        int slimeHeight = slime.getHeight();

        // Apply boundary restrictions
        if (x < margin) {
            slime.setX(margin);
        } else if (x + slimeWidth >= mapWidth - margin) {
            slime.setX(mapWidth - slimeWidth - margin);
        }

        if (y < margin) {
            slime.setY(margin);
        } else if (y + slimeHeight >= mapHeight - margin) {
            slime.setY(mapHeight - slimeHeight - margin);
        }
    }

    public List<Slime> getSlimes() {
        return slimes;
    }

    public void render(Graphics g, int cameraX, int cameraY) {
        // Render slimes
        for (Slime slime : slimes) {
            if (isOnScreen(slime, cameraX, cameraY)) {
                slime.renderWithCamera(g, cameraX, cameraY);
            }
        }

        // Render wave text
        renderWaveText(g);
    }

    private void renderWaveText(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 48));

        if (gamePanel.player.isDead()) {
            // Game Over Screen
            g.setColor(new Color(255, 0, 0, 200)); // Semi-transparent red
            String gameOverText = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            int x = (gamePanel.PANEL_WIDTH - textWidth) / 2;
            int y = gamePanel.PANEL_HEIGHT / 2;
            g.drawString(gameOverText, x, y);

            // Optional: Add restart or score information
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String restartText = "Press R to Restart";
            int restartWidth = g.getFontMetrics().stringWidth(restartText);
            g.drawString(restartText, (gamePanel.PANEL_WIDTH - restartWidth) / 2, y + 50);
        } else if (waveDisplayTick > 0) {
            g.setColor(new Color(255, 0, 0, 200)); // Semi-transparent red

            String waveText;
            if (currentWave > 0 && currentWave <= WAVE_SLIME_COUNTS.length) {
                waveText = "WAVE " + currentWave;
            } else if (currentWave > WAVE_SLIME_COUNTS.length) {
                waveText = "ALL WAVES COMPLETE!";
            } else {
                waveText = "WAVE 1";
            }

            // Center the text
            int textWidth = g.getFontMetrics().stringWidth(waveText);
            int x = (gamePanel.PANEL_WIDTH - textWidth) / 2;
            int y = gamePanel.PANEL_HEIGHT / 2;

            g.drawString(waveText, x, y);
        }
    }

    private void updateSlimeBehavior(Slime slime) {
        Player player = gamePanel.player;
        float slimeX = slime.getX();
        float slimeY = slime.getY();
        float playerX = player.getX();
        float playerY = player.getY();

        float distanceToPlayer = (float) Math.sqrt(
                Math.pow(slimeX - playerX, 2) +
                        Math.pow(slimeY - playerY, 2)
        );

        if (distanceToPlayer <= DETECTION_RANGE) {
            int direction = determineDirectionToPlayer(slimeX, slimeY, playerX, playerY);
            slime.setDirection(direction);
            slime.setMoving(true);

            // Damage player with cooldown
            if (distanceToPlayer < slime.getWidth()) {
                // Player damage cooldown mechanism
                if (!playerHasTakenDamage) {
                    slime.attack(); // Ensure attack animation
                    player.takeDamage(slime.getAttackDamage());
                    playerHasTakenDamage = true;
                    playerDamageTick = 0;
                    System.out.println("Slime attacked player! Damage: " + slime.getAttackDamage());
                    System.out.println("Player HP: " + player.getCurrentHp());
                }
            }
        } else {
            // Random movement when player is not nearby
            if (random.nextInt(200) < 5) {
                slime.setDirection(random.nextInt(4));
                slime.setMoving(random.nextBoolean());
            }
        }
    }

    private boolean isSlimeTooFarFromPlayer(Slime slime) {
        Player player = gamePanel.player;
        float playerX = player.getX();
        float playerY = player.getY();

        float distanceToPlayer = (float) Math.sqrt(
                Math.pow(slime.getX() - playerX, 2) +
                        Math.pow(slime.getY() - playerY, 2)
        );

        return distanceToPlayer > DESPAWN_RANGE;
    }

    private int determineDirectionToPlayer(float slimeX, float slimeY, float playerX, float playerY) {
        float dx = playerX - slimeX;
        float dy = playerY - slimeY;

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? 2 : 0; // RIGHT : LEFT
        } else {
            return dy > 0 ? 3 : 1; // DOWN : UP
        }
    }

    private boolean isOnScreen(Slime slime, int cameraX, int cameraY) {
        int screenWidth = gamePanel.PANEL_WIDTH;
        int screenHeight = gamePanel.PANEL_HEIGHT;

        float slimeX = slime.getX();
        float slimeY = slime.getY();

        return (slimeX >= cameraX && slimeX <= cameraX + screenWidth &&
                slimeY >= cameraY && slimeY <= cameraY + screenHeight);
    }

    // Getter for current wave
    public int getCurrentWave() {
        return currentWave;
    }

    // Getter for wave active status
    public boolean isWaveActive() {
        return waveActive;
    }

    // Getter for killed slimes count
    public int getKilledSlimesInWave() {
        return killedSlimesInWave;
    }

    // Getter for total slimes in current wave
    public int getTotalSlimesInWave() {
        return totalSlimesInWave;
    }

    // Getter to check if waves are complete
    public boolean areWavesComplete() {
        boolean completed = currentWave >= WAVE_SLIME_COUNTS.length && !waveActive && killedSlimesInWave >= totalSlimesInWave;

        if (completed) {
            System.out.println("DEBUG: Waves completed - currentWave: " + currentWave +
                    ", totalWaves: " + WAVE_SLIME_COUNTS.length +
                    ", waveActive: " + waveActive +
                    ", killedSlimes: " + killedSlimesInWave +
                    ", totalSlimes: " + totalSlimesInWave);
        }

        return completed;
    }
}
