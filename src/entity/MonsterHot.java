package entity;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MonsterHot extends Monster {
    public MonsterHot(GamePanel gp) {
        super(gp);
        loadMonsterImage();
    }

    @Override
    public void loadMonsterImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/monsters/monster_hot.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load hot monster image.");
        }
    }
}
