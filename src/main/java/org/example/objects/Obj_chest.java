package org.example.objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Obj_chest extends SuperObject{
    public Obj_chest(){
        name = "chest";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest/chest1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
