package org.example.objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Obj_mushroom2 extends SuperObject{
    public Obj_mushroom2(){
        name = "red mushroom";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/mushroom2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
