package org.example.objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Obj_chicken extends SuperObject{
    public Obj_chicken(){
        name = "chicken";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chicken.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }
}
