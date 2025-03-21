package org.example.objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Obj_mushroom1 extends SuperObject{
    public Obj_mushroom1(){
        name = "blue mushroom";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/mushroom1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
