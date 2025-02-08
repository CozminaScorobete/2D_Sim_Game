package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed;
	private GamePanel gp;  // Reference to the GamePanel

    // Constructor
    public KeyHandler(GamePanel gp) {
        this.gp = gp;  // Assign GamePanel reference
    }
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public boolean attackPressed = false;

	@Override
	public void keyPressed(KeyEvent e) {
	    int code = e.getKeyCode();

	    if (code == KeyEvent.VK_W) upPressed = true;
	    if (code == KeyEvent.VK_S) downPressed = true;
	    if (code == KeyEvent.VK_A) leftPressed = true;
	    if (code == KeyEvent.VK_D) rightPressed = true;
	    if (code == KeyEvent.VK_SPACE) attackPressed = true;
	    
	    // 🛠 NEW: Lose health when pressing "H"
	    if (code == KeyEvent.VK_H) {
	        gp.player.takeDamage(1);
	        System.out.println("Player took damage! Health: " + gp.player.health);
	    }
	}


	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		if (code == KeyEvent.VK_SPACE) attackPressed = false;
	}

}
