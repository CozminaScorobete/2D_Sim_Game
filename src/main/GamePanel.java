package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import entity.Monster;
import entity.MonsterCold;
import entity.MonsterHot;
import entity.MonsterNormal;
import entity.Player;
import entity.RainParticle;
import entity.SnowParticle;
import object.SuperObject;
import sockets.GameClient;
import tile.TileManager;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; 
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; 
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; 
    public final int screenHeight = tileSize * maxScreenRow; 

    // WORLD SETTINGS
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 60;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // FPS
    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);

    public AssetSetter aSetter = new AssetSetter(this);
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[10];

    // MONSTER SYSTEM
    public ArrayList<Monster> monsters = new ArrayList<>(); // List of monsters

    // Multiplayer variables
    private Map<String, int[]> otherPlayers = new ConcurrentHashMap<>(); 
    private GameClient client;

    // GAME STATES
    public static final int STATE_TITLE = 0;
    public static final int STATE_PLAY = 1;
    public int gameState = STATE_PLAY;
    
    private Timer weatherUpdateTimer = new Timer();
    private String currentWeather = "Clear";
    public boolean isGameOver = false; // Track if the player has died
    
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
        spawnMonsters();
        initializeWeatherEffects();
        startWeatherUpdates(); // 🔹 Start auto weather updates
    }


    private ArrayList<RainParticle> rainParticles = new ArrayList<>();
    private ArrayList<SnowParticle> snowParticles = new ArrayList<>();
    private boolean isRaining = false;
    private boolean isSnowing = false;

    public void initializeWeatherEffects() {
        for (int i = 0; i < 100; i++) {
            rainParticles.add(new RainParticle(screenWidth, screenHeight));
            snowParticles.add(new SnowParticle(screenWidth, screenHeight));
        }
    }

    private void startWeatherUpdates() {
        weatherUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateWeather();
            }
        }, 0, 60000); // Every 60 seconds
    }

    public void updateWeather() {
        String newWeather = WeatherFetcher.getCurrentWeather();

        if (!newWeather.equals(currentWeather)) { // Only update if weather has changed
            currentWeather = newWeather;
            System.out.println("Weather changed to: " + currentWeather);
            
            spawnMonsters(); // 🔹 Re-spawn monsters based on new weather
            updateWeatherEffects(); // 🔹 Update rain/snow effects
        }
    }

    public void updateWeatherEffects() {
    	 isRaining = currentWeather.equalsIgnoreCase("Rain") ||
                 currentWeather.equalsIgnoreCase("Thunderstorm") ||
                 currentWeather.equalsIgnoreCase("Drizzle") ||
                 currentWeather.equalsIgnoreCase("Clouds");

        isSnowing = currentWeather.equalsIgnoreCase("Snow");

        if (isRaining) {
            for (RainParticle r : rainParticles) {
                r.update();
            }
        }
        if (isSnowing) {
            for (SnowParticle s : snowParticles) {
                s.update();
            }
        }
    }


    public void drawWeatherEffects(Graphics2D g2) {
        if (isRaining) {
            for (RainParticle r : rainParticles) {
                r.draw(g2);
            }
        }
        if (isSnowing) {
            for (SnowParticle s : snowParticles) {
                s.draw(g2);
            }
        }
    }

    public void spawnMonsters() {
        System.out.println("Current Weather: " + currentWeather);

        Random rand = new Random();
        monsters.clear(); // Remove old monsters

        for (int i = 0; i < 5; i++) { // Spawn 5 monsters
            int spawnX, spawnY;

            do {
                spawnX = tileSize * (rand.nextInt(maxWorldCol - 20) + 10);
                spawnY = tileSize * (rand.nextInt(maxWorldRow - 20) + 10);
            } while (isSpawnOccupied(spawnX, spawnY));

            Monster monster;
            if (currentWeather.equalsIgnoreCase("Snow")) {
                monster = new MonsterCold(this);
            } else if (currentWeather.equalsIgnoreCase("Rain")||currentWeather.equalsIgnoreCase("Thunderstorm")||currentWeather.equalsIgnoreCase("Drizzle")||currentWeather.equalsIgnoreCase("Clouds")) {
                monster = new MonsterHot(this);
            } else {
                monster = new MonsterNormal(this);
            }

            monster.worldX = spawnX;
            monster.worldY = spawnY;
            monster.spawnX = spawnX;
            monster.spawnY = spawnY;

            monsters.add(monster);
        }
    }



    // Helper method to ensure no overlapping spawn points
    private boolean isSpawnOccupied(int x, int y) {
        for (Monster m : monsters) {
            if (m.spawnX == x && m.spawnY == y) {
                return true; // Spawn point is already taken
            }
        }
        return false; // Spawn point is free
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
        long lastTime = System.nanoTime();
        long currentTime;

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
            ///    System.out.println("FPS:" + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        player.update();

        // Update monsters
        for (Monster monster : monsters) {
            monster.update();
        }
        updateWeatherEffects();

        // Send player's position to the server
        if (client != null) {
            try {
                client.sendMove(player.worldX, player.worldY);
            } catch (Exception e) {
                System.out.println("Failed to send player move to server: " + e.getMessage());
            }
        }
    }

    public void setOtherPlayers(Map<String, int[]> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == STATE_PLAY) {
            drawGame(g2);
        }
        drawWeatherEffects(g2); // 🔹 Draw weather effects after everything else
        drawHealthBar(g2);  // 🛠 Draw the health bar
        if (isGameOver) {
            g2.setColor(new Color(0, 0, 0, 150)); // Transparent black overlay
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setColor(Color.RED);
            g2.setFont(g2.getFont().deriveFont(50f)); // Large font
            String text = "YOU DIED";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
        }
        g2.dispose();
    }

   
    public void drawHealthBar(Graphics2D g2) {
        int barWidth = 200;
        int barHeight = 20;
        int x = 20;
        int y = 20;

        // Draw background bar
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        // Draw current health
        int currentWidth = (int) ((double) player.health / player.maxHealth * barWidth);
        g2.setColor(Color.RED);
        g2.fillRect(x, y, currentWidth, barHeight);

        // Draw border
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, barWidth, barHeight);
    }

    public void drawGame(Graphics2D g2) {
        // Draw tiles
        tileM.draw(g2);

        // Draw objects
        for (SuperObject obj : obj) {
            if (obj != null) {
                obj.draw(g2, this);
            }
        }

        // Draw other players
        for (Map.Entry<String, int[]> entry : otherPlayers.entrySet()) {
            int[] position = entry.getValue();
            int worldX = position[0];
            int worldY = position[1];

            int screenX = worldX - player.worldX + player.screenX;
            int screenY = worldY - player.worldY + player.screenY;

            if (screenX + tileSize > 0 && screenX < screenWidth &&
                screenY + tileSize > 0 && screenY < screenHeight) {
                g2.setColor(Color.BLUE);
                g2.fillRect(screenX, screenY, tileSize, tileSize); // Draw as blue squares
            }
        }

        // Draw monsters
        for (Monster monster : monsters) {
            monster.draw(g2);
        }

        // Draw local player
        player.draw(g2);
    }
}
