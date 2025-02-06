package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import javax.swing.JPanel;

import entity.Player;
import object.SuperObject;
import sockets.GameClient;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;  // 48*48
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // World Settings
    public final int maxWorldCol = 30;
    public final int maxWorldRow = 30;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    public CollisionChecker cChecker = new CollisionChecker(this);

    public AssetSetter aSetter = new AssetSetter(this);

    public Player player = new Player(this, keyH);

    public SuperObject obj[] = new SuperObject[10];

    // Multiplayer variables
    private Map<String, int[]> otherPlayers = new ConcurrentHashMap<>(); // Thread-safe map for other players
    private GameClient client; // Client reference for networking

    public GamePanel(GameClient client) {
        this.client = client;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        aSetter.setObject();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long timer = 0;
        int drawCount = 0;
        long currentTime;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if (timer >= 1000000000) {
                System.out.println("FPS:" + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        player.update();

        // Send player's position to the server
        if (client != null) {
            client.sendMove(player.worldX, player.worldY);
        }
    }

    public void setOtherPlayers(Map<String, int[]> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Draw tiles
        tileM.draw(g2);

        // Draw other players
        for (int[] position : otherPlayers.values()) {
            int worldX = position[0];
            int worldY = position[1];

            // Calculate screen positions relative to the local player's position
            int screenX = worldX - player.worldX + player.screenX;
            int screenY = worldY - player.worldY + player.screenY;

            // Only draw players that are visible on the screen
            if (screenX + tileSize > 0 && screenX < screenWidth &&
                screenY + tileSize > 0 && screenY < screenHeight) {
                g2.setColor(Color.BLUE);
                g2.fillRect(screenX, screenY, tileSize, tileSize); // Draw other players as blue squares
            }
        }

        // Draw the local player
        player.draw(g2);

        g2.dispose();
    }
}
