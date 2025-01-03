package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{

	GamePanel gp;
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;

	public int hasCascuta = 0;
	
	public Player(GamePanel gp, KeyHandler keyH) {

		this.gp = gp;
		this.keyH = keyH;
		
		screenX = gp.screenWidth/2 - (gp.tileSize/2);
		screenY = gp.screenHeight/2- (gp.tileSize/2);
		
		solidArea = new Rectangle();
		
		solidArea.x = 16;
		solidArea.y = 40;
		solidArea.width = 30; 
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		solidArea.height = 16;
		
		setDefaultValues();
		getPlayerImage();
	}

	public void setDefaultValues() {

		worldX = gp.tileSize * 10;
		worldY = gp.tileSize * 15;;
		speed = 4; 
		direction= "down";
	}

	public void getPlayerImage() {

		try {

			up1=ImageIO.read(getClass().getResource("/player/girl_up_1.png"));
			up2=ImageIO.read(getClass().getResource("/player/girl_up_2.png"));
			down1=ImageIO.read(getClass().getResource("/player/girl_down_1.png"));
			down2=ImageIO.read(getClass().getResource("/player/girl_down_2.png"));
			left1=ImageIO.read(getClass().getResource("/player/girl_left_1.png"));
			left2=ImageIO.read(getClass().getResource("/player/girl_left_2.png"));
			right1=ImageIO.read(getClass().getResource("/player/girl_right_1.png"));
			right2=ImageIO.read(getClass().getResource("/player/girl_right_2.png"));


		}catch(IOException e) {
			e.printStackTrace();
		}

	}

	public void update() {

	   
		
		if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
	        if (keyH.upPressed) {
	            direction = "up";
	           
	        } else if (keyH.downPressed) {
	            direction = "down";
	            
	        } else if (keyH.leftPressed) {
	            direction = "left";
	            
	        } else if (keyH.rightPressed) {
	            direction = "right";
	           
	        }
	        
	        
	        //Check Tile Collision
	        collisionOn = false;
	        gp.cChecker.checkTile(this);
	        
	        //check obj collision
	        int objIndex = gp.cChecker.checkObject(this,true);
	        pickUpObject(objIndex);
	        //If collision is False, player can move
	        
	        if(collisionOn == false) {
	        	switch(direction) {
	        	
	        	case "up":
	        		 if (worldY > 0) worldY -= speed; // Stop at the top edge
	        		 break;
	        	case "down":
	        		if (worldY < gp.worldHeight - gp.tileSize) worldY += speed; // Stop at the bottom edge
	        		break;
	        	case "left" :
	        		if (worldX > 0) worldX -= speed; // Stop at the left edge
	        		break;
	        	case "right":
	        		if (worldX < gp.worldWidth - gp.tileSize) worldX += speed; // Stop at the right edge
	        		break;
	        	}
	        }
	        
	        spriteCounter++;
	        if (spriteCounter > 12) {
	            spriteNum = (spriteNum == 1) ? 2 : 1;
	            spriteCounter = 0;
	        }
	    }
	}
	
	public void pickUpObject(int i) {
		
		if(i != 999)
		{
			String objectName = gp.obj[i].name;
			System.out.println(objectName);
			switch(objectName) {
			case "Cascuta":
				hasCascuta++;
				gp.obj[i] = null;
				System.out.println(hasCascuta);
				break;
			
			}
		}
		
		
	}


	public void draw(Graphics2D g2) {
	    BufferedImage image = null;

	    switch (direction) {
	        case "up":
	            image = (spriteNum == 1) ? up1 : up2;
	            break;
	        case "down":
	            image = (spriteNum == 1) ? down1 : down2;
	            break;
	        case "left":
	            image = (spriteNum == 1) ? left1 : left2;
	            break;
	        case "right":
	            image = (spriteNum == 1) ? right1 : right2;
	            break;
	    }

	    int screenX = (worldX < gp.screenWidth / 2) ? worldX : gp.screenWidth / 2;
	    int screenY = (worldY < gp.screenHeight / 2) ? worldY : gp.screenHeight / 2;

	    if (worldX > gp.worldWidth - gp.screenWidth / 2) {
	        screenX = worldX - (gp.worldWidth - gp.screenWidth);
	    }
	    if (worldY > gp.worldHeight - gp.screenHeight / 2) {
	        screenY = worldY - (gp.worldHeight - gp.screenHeight);
	    }

	    g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	}

}
