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
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Make sure it doesn't scroll incorrectly
        if (gp.player.worldX < gp.screenWidth / 2) {
            screenX = worldX;
        }
        if (gp.player.worldY < gp.screenHeight / 2) {
            screenY = worldY;
        }
        if (gp.player.worldX > gp.worldWidth - gp.screenWidth / 2) {
            screenX = worldX - (gp.worldWidth - gp.screenWidth);
        }
        if (gp.player.worldY > gp.worldHeight - gp.screenHeight / 2) {
            screenY = worldY - (gp.worldHeight - gp.screenHeight);
        }

        // 🔍 🛠 FIX: Check if the object is within screen bounds
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
            screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

    }







