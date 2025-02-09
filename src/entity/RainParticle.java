package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class RainParticle {
    int x, y, speed;
    Random rand = new Random();

    public RainParticle(int screenWidth, int screenHeight) {
        this.x = rand.nextInt(screenWidth);
        this.y = rand.nextInt(screenHeight);
        this.speed = 5 + rand.nextInt(5);
    }

    public void update() {
        y += speed;
        if (y > 800) {
            y = 0;
            x = rand.nextInt(800);
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 255, 150));
        g2.drawLine(x, y, x + 2, y + 10);
    }
}
