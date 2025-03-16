package org.example.tile;

import org.example.GamePanel;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GamePanel gp;
    public Tile[] tiles;
    public int[][] mapTileNum;

    // Map dimensions (in tiles)
    public final int MAX_MAP_COL;
    public final int MAX_MAP_ROW;
    public final int TILE_SIZE;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        TILE_SIZE = 48;
        MAX_MAP_COL = gp.PANEL_WIDTH / TILE_SIZE;
        MAX_MAP_ROW = gp.PANEL_HEIGHT / TILE_SIZE;

        tiles = new Tile[30];
        mapTileNum = new int[MAX_MAP_ROW][MAX_MAP_COL];

        loadTiles();
        loadMap("/maps/map01.txt");
    }

    public void loadTiles() {
        try {
            // Initialize each tile with its image and collision property
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/earth.png"));
            tiles[0].collision = false;

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass00.png"));
            tiles[1].collision = false;

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree.png"));
            tiles[2].collision = true;

            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree.png"));
            tiles[3].collision = true;

            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water00.png"));
            tiles[4].collision = true;

            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water01.png"));
            tiles[5].collision = true;

            tiles[6] = new Tile();
            tiles[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water02.png"));
            tiles[6].collision = true;

            tiles[7] = new Tile();
            tiles[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water03.png"));
            tiles[7].collision = true;

            tiles[8] = new Tile();
            tiles[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water04.png"));
            tiles[8].collision = true;

            tiles[9] = new Tile();
            tiles[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water05.png"));
            tiles[9].collision = true;

            tiles[10] = new Tile();
            tiles[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water06.png"));
            tiles[10].collision = true;

            tiles[11] = new Tile();
            tiles[11].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water07.png"));
            tiles[11].collision = true;

            tiles[12] = new Tile();
            tiles[12].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water08.png"));
            tiles[12].collision = true;

            tiles[13] = new Tile();
            tiles[13].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water09.png"));
            tiles[13].collision = true;

            tiles[14] = new Tile();
            tiles[14].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water10.png"));
            tiles[14].collision = true;

            tiles[15] = new Tile();
            tiles[15].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water11.png"));
            tiles[15].collision = true;

            tiles[16] = new Tile();
            tiles[16].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water12.png"));
            tiles[16].collision = true;

            tiles[17] = new Tile();
            tiles[17].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water13.png"));
            tiles[17].collision = true;




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (row < MAX_MAP_ROW) {
                String line = br.readLine();

                while (col < MAX_MAP_COL) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[row][col] = num;
                    col++;
                }

                if (col == MAX_MAP_COL) {
                    col = 0;
                    row++;
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        int col = 0;
        int row = 0;

        while (col < MAX_MAP_COL && row < MAX_MAP_ROW) {
            int tileNum = mapTileNum[row][col];

            int x = col * TILE_SIZE;
            int y = row * TILE_SIZE;

            g.drawImage(tiles[tileNum].image, x, y, TILE_SIZE, TILE_SIZE, null);

            col++;
            if(col == MAX_MAP_COL) {
                col = 0;
                row++;
            }
        }
    }

    public boolean hasCollision(float worldX, float worldY) {
        int col = (int)(worldX / TILE_SIZE);
        int row = (int)(worldY / TILE_SIZE);

        if (col < 0 || col >= MAX_MAP_COL || row < 0 || row >= MAX_MAP_ROW) {
            return true; //out of bounds
        }

        return tiles[mapTileNum[row][col]].collision;
    }
}