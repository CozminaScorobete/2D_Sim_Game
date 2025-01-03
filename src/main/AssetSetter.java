package main;

import object.Obj_Cascuta;

public class AssetSetter {

	
	GamePanel gp;
	
	public AssetSetter(GamePanel gp) {
		
		this.gp = gp;
		
	}
	
	public void setObject() {
		
		gp.obj[0] = new Obj_Cascuta();
		gp.obj[0].worldX = 15 * gp.tileSize;
		gp.obj[0].worldY = 7 * gp.tileSize;
		
		
		
	}
	
}
