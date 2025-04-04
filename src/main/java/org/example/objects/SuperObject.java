package org.example.objects;

import org.example.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldX, worldY;

    //for hitbox
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);


    public void draw(Graphics2D g2, GamePanel gp) {
        // Calculate screen position relative to camera
        int screenX = worldX - gp.getCameraX();
        int screenY = worldY - gp.getCameraY();

        // Only draw if the object is visible on screen (optimization)
        if (worldX + 48 > gp.getCameraX() &&
                worldX - 48 < gp.getCameraX() + gp.PANEL_WIDTH &&
                worldY + 48 > gp.getCameraY() &&
                worldY - 48 < gp.getCameraY() + gp.PANEL_HEIGHT) {

            g2.drawImage(image, screenX, screenY, 48, 48, null);
        }
    }
}