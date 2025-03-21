package org.example.objects;

import org.example.GamePanel;

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

    public void populateInitialObjects(SuperObject[] objects) {
        objects[0] = createObject("Key", 13 * 48, 13 * 48);
        objects[1] = createObject("Key", 27 * 48, 34 * 48);
        objects[2] = createObject("chest", 33 * 48, 25 * 48);
        objects[3] = createObject("chest", 22 * 48, 8 * 48);
    }
}