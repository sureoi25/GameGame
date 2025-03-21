package org.example.inputs;

import org.example.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static org.example.utils.constants.Directions.*;
import static org.example.utils.constants.PlayerConstants.*;

public class KeyBoardInputs implements KeyListener {
    private GamePanel gP;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public KeyBoardInputs(GamePanel gP) {
        this.gP = gP;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                upPressed = true;
                updateMovementState();
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                updateMovementState();
                break;
            case KeyEvent.VK_S:
                downPressed = true;
                updateMovementState();
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                updateMovementState();
                break;
            case KeyEvent.VK_SPACE:
                gP.attack();
                break;
            case KeyEvent.VK_E:
                // Trigger interaction with nearby objects
                gP.interact();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                upPressed = false;
                updateMovementState();
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                updateMovementState();
                break;
            case KeyEvent.VK_S:
                downPressed = false;
                updateMovementState();
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                updateMovementState();
                break;
        }
    }

    private void updateMovementState() {
        // If any direction key is still pressed, keep moving
        if (upPressed || downPressed || leftPressed || rightPressed) {
            gP.setMoving(true);

            // Determine the direction priority
            // Horizontal movement takes priority over vertical if both are pressed
            if (leftPressed)
                gP.setDirection(LEFT);
            else if (rightPressed)
                gP.setDirection(RIGHT);
            else if (upPressed)
                gP.setDirection(UP);
            else if (downPressed)
                gP.setDirection(DOWN);
        } else {
            // Only stop moving when all keys are released
            gP.setMoving(false);
        }
    }
}