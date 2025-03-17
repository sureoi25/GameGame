package org.example;

import javax.swing.*;

public class GameWindow {
    private JFrame jframe;

    public GameWindow(GamePanel gamePanel){
        jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(gamePanel);
        jframe.setLocation(0,0);
        jframe.setResizable(false);
        jframe.pack();
        jframe.setVisible(true);
        gamePanel.requestFocus();
    }
}
