package org.example;

import org.example.entity.Player;
import org.example.entity.SlimeManager;
import org.example.GamePanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class HUD {
    private GamePanel gp;
    private Player player;
    private SlimeManager slimeManager;
    private Font statsFont;
    private Font titleFont;
    private BufferedImage heartFull, heartEmpty;
    private BufferedImage swordIcon;
    private BufferedImage bootsIcon;
    private BufferedImage slimeIcon;

    // HUD positioning and dimensions
    private final int MARGIN = 20;
    private final int BAR_HEIGHT = 20;
    private final int BAR_WIDTH = 150;
    private final int ICON_SIZE = 32;

    public HUD(GamePanel gp, Player player, SlimeManager slimeManager) {
        this.gp = gp;
        this.player = player;
        this.slimeManager = slimeManager;

        // Initialize fonts
        statsFont = new Font("Arial", Font.PLAIN, 16);
        titleFont = new Font("Arial", Font.BOLD, 18);

        // Load HUD images
        loadImages();
    }

    private void loadImages() {
        try {
            // Load heart images for health
            InputStream is = getClass().getResourceAsStream("/HUD/heart_full.png");
            heartFull = ImageIO.read(is);
            is.close();

            is = getClass().getResourceAsStream("/HUD/heart_empty.png");
            heartEmpty = ImageIO.read(is);
            is.close();

            // Load sword icon for attack
            is = getClass().getResourceAsStream("/HUD/sword_icon.png");
            swordIcon = ImageIO.read(is);
            is.close();

            // Load boots icon for speed
            is = getClass().getResourceAsStream("/HUD/boots_icon.png");
            bootsIcon = ImageIO.read(is);
            is.close();

            // Load slime icon for enemy tracking
            is = getClass().getResourceAsStream("/HUD/slime_icon.png");
            slimeIcon = ImageIO.read(is);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading HUD images. Using fallback rendering.");
        }
    }

    public void draw(Graphics2D g2) {
        // Set font for stats
        g2.setFont(statsFont);

        // Draw HUD title
        g2.setFont(titleFont);
        g2.drawString("PLAYER STATS", MARGIN + 10, MARGIN + 25);
        g2.setFont(statsFont);

        // Draw HP bar
        int currentY = MARGIN + 50;
        drawStatBar(g2, "HP", player.getCurrentHp(), player.getMaxHp(), currentY, Color.RED, heartFull);

        // Draw Attack stat
        currentY += BAR_HEIGHT + 10;
        drawStatBar(g2, "ATK", player.getAttackDamage(), 100, currentY, Color.ORANGE, swordIcon);

        // Draw Speed stat
        currentY += BAR_HEIGHT + 10;
        drawStatBar(g2, "SPD", (int)(player.getSpeed() * 10), 50, currentY, Color.GREEN, bootsIcon);

        // Draw Wave information section
        currentY += BAR_HEIGHT + 30;
        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        g2.drawString("WAVE INFO", MARGIN + 10, currentY);
        g2.setFont(statsFont);

        // Draw current wave
        currentY += 25;
        g2.drawString("WAVE: " + slimeManager.getCurrentWave() + "/3", MARGIN + 10, currentY);

        // Draw slime kill count
        currentY += 25;
        int killedSlimes = slimeManager.getKilledSlimesInWave();
        int totalSlimes = slimeManager.getTotalSlimesInWave();
        g2.drawString("SLIMES: " + killedSlimes + "/" + totalSlimes, MARGIN + 10, currentY);

        // Draw slime progress bar
        currentY += 25;
        drawSlimeProgress(g2, killedSlimes, totalSlimes, currentY);

        // Draw remaining slimes count
        currentY += 45;
        g2.drawString("REMAINING: " + slimeManager.getSlimes().size(), MARGIN + 10, currentY);
    }

    private void drawStatBar(Graphics2D g2, String label, int current, int max, int y, Color barColor, BufferedImage icon) {
        // Calculate bar fill percentage
        float percentage = (float)current / max;
        int filledWidth = (int)(BAR_WIDTH * percentage);

        // Draw icon if available, otherwise draw text label
        if (icon != null) {
            g2.drawImage(icon, MARGIN + 10, y - 5, ICON_SIZE, ICON_SIZE, null);
        } else {
            g2.drawString(label, MARGIN + 10, y + 15);
        }

        // Draw bar background
        g2.setColor(Color.GRAY);
        g2.fillRect(MARGIN + 50, y, BAR_WIDTH, BAR_HEIGHT);

        // Draw filled bar
        g2.setColor(barColor);
        g2.fillRect(MARGIN + 50, y, filledWidth, BAR_HEIGHT);

        // Draw bar border
        g2.setColor(Color.WHITE);
        g2.drawRect(MARGIN + 50, y, BAR_WIDTH, BAR_HEIGHT);

        // Draw value text
        g2.drawString(current + "/" + max, MARGIN + BAR_WIDTH + 60, y + 15);
    }

    private void drawSlimeProgress(Graphics2D g2, int killed, int total, int y) {
        // Calculate progress percentage
        float percentage = total > 0 ? (float)killed / total : 0;
        int filledWidth = (int)(BAR_WIDTH * percentage);

        // Draw slime icon if available
        if (slimeIcon != null) {
            g2.drawImage(slimeIcon, MARGIN + 10, y - 5, ICON_SIZE, ICON_SIZE, null);
        } else {
            g2.drawString("PROG", MARGIN + 10, y + 15);
        }

        // Draw bar background
        g2.setColor(Color.GRAY);
        g2.fillRect(MARGIN + 50, y, BAR_WIDTH, BAR_HEIGHT);

        // Draw filled progress
        g2.setColor(new Color(75, 180, 75)); // Green for progress
        g2.fillRect(MARGIN + 50, y, filledWidth, BAR_HEIGHT);

        // Draw bar border
        g2.setColor(Color.WHITE);
        g2.drawRect(MARGIN + 50, y, BAR_WIDTH, BAR_HEIGHT);

        // Draw progress text
        g2.drawString(killed + "/" + total, MARGIN + BAR_WIDTH + 60, y + 15);
    }
}