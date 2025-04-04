package org.example.objects;

import org.example.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Factory class responsible for creating game objects
 * Centralizes object creation logic in one place
 */
public class ObjectFactory {
    private GamePanel gp;

    public ObjectFactory(GamePanel gp) {
        this.gp = gp;
    }


    public SuperObject createObject(String name, int worldX, int worldY) {
        SuperObject obj = null;

        switch (name) {
            case "Key":
                obj = new Obj_key();
                break;
            case "chest":
                obj = new Obj_chest();
                break;
            case "chicken":
                obj = new Obj_chicken();
                break;
            case "blue mushroom":
                obj = new Obj_mushroom1();
                break;
            case "red mushroom":
                obj = new Obj_mushroom2();
                break;
            case "pork":
                obj = new Obj_pork();
                break;
        }

        if (obj != null) {
            obj.worldX = worldX;
            obj.worldY = worldY;
        }

        return obj;
    }
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
    public class Obj_key extends SuperObject{
        public Obj_key() {
            name = "Key";
            try{
                image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
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
}