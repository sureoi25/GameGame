package org.example.utils;

import org.example.entity.Entity;
import org.example.objects.SuperObject;
import org.example.tile.TileManager;
import java.awt.Rectangle;

public class CollisionChecker {
    private TileManager tileManager;

    public CollisionChecker(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public boolean checkTile(Entity entity) {
        // Get entity's hitbox position from the entity class
        float hitboxX = entity.getHitboxX();
        float hitboxY = entity.getHitboxY();
        int hitboxWidth = entity.getHitboxWidth();
        int hitboxHeight = entity.getHitboxHeight();

        // Calculate next position based on direction (now using entity methods)
        float nextHitboxX = entity.getNextHitboxX();
        float nextHitboxY = entity.getNextHitboxY();

        // Check world boundaries
        int worldWidth = tileManager.MAX_MAP_COL * tileManager.TILE_SIZE;
        int worldHeight = tileManager.MAX_MAP_ROW * tileManager.TILE_SIZE;

        if (nextHitboxX < 0 || nextHitboxX + hitboxWidth > worldWidth ||
                nextHitboxY < 0 || nextHitboxY + hitboxHeight > worldHeight) {
            return true;
        }

        // Check all four corners of the hitbox
        boolean collision = checkPoint(nextHitboxX, nextHitboxY) ||
                checkPoint(nextHitboxX + hitboxWidth, nextHitboxY) ||
                checkPoint(nextHitboxX, nextHitboxY + hitboxHeight) ||
                checkPoint(nextHitboxX + hitboxWidth, nextHitboxY + hitboxHeight);

        // Set collision detection flag on entity
        entity.setCollisionDetected(collision);

        return collision;
    }

    public boolean checkTileAtLocation(int x, int y, int width, int height) {
        // Convert coordinates to tile coordinates
        int leftTile = x / tileManager.TILE_SIZE;
        int rightTile = (x + width) / tileManager.TILE_SIZE;
        int topTile = y / tileManager.TILE_SIZE;
        int bottomTile = (y + height) / tileManager.TILE_SIZE;

        // Ensure within world bounds
        if (leftTile < 0 || rightTile >= tileManager.MAX_MAP_COL ||
                topTile < 0 || bottomTile >= tileManager.MAX_MAP_ROW) {
            return true; // Out of bounds is considered a collision
        }

        // Check each tile in the entity's bounding box
        for (int row = topTile; row <= bottomTile; row++) {
            for (int col = leftTile; col <= rightTile; col++) {
                int tileNum = tileManager.mapTileNum[row][col];

                // Check if the tile has collision
                if (tileManager.tiles[tileNum].collision) {
                    return true; // Collision detected
                }
            }
        }
        return false; // No collision
    }

    public int checkObject(Entity entity, boolean player) {
        int index = 999; // Default return value when no collision

        // Store original solidArea position
        int entitySolidAreaX = entity.solidArea.x;
        int entitySolidAreaY = entity.solidArea.y;

        // Check all objects in the game
        for (int i = 0; i < tileManager.gp.obj.length; i++) {
            if (tileManager.gp.obj[i] != null) {
                // Get original object solidArea position
                int objSolidAreaX = tileManager.gp.obj[i].solidArea.x;
                int objSolidAreaY = tileManager.gp.obj[i].solidArea.y;

                // Update entity's solidArea position for world coordinates
                entity.solidArea.x = (int)entity.getX() + entity.solidAreaDefaultX;
                entity.solidArea.y = (int)entity.getY() + entity.solidAreaDefaultY;

                // Update object's solidArea position for world coordinates
                tileManager.gp.obj[i].solidArea.x = tileManager.gp.obj[i].worldX + tileManager.gp.obj[i].solidArea.x;
                tileManager.gp.obj[i].solidArea.y = tileManager.gp.obj[i].worldY + tileManager.gp.obj[i].solidArea.y;

                // Adjust entity's solidArea position based on direction
                switch (entity.getDirection()) {
                    case 0: // left
                        entity.solidArea.x -= entity.getSpeed();
                        break;
                    case 1: // up
                        entity.solidArea.y -= entity.getSpeed();
                        break;
                    case 2: // right
                        entity.solidArea.x += entity.getSpeed();
                        break;
                    case 3: // down
                        entity.solidArea.y += entity.getSpeed();
                        break;
                }

                // Check collision
                if (entity.solidArea.intersects(tileManager.gp.obj[i].solidArea)) {
                    if (tileManager.gp.obj[i].collision) {
                        entity.setCollisionDetected(true);
                    }
                    if (player) {
                        index = i;
                    }
                }

                // Reset solidArea positions
                entity.solidArea.x = entitySolidAreaX;
                entity.solidArea.y = entitySolidAreaY;
                tileManager.gp.obj[i].solidArea.x = objSolidAreaX;
                tileManager.gp.obj[i].solidArea.y = objSolidAreaY;
            }
        }

        return index;
    }

    private boolean checkPoint(float x, float y) {
        // Convert world coordinates to tile coordinates
        int tileCol = (int) (x / tileManager.TILE_SIZE);
        int tileRow = (int) (y / tileManager.TILE_SIZE);

        // Check out of bounds
        if (tileCol < 0 || tileCol >= tileManager.MAX_MAP_COL ||
                tileRow < 0 || tileRow >= tileManager.MAX_MAP_ROW) {
            return true;
        }

        // Check if tile has collision
        return tileManager.tiles[tileManager.mapTileNum[tileRow][tileCol]].collision;
    }
}