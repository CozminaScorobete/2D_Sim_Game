package main;

import javax.swing.JFrame;
import sockets.GameClient; // Import GameClient if it's in a different package

public class Main {
    public static void main(String[] args) {
        // Start the GameClient and connect to the server
    	GameClient client = new GameClient("localhost", 12325);
    	GamePanel gamePanel = new GamePanel(client);
    	client.setGamePanel(gamePanel);
        // Initialize the game window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("FarmSimulator");

        // Pass the GameClient to the GamePanel
       
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }
}
