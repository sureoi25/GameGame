package org.example.entity;

import org.example.GamePanel;
import org.example.tile.TileManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SlimeSpawner {
    private GamePanel gp;
    private TileManager tileManager;
    private Random random;
    private int maxSlimes = 10; // Configurable max number of slimes
    private ArrayList<Slime> slimes;
    private static final int MIN_SPAWN_DISTANCE = 300; // Minimum distance from player
    private static final int MAX_SPAWN_ATTEMPTS = 500; // Increased spawn attempts
    private static final int SLIME_WIDTH = 64;
    private static final int SLIME_HEIGHT = 64;

    // Map dimensions in tiles
    private static final int MAP_WIDTH_TILES = 40;
    private static final int MAP_HEIGHT_TILES = 40;

    public SlimeSpawner(GamePanel gp) {
        this.gp = gp;
        this.tileManager = gp.tileManager;
        this.random = new Random();
        this.slimes = new ArrayList<>();
    }

    public void renderSlimes(Graphics g, int cameraX, int cameraY) {
        for (Slime slime : slimes) {
            int slimeScreenX = (int)slime.getX() - cameraX;
            int slimeScreenY = (int)slime.getY() - cameraY;

            // Only render if on screen
            if (slimeScreenX + SLIME_WIDTH > 0 && slimeScreenX < gp.WORLD_WIDTH &&
                    slimeScreenY + SLIME_HEIGHT > 0 && slimeScreenY < gp.WORLD_HEIGHT) {

                Graphics slimeGraphics = g.create(slimeScreenX, slimeScreenY, SLIME_WIDTH, SLIME_HEIGHT);
                slime.render(slimeGraphics);
                slimeGraphics.dispose();
            }
        }
    }

    private Slime createSlimeAtRandomLocation() {
        // Calculate world boundaries in pixels (40x40 tiles)
        int worldWidthPixels = MAP_WIDTH_TILES * tileManager.TILE_SIZE;
        int worldHeightPixels = MAP_HEIGHT_TILES * tileManager.TILE_SIZE;

        // Try to find a valid position
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            int worldX = random.nextInt(
                    tileManager.TILE_SIZE, // Leave 1 tile border
                    worldWidthPixels - tileManager.TILE_SIZE // Leave 1 tile border
            );
            int worldY = random.nextInt(
                    tileManager.TILE_SIZE,
                    worldHeightPixels - tileManager.TILE_SIZE
            );

            if (isValidSpawnLocation(worldX, worldY)) {
                return new Slime(worldX, worldY, SLIME_WIDTH, SLIME_HEIGHT, gp);
            }
        }
        return null;
    }

    private boolean isValidSpawnLocation(int x, int y) {
        // 1. Check distance from player
        int distanceToPlayer = calculateDistanceToPlayer(x, y);
        if (distanceToPlayer < MIN_SPAWN_DISTANCE) {
            return false;
        }

        // 2. Check tile collision for the slime's area
        if (gp.collisionChecker.checkTileAtLocation(x, y, SLIME_WIDTH, SLIME_HEIGHT)) {
            return false;
        }

        // 3. Ensure within map bounds (with buffer for slime size)
        int worldWidthPixels = MAP_WIDTH_TILES * tileManager.TILE_SIZE;
        int worldHeightPixels = MAP_HEIGHT_TILES * tileManager.TILE_SIZE;

        if (x < SLIME_WIDTH || x > worldWidthPixels - SLIME_WIDTH ||
                y < SLIME_HEIGHT || y > worldHeightPixels - SLIME_HEIGHT) {
            return false;
        }

        return true;
    }

    public void spawnSlimes() {
        slimes.clear(); // Clear existing slimes before respawning

        int spawnedCount = 0;
        while (spawnedCount < maxSlimes) {
            Slime newSlime = createSlimeAtRandomLocation();
            if (newSlime != null) {
                slimes.add(newSlime);
                spawnedCount++;
                System.out.println("Spawned Slime at: " + newSlime.getX() + ", " + newSlime.getY());
            } else {
                System.out.println("Warning: Failed to find valid spawn location after attempts");
                break;
            }
        }
    }

    private int calculateDistanceToPlayer(int x, int y) {
        int playerX = (int)gp.player.getX();
        int playerY = (int)gp.player.getY();

        return (int)Math.sqrt(
                Math.pow(x - playerX, 2) +
                        Math.pow(y - playerY, 2)
        );
    }

    public void updateSlimes() {
        // Remove dead slimes
        slimes.removeIf(slime -> slime.getCurrentHp() <= 0);

        // Update existing slimes
        for (Slime slime : slimes) {
            slime.update();
        }

        // Maintain slime count
        if (slimes.size() < maxSlimes) {
            spawnSlimes();
        }
    }

    public ArrayList<Slime> getSlimes() {
        return slimes;
    }
}