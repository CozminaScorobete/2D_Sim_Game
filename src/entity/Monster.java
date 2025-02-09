package entity;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Monster extends Entity {

	public GamePanel gp;
    public BufferedImage image;
    public String direction = "right";
    public int speed = 2;
    public int life;
    public int maxLife = 30; // Reduced health to make monsters easier to kill
    public int spawnX;
    public int spawnY;
    private int moveDistance; // Distance to move before changing direction
    private int distanceMoved; // Track how far the monster has moved in the current direction
    private boolean damageCooldown; // Cooldown for damaging the player
    private long lastDamageTime; // Track the last damage time

    public Monster(GamePanel gp) {
        super(gp);
        this.gp = gp;

        // Initialize monster attributes
        this.speed = 2;
        
        this.life = maxLife; 
        this.solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);

        // Define the movement pattern variables
        this.moveDistance = gp.tileSize * 5; // Move 5 tiles before changing direction
        this.distanceMoved = 0;
        this.damageCooldown = false; // Cooldown initially off

        // Load the monster image
        loadMonsterImage();
    }

    public void loadMonsterImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/monsters/monster_normal.png")); // Replace with actual image path
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load monster image.");
        }
    }

    public void update() {
        // Check if the monster has moved the designated distance in the current direction
        if (distanceMoved >= moveDistance) {
            changeDirection();
            distanceMoved = 0; // Reset the distance moved for the next direction
        }

        // Move in the current direction
        move();

        // Increment the distance moved
        distanceMoved += speed;

        // Check collision with the player
       /// System.out.println("Monster Position: X=" + worldX + ", Y=" + worldY);
        //System.out.println("Monster Hitbox: " + solidArea);

        if (gp.cChecker.checkEntityCollision(this, gp.player)) {
            if (!damageCooldown) {
                gp.player.takeDamage(10);
                knockbackPlayer();
                triggerDamageCooldown();
            }
        }
    }

    private void move() {
        switch (direction) {
            case "up" -> worldY -= speed;
            case "down" -> worldY += speed;
            case "left" -> worldX -= speed;
            case "right" -> worldX += speed;
        }
    }

    private void changeDirection() {
        // Change the direction in a clockwise order
        switch (direction) {
            case "right" -> direction = "down";
            case "down" -> direction = "left";
            case "left" -> direction = "up";
            case "up" -> direction = "right";
        }
    }

    private void knockbackPlayer() {
        int dx = gp.player.worldX - this.worldX;
        int dy = gp.player.worldY - this.worldY;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) gp.player.worldX += 20; // Knock player right
            else gp.player.worldX -= 20; // Knock player left
        } else {
            if (dy > 0) gp.player.worldY += 20; // Knock player down
            else gp.player.worldY -= 20; // Knock player up
        }
    }

    private void triggerDamageCooldown() {
        damageCooldown = true;

        // Set a cooldown period (e.g., 1 second)
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1-second cooldown
            } catch (InterruptedException ignored) {}
            damageCooldown = false; // Reset cooldown
        }).start();
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only draw the monster if it's within the player's screen
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
            screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
    @Override
    public void takeDamage(int damage) {
        life -= damage;
        System.out.println("Monster took damage! Remaining HP: " + life);
        
        if (life <= 0) {
            life = 0;
            onDeath();
        }
    }

  
    @Override
    public void onDeath() {
        System.out.println("Monster has been defeated!");

        // **Ensures proper removal**
        gp.monsters.removeIf(m -> m == this);
    }

   
    // When a monster dies


}
