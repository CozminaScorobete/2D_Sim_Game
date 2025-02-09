package entity;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MonsterCold extends Monster {
    public MonsterCold(GamePanel gp) {
        super(gp);
        loadMonsterImage();
    }

    @Override
    public void loadMonsterImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/monsters/monster_cold.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load cold monster image.");
        }
    }
}
