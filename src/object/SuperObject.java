package object;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import main.GamePanel;

public class SuperObject {
    
    public BufferedImage image;
    public String name;
    public boolean collision = false; // Used for collision detection
    public int worldX, worldY; // Fixed world position of the object
    
    // Draw the object based on its world position and the player's location
    public void draw(Graphics2D g2, GamePanel gp) {
        // Calculate the object's screen position relative to the player's position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only render the object if it’s within the visible area of the screen
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
            // Debugging: Log when the object is outside the visible screen
            System.out.println("Object out of visible range: (" + worldX + ", " + worldY + ")");
        }
    }



}
