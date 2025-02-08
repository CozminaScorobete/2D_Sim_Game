package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class SnowParticle {
    int x, y, speed;
    Random rand = new Random();

    public SnowParticle(int screenWidth, int screenHeight) {
        this.x = rand.nextInt(screenWidth);
        this.y = rand.nextInt(screenHeight);
        this.speed = 2 + rand.nextInt(3);
    }

    public void update() {
        y += speed;
        x += rand.nextInt(3) - 1;
        if (y > 800) {
            y = 0;
            x = rand.nextInt(800);
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, 5, 5);
    }
}
