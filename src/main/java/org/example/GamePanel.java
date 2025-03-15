package org.example;

import org.example.inputs.KeyBoardInputs;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    public GamePanel(){
        addKeyListener(new KeyBoardInputs());
        addMouseListener(null);

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);


    }
}
