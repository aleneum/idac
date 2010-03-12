package totem.visual;

import processing.core.PApplet;

public class TSimpleText {

	PApplet parent;
	int textColor = 0;
	
	public TSimpleText(PApplet aParent){
		this.parent = aParent;		
	}
	
	public void draw(float crowd){
		  parent.fill(0,textColor,0);
		  if (textColor >= 255) {
		    textColor = 0;  
		  } else {
		    textColor+=4;
		  }
		  
		  String outText = "CALL THE DEVELOPER";
		  
		  if (crowd < 0.2) {
		    outText = "Make some noise!";
		  } else if (crowd < 0.4) {
		    outText = "Yeah, good work!\n Go on!";
		  } else if (crowd < 0.6) {
		    outText = "PARTY HARD!";
		  } else if (crowd < 0.8) {
		    outText = "IT'S ON!!";  
		  } else if (crowd < 0.9) {
		    outText = "AWESOME!!!";
		  } else {
		    outText = "O N   F I R E ! ! !";
		  }
		  
		  parent.text(outText,parent.width/2,parent.height/2);
	}
	
}
