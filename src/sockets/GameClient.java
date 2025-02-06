package sockets;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import main.GamePanel;

public class GameClient {
    public Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;
    private final Map<String, int[]> otherPlayers = new ConcurrentHashMap<>();

    public GameClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new ServerListener()).start();
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            socket = null;
            out = null;
        }
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void sendMove(int x, int y) {
        if (out != null) {
            out.println("MOVE," + x + "," + y);
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    parseGameState(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseGameState(String gameState) {
        otherPlayers.clear();
        String[] players = gameState.split(";");
        for (String playerData : players) {
            String[] data = playerData.split(",");
            if (data.length == 3) {
                String playerId = data[0];
                int x = Integer.parseInt(data[1]);
                int y = Integer.parseInt(data[2]);
                otherPlayers.put(playerId, new int[]{x, y});
            }
        }

        if (gamePanel != null) {
            gamePanel.setOtherPlayers(otherPlayers);
        }
    }
}
