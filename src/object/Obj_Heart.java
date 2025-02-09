package object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class Obj_Heart extends SuperObject {

    public Obj_Heart() {
        name = "Heart";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/hart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
