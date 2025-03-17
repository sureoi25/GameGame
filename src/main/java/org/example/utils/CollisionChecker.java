package org.example.utils;

import org.example.entity.Entity;
import org.example.tile.TileManager;

public class CollisionChecker {
    private TileManager tileManager;

    public CollisionChecker(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public boolean checkTile(Entity entity) {
        // Get entity's current position
        float entityX = entity.getX();
        float entityY = entity.getY();
        int entityWidth = entity.getWidth();
        int entityHeight = entity.getHeight();

        // Create a smaller hitbox that better represents the entity's actual size
        int hitboxWidth = entityWidth / 2;  // Half width
        int hitboxHeight = entityHeight / 3; // One-third height, at the bottom

        // Position the hitbox at the center-bottom of the sprite
        int hitboxOffsetX = (entityWidth - hitboxWidth) / 2; // Center horizontally
        int hitboxOffsetY = entityHeight - hitboxHeight; // Bottom aligned

        // Calculate hitbox coordinates
        float hitboxX = entityX + hitboxOffsetX;
        float hitboxY = entityY + hitboxOffsetY;

        // Calculate next position based on direction and speed
        float nextHitboxX = hitboxX;
        float nextHitboxY = hitboxY;

        switch (entity.getDirection()) {
            case 0: // LEFT
                nextHitboxX = hitboxX - entity.getSpeed();
                break;
            case 1: // UP
                nextHitboxY = hitboxY - entity.getSpeed();
                break;
            case 2: // RIGHT
                nextHitboxX = hitboxX + entity.getSpeed();
                break;
            case 3: // DOWN
                nextHitboxY = hitboxY + entity.getSpeed();
                break;
        }

        // Check world boundaries
        int worldWidth = tileManager.MAX_MAP_COL * tileManager.TILE_SIZE;
        int worldHeight = tileManager.MAX_MAP_ROW * tileManager.TILE_SIZE;

        // Check if the entity would go out of world bounds
        if (nextHitboxX < 0 || nextHitboxX + hitboxWidth > worldWidth ||
                nextHitboxY < 0 || nextHitboxY + hitboxHeight > worldHeight) {
            return true; // Out of world bounds is considered a collision
        }

        // Check all four corners of the hitbox
        return checkPoint(nextHitboxX, nextHitboxY) || // Top-left
                checkPoint(nextHitboxX + hitboxWidth, nextHitboxY) || // Top-right
                checkPoint(nextHitboxX, nextHitboxY + hitboxHeight) || // Bottom-left
                checkPoint(nextHitboxX + hitboxWidth, nextHitboxY + hitboxHeight); // Bottom-right
    }

    private boolean checkPoint(float x, float y) {
        // Convert world coordinates to tile coordinates
        int tileCol = (int) (x / tileManager.TILE_SIZE);
        int tileRow = (int) (y / tileManager.TILE_SIZE);

        //check out of bounds
        if (tileCol < 0 || tileCol >= tileManager.MAX_MAP_COL ||
                tileRow < 0 || tileRow >= tileManager.MAX_MAP_ROW) {
            return true; //out of bounds collide
        }

        //check if tile has collision
        return tileManager.tiles[tileManager.mapTileNum[tileRow][tileCol]].collision;
    }
}