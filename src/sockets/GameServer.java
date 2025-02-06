package sockets;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Map<String, PlayerState> players = new HashMap<>();
    private List<ClientHandler> clients = new ArrayList<>();

    public GameServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void updatePlayer(String playerId, PlayerState state) {
        players.put(playerId, state);
        broadcastGameState();
    }

    public synchronized void removePlayer(String playerId) {
        players.remove(playerId);
        broadcastGameState();
    }

    private synchronized void broadcastGameState() {
        StringBuilder gameState = new StringBuilder();
        for (Map.Entry<String, PlayerState> entry : players.entrySet()) {
            PlayerState state = entry.getValue();
            gameState.append(entry.getKey()).append(",")
                     .append(state.x).append(",")
                     .append(state.y).append(";");
        }
        String gameStateMessage = gameState.toString();
        for (ClientHandler client : clients) {
            client.sendMessage(gameStateMessage);
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer(12325);
        server.start();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private GameServer server;
        private PrintWriter out;
        private BufferedReader in;
        private String playerId;

        public ClientHandler(Socket socket, GameServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                playerId = UUID.randomUUID().toString();
                server.updatePlayer(playerId, new PlayerState(100, 100)); // Default position

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(",");
                    if (parts[0].equals("MOVE")) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        server.updatePlayer(playerId, new PlayerState(x, y));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server.removePlayer(playerId);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    private static class PlayerState {
        int x, y;

        public PlayerState(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
