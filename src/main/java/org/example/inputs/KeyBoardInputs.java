    package org.example.inputs;

    import org.example.GamePanel;

    import java.awt.event.KeyEvent;
    import java.awt.event.KeyListener;
    import static org.example.utils.constants.Directions.*;
    import static org.example.utils.constants.PlayerConstants.*;

    public class KeyBoardInputs implements KeyListener {
        private GamePanel gP;
        private boolean upPressed, downPressed, leftPressed, rightPressed;
        private boolean gamePaused = false;

        public KeyBoardInputs(GamePanel gP) {
            this.gP = gP;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Not used
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Handle inputs differently based on game state
            if (gP.player.isDead()) {
                handleDeadStateInput(e);
            } else if(gP.isGameCompleted()){
                handleCompletedStateInput(e);
            }else {
                handleActiveGameInput(e);
            }
        }

        private void handleActiveGameInput(KeyEvent e) {
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
                case KeyEvent.VK_P:
                    // Toggle pause
                    togglePause();
                    break;
            }
        }

        private void handleDeadStateInput(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_R:
                    // Restart the game when player is dead
                    gP.restartGame();
                    break;
            }
        }

        private void handleCompletedStateInput(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_R:
                    // Restart the game when player has won
                    gP.restartGame();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Only process key releases if not dead, game completed, and not paused
            if (!gP.player.isDead() && !gP.isGameCompleted() && !gamePaused) {
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

        private void togglePause() {
            gamePaused = !gamePaused;
            gP.setPaused(gamePaused);
        }
    }