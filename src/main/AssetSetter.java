package main;

import object.Obj_Cascuta;
import object.Obj_Heart;

public class AssetSetter {

	
	GamePanel gp;
	
	public AssetSetter(GamePanel gp) {
		
		this.gp = gp;
		
	}
	
	public void setObject() {
	    gp.obj[0] = new Obj_Cascuta();  
	    gp.obj[0].worldX = 15 * gp.tileSize;
	    gp.obj[0].worldY = 7 * gp.tileSize;

	    // 🛠 NEW: Add a heart object
	    gp.obj[1] = new Obj_Heart();  
	    gp.obj[1].worldX = 10 * gp.tileSize;
	    gp.obj[1].worldY = 20 * gp.tileSize;
	}

	
}
