package entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class Entity {
	
	protected GamePanel gp;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }
    public int health;
    public int maxHealth;
	public int worldX, worldY;
	public int speed;
	
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2,attackUp,attackDown,attackLeft,attackRight,attackUp2,attackDown2,attackLeft2,attackRight2;
	public String direction;
	
	public int spriteCounter = 0;
	public int spriteNum = 1;
	
	public Rectangle solidArea;
	public int solidAreaDefaultX, solidAreaDefaultY;
	
	
	public boolean collisionOn = false;
	 public void takeDamage(int damage) {
	        health -= damage;
	        if (health <= 0) {
	            health = 0;
	            System.out.println("Entity is dead!");
	            onDeath(); // Call the death handler
	        } else {
	            System.out.println("Entity took damage! Health: " + health);
	        }
	    }

	    // Placeholder for death handling, can be overridden in subclasses
	    public void onDeath() {
	        System.out.println("Entity died.");
	    }

}
