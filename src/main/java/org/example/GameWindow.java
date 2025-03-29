package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow {
    private JFrame jframe;
    private GamePanel gamePanel;
    private StartScreen startScreen;
    private Game game;

    public GameWindow(Game game) {
        this.game = game;
        jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);


        startScreen = new StartScreen();
        jframe.add(startScreen);
        jframe.pack();
        jframe.setVisible(true);
        startScreen.requestFocus();
    }

    public void startGame(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        jframe.getContentPane().removeAll();
        jframe.add(gamePanel);
        jframe.pack();
        gamePanel.requestFocus();
    }

    // Inner class for the start screen
    private class StartScreen extends JPanel implements KeyListener {
        private final int PANEL_WIDTH = 960;
        private final int PANEL_HEIGHT = 540;
        private Image backgroundImage;

        public StartScreen() {
            setPanelSize();
            setFocusable(true);
            addKeyListener(this);

            backgroundImage = new ImageIcon(getClass().getResource("/background/pixel background.jpg")).getImage();

        }

        private void setPanelSize() {
            Dimension size = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
            setPreferredSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.drawImage(backgroundImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);

            // Draw game title
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.setColor(Color.WHITE);
            String gameTitle = "THE CUMBACK";
            int titleWidth = g2d.getFontMetrics().stringWidth(gameTitle);
            g2d.drawString(gameTitle, (PANEL_WIDTH - titleWidth) / 2, PANEL_HEIGHT / 3);

            // Draw instruction
            g2d.setFont(new Font("Arial", Font.PLAIN, 30));
            String instruction = "Press enter to start";
            int instructionWidth = g2d.getFontMetrics().stringWidth(instruction);
            g2d.drawString(instruction, (PANEL_WIDTH - instructionWidth) / 2, PANEL_HEIGHT * 2 / 3);

            // Optional: Add more decorative elements or game info
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            String credits = "© 2025 Judd Tagalog Gang \n shoutout mom and dad";
            g2d.drawString(credits, 20, PANEL_HEIGHT - 20);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Not needed
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                game.startGame();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Not needed
        }
    }
}