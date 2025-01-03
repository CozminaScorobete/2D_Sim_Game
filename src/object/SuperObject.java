package object;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;

public class SuperObject {
    
    public BufferedImage image;
    public String name;
    public boolean collision = false; // Used for collision detection
    public int worldX, worldY; // Fixed world position of the object
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    
    // Draw the object based on its world position and the player's location
    public void draw(Graphics2D g2, GamePanel gp) {
        // Calculate screen position relative to the player's position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Adjust for camera bounds at map edges
        if (gp.player.worldX < gp.screenWidth / 2) {
            // Player near the left edge
            screenX = worldX;
        } else if (gp.player.worldX > gp.worldWidth - gp.screenWidth / 2) {
            // Player near the right edge
            screenX = worldX - (gp.worldWidth - gp.screenWidth);
        }

        if (gp.player.worldY < gp.screenHeight / 2) {
            // Player near the top edge
            screenY = worldY;
        } else if (gp.player.worldY > gp.worldHeight - gp.screenHeight / 2) {
            // Player near the bottom edge
            screenY = worldY - (gp.worldHeight - gp.screenHeight);
        }

        // Render the object only if it is within the visible screen bounds
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
            screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
           
          
        }
    }






}
