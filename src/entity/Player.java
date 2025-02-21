package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import sockets.GameClient;

public class Player extends Entity {

	GamePanel gp;
    KeyHandler keyH;
    GameClient client;
    public final int screenX;
    public final int screenY;
    private boolean damageCooldown;
    private int knockbackDuration = 0; // Frames for knockback effect
    private int knockbackSpeed = 2;   // Speed of knockback movement
    private String knockbackDirection; // Direction of the knockback
    private boolean attacking = false; // Is the player attacking?
    private int attackCooldown = 0; // Time between attacks
    private final int attackDelay = 30; // Delay between attacks (frames)
    private Rectangle attackHitbox; // Attack area
    private int attackAnimationDuration = 10; // Frames to show attack animation
    private int attackAnimationTimer = 0; // Timer for attack animation

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;
        this.gp = gp;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(16, 40, 30, 16);
        attackHitbox = new Rectangle(0, 0, 0, 0); // Initialize attackHitbox
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 10;
        worldY = gp.tileSize * 15;
        speed = 4;
        direction = "down";
        maxHealth = 100;  // Set maximum health
        health = maxHealth;  // Start with full health
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(getClass().getResource("/player/girl_up_1.png"));
            up2 = ImageIO.read(getClass().getResource("/player/girl_up_2.png"));
            down1 = ImageIO.read(getClass().getResource("/player/girl_down_1.png"));
            down2 = ImageIO.read(getClass().getResource("/player/girl_down_2.png"));
            left1 = ImageIO.read(getClass().getResource("/player/girl_left_1.png"));
            left2 = ImageIO.read(getClass().getResource("/player/girl_left_2.png"));
            right1 = ImageIO.read(getClass().getResource("/player/girl_right_1.png"));
            right2 = ImageIO.read(getClass().getResource("/player/girl_right_2.png"));

            // Attack sprites (Add your own attack images)
            attackUp = ImageIO.read(getClass().getResource("/player/atack_up_1.png"));
            attackDown = ImageIO.read(getClass().getResource("/player/atack_down_1.png"));
            attackLeft = ImageIO.read(getClass().getResource("/player/atack_left_1.png"));
            attackRight = ImageIO.read(getClass().getResource("/player/atack_right_1.png"));
            attackUp2 = ImageIO.read(getClass().getResource("/player/atack_up_2.png"));
            attackDown2 = ImageIO.read(getClass().getResource("/player/atack_down_2.png"));
            attackLeft2 = ImageIO.read(getClass().getResource("/player/atack_left_2.png"));
            attackRight2 = ImageIO.read(getClass().getResource("/player/atack_right_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void update() {
        if (knockbackDuration > 0) {
            applyKnockback();
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (attacking) {
            attackAnimationTimer--;
            if (attackAnimationTimer <= 0) {
                attacking = false; // End attack animation
                attackHitbox.setBounds(0, 0, 0, 0); // Clear attack hitbox
            }
        } else {
            if (keyH.attackPressed && attackCooldown == 0) {
                attacking = true;
                attackAnimationTimer = attackAnimationDuration; // Set attack animation duration
                attackCooldown = attackDelay;
                attack();
            }

            if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                if (keyH.upPressed) direction = "up";
                else if (keyH.downPressed) direction = "down";
                else if (keyH.leftPressed) direction = "left";
                else if (keyH.rightPressed) direction = "right";

                collisionOn = false;
                gp.cChecker.checkTile(this);

                int objIndex = gp.cChecker.checkObject(this, true);
                pickUpObject(objIndex);

                for (Monster monster : gp.monsters) {
                    if (gp.cChecker.checkEntityCollision(this, monster)) {
                        if (!damageCooldown) {
                            takeDamage(10, monster);
                            knockback(monster);
                            triggerDamageCooldown();
                        }
                    }
                }

                if (!collisionOn) {
                    switch (direction) {
                        case "up" -> worldY -= speed;
                        case "down" -> worldY += speed;
                        case "left" -> worldX -= speed;
                        case "right" -> worldX += speed;
                    }
                }

                spriteCounter++;
                if (spriteCounter > 12) {
                    spriteNum = (spriteNum == 1) ? 2 : 1;
                    spriteCounter = 0;
                }
            }
        }
    }

    public void attack() {
        System.out.println("Player Attacked!");

        int attackX = worldX;
        int attackY = worldY;
        int attackSize = gp.tileSize; // Default attack size

        switch (direction) {
            case "up":
                attackY -= attackSize;
                break;
            case "down":
                attackY += attackSize;
                break;
            case "left":
                attackX -= attackSize;
                break;
            case "right":
                attackX += attackSize;
                break;
        }

        attackHitbox.setBounds(attackX, attackY, attackSize, attackSize);
        System.out.println("Attack hitbox: " + attackHitbox);

        boolean hit = false;
        for (Monster monster : gp.monsters) {
            if (monster != null) {
                Rectangle monsterHitbox = new Rectangle(monster.worldX, monster.worldY, gp.tileSize, gp.tileSize);
                System.out.println("Checking collision with Monster at " + monsterHitbox);

                if (attackHitbox.intersects(monsterHitbox)) {
                    System.out.println("Monster HIT! HP before: " + monster.life);
                    monster.takeDamage(10);
                    System.out.println("Monster HP after: " + monster.life);

                    if (monster.life <= 0) {
                        System.out.println("Monster KILLED!");
                        monster.onDeath();
                        gp.monsters.remove(monster);
                    }
                    hit = true;
                    break; // Stop checking after hitting one monster
                }
            }
        }

        if (!hit) {
            System.out.println("No monster was hit!");
        }

        attacking = true;
        attackAnimationTimer = attackAnimationDuration;
    }



    public void respawnPlayer() {
        System.out.println("🔄 Respawning player...");

        // Reset player position & health
        worldX = gp.tileSize * 10;
        worldY = gp.tileSize * 15;
        health = maxHealth;

        // Reset game over state
        gp.isGameOver = false;
    }

    public void takeDamage(int damage, Monster attacker) {
        if (damageCooldown) return; // Prevent spam damage

        health -= damage;
        if (health <= 0) {
            health = 0;
            gp.isGameOver = true;
            System.out.println("Player is dead!");
            // Handle player death
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Wait 3 seconds before respawning
                } catch (InterruptedException ignored) {}
                respawnPlayer(); // Respawn after 3 seconds
            }).start();
        } else {
            System.out.println("Player took damage! Health: " + health);
            knockback(attacker);
            triggerDamageCooldown();
        }
    }

    public void knockback(Monster monster) {
        int knockbackAmount = gp.tileSize / 4; // Smooth movement
        if (monster.worldX > this.worldX) knockbackDirection = "left";
        else if (monster.worldX < this.worldX) knockbackDirection = "right";

        if (monster.worldY > this.worldY) knockbackDirection = "up";
        else if (monster.worldY < this.worldY) knockbackDirection = "down";

        knockbackDuration = 10; // Apply knockback over 10 frames
    }

    private void applyKnockback() {
        int knockbackSpeed = 2; // Move 2 pixels per frame for smooth effect
        switch (knockbackDirection) {
            case "up" -> worldY -= knockbackSpeed;
            case "down" -> worldY += knockbackSpeed;
            case "left" -> worldX -= knockbackSpeed;
            case "right" -> worldX += knockbackSpeed;
        }
        knockbackDuration--;
    }

    private void triggerDamageCooldown() {
        damageCooldown = true;
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
            damageCooldown = false;
        }).start();
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (attacking) {
            switch (direction) {
                case "up" -> image = (spriteNum == 1) ? attackUp: attackUp2;
                case "down" -> image =(spriteNum == 1)? attackDown: attackDown2;
                case "left" -> image = (spriteNum == 1)? attackLeft:attackLeft2;
                case "right" -> image =(spriteNum == 1)? attackRight:attackRight2;
            }

            // Visualize the attack hitbox (optional)
            g2.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
            g2.fillRect(
                attackHitbox.x - gp.player.worldX + gp.player.screenX,
                attackHitbox.y - gp.player.worldY + gp.player.screenY,
                attackHitbox.width,
                attackHitbox.height
            );
        } else {
            switch (direction) {
                case "up" -> image = (spriteNum == 1) ? up1 : up2;
                case "down" -> image = (spriteNum == 1) ? down1 : down2;
                case "left" -> image = (spriteNum == 1) ? left1 : left2;
                case "right" -> image = (spriteNum == 1) ? right1 : right2;
            }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    public void pickUpObject(int index) {
        if (index != 999 && gp.obj[index] != null) {
            String objectName = gp.obj[index].name;

            switch (objectName) {
                case "Cascuta": // Example item
                    System.out.println("Picked up: Cascuta");
                    gp.obj[index] = null; // Remove object from the game
                    break;
                case "Heart": // Example health item
                    restoreHealth(60); // Restore health by 10
                    gp.obj[index] = null;
                    break;
            }
        }
    }
    public void restoreHealth(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth; // Ensure health does not exceed the maximum
        }
        System.out.println("Health restored! Current health: " + health);
    }

}
