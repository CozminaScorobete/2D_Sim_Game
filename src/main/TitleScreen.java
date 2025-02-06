package main;

import javax.swing.*;
import java.awt.*;
import sockets.GameClient;
import sockets.GameServer;

public class TitleScreen extends JFrame {

    public TitleScreen() {
        setTitle("FarmSimulator - Title Screen");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        JButton startButton = new JButton("Start Game (Single Player)");
        JButton hostButton = new JButton("Host Game");
        JButton joinButton = new JButton("Join Game");

        startButton.addActionListener(e -> startGame(null));
        hostButton.addActionListener(e -> {
            startServer();
            startGame("localhost");
        });
        joinButton.addActionListener(e -> {
            String serverIP = JOptionPane.showInputDialog("Enter server IP:");
            if (serverIP != null && !serverIP.isEmpty()) {
                startGame(serverIP);
            }
        });

        add(startButton);
        add(hostButton);
        add(joinButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(String serverIP) {
        dispose();

        GameClient client = (serverIP != null) ? new GameClient(serverIP, 12325) : null;
        if (client != null && client.socket == null) {
            JOptionPane.showMessageDialog(null, "Failed to connect to server!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GamePanel gamePanel = new GamePanel(client);
        if (client != null) {
            client.setGamePanel(gamePanel);
        }

        JFrame gameWindow = new JFrame("FarmSimulator");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.add(gamePanel);
        gameWindow.pack();
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }

    private void startServer() {
        new Thread(() -> {
            System.out.println("Starting server...");
            GameServer server = new GameServer(12325);
            server.start();
        }).start();
    }
}
