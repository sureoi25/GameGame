package org.example.objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Obj_pork extends SuperObject{
    public Obj_pork(){
        name = "pork";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/pork.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
