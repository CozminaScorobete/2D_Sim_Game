package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {

	GamePanel gp;
	public Tile[] tile;
	public int mapTileNum[][];
	
	public TileManager(GamePanel gp) {
		
		this.gp = gp;
		
		tile = new Tile[10];
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		
		getTileImage();
		loadMap("/maps/map.txt");
	}
	
	public void getTileImage() {
		
		try {
			
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/gras.png"));
			
			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/dirt.png"));
			
			tile[4] = new Tile();
			tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water.png"));
			tile[4].collision = true;
			
			tile[4].collision = true;
			
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree.png"));
			tile[1].collision = true;
			
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/sand.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadMap(String filePath) {
		
		try {
			
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				String line = br.readLine();
			
				while(col < gp.maxWorldCol) {
					
					String numbers[] = line.split(" ");
					
					int num = Integer.parseInt(numbers[col]);
					
					mapTileNum[col][row] = num;
					col++;
				}
			
				if(col == gp.maxWorldCol) {
					
					col = 0;
					row ++;
				}
			}
			
			br.close();
		}catch(Exception e) {
		}
	}
	
	public void draw(Graphics2D g2) {
	    for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
	        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
	            int tileNum = mapTileNum[worldCol][worldRow];
	            int worldX = worldCol * gp.tileSize;
	            int worldY = worldRow * gp.tileSize;

	            // Determine screen position based on player's position
	            int screenX = worldX - gp.player.worldX + gp.player.screenX;
	            int screenY = worldY - gp.player.worldY + gp.player.screenY;

	            // Adjust to avoid scrolling at map edges
	            if (gp.player.worldX < gp.screenWidth / 2) {
	                screenX = worldX; // Stop scrolling horizontally at the left edge
	            }
	            if (gp.player.worldY < gp.screenHeight / 2) {
	                screenY = worldY; // Stop scrolling vertically at the top edge
	            }
	            if (gp.player.worldX > gp.worldWidth - gp.screenWidth / 2) {
	                screenX = worldX - (gp.worldWidth - gp.screenWidth); // Stop at the right edge
	            }
	            if (gp.player.worldY > gp.worldHeight - gp.screenHeight / 2) {
	                screenY = worldY - (gp.worldHeight - gp.screenHeight); // Stop at the bottom edge
	            }

	            // Check if the tile is water and the player has "Cascuta"
	            if (tileNum == 4 && gp.player.hasCascuta > 1) { // Assuming 4 represents water
	                tile[4].collision = false;
	                 // Decrease the player's "Cascuta"
	            }

	            // Render tile if it's visible
	            if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
	                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
	                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	            }
	        }
	    }
	}


	
	
}
