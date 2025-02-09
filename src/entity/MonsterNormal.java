package entity;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MonsterNormal extends Monster {
    public MonsterNormal(GamePanel gp) {
        super(gp);
        loadMonsterImage();
    }

    @Override
    public void loadMonsterImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/monsters/monster_normal.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load normal monster image.");
        }
    }
}
