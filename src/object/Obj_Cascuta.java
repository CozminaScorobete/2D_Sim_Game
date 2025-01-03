package object;

import java.io.IOException;

import javax.imageio.ImageIO;

public class Obj_Cascuta extends SuperObject{
	
	public Obj_Cascuta() {
		
		name= "Cascuta";
		try {
			
			image = ImageIO.read(getClass().getResourceAsStream("/objects/cascuta.png"));
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
