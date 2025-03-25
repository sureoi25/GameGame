package org.example.entity.Ai;

import org.example.GamePanel;
import org.example.entity.Slime;
import static org.example.utils.constants.Directions.*;

import java.util.Random;

public class SlimeAI {
    private final Slime slime;
    private final GamePanel gp;
    private final Random random;
    private int actionLockCounter = 0;

    // AI parameters
    private static final int DETECTION_RANGE = 200;
    private static final int ATTACK_RANGE = 50;
    private static final int WANDER_CHANCE = 30; // 30% chance to move when wandering
    private static final int ATTACK_CHANCE = 5;   // 5% chance to attack randomly

    public SlimeAI(Slime slime, GamePanel gp) {
        this.slime = slime;
        this.gp = gp;
        this.random = new Random();
    }

    public void makeDecision() {
        // Only make decisions at certain intervals
        if (actionLockCounter > 0) {
            actionLockCounter--;
            return;
        }

        // Reset action lock
        actionLockCounter = 30; // About 0.5 seconds at 60 FPS

        // Calculate distance to player
        float playerX = gp.player.getX();
        float playerY = gp.player.getY();
        float slimeX = slime.getX();
        float slimeY = slime.getY();

        float distanceX = Math.abs(playerX - slimeX);
        float distanceY = Math.abs(playerY - slimeY);
        float distanceToPlayer = (float)Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distanceToPlayer <= DETECTION_RANGE) {
            // Player is nearby - chase or attack
            slime.setChasing(true);
            slime.setWandering(false);

            if (distanceToPlayer <= ATTACK_RANGE) {
                slime.attack();
            } else {
                chasePlayer(playerX, playerY, slimeX, slimeY);
            }
        } else {
            // Wander randomly
            slime.setChasing(false);
            slime.setWandering(true);
            wander();
        }
    }

    private void chasePlayer(float playerX, float playerY, float slimeX, float slimeY) {
        // Determine primary movement direction
        if (Math.abs(playerX - slimeX) > Math.abs(playerY - slimeY)) {
            // Prioritize horizontal movement
            slime.setDirection(playerX > slimeX ? RIGHT : LEFT);
        } else {
            // Prioritize vertical movement
            slime.setDirection(playerY > slimeY ? DOWN : UP);
        }
        slime.setMoving(true);
    }

    private void wander() {
        if (random.nextInt(100) < WANDER_CHANCE) {
            // Random movement
            int direction = random.nextInt(4); // 0-3 for directions
            slime.setDirection(direction);
            slime.setMoving(true);
        } else {
            // Stay idle
            slime.setMoving(false);
        }

        // Random attack attempt
        if (random.nextInt(100) < ATTACK_CHANCE) {
            slime.attack();
        }
    }
}