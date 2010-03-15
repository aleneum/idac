package totem.visual;

import processing.core.PApplet;

/**
 * Writes fancy text. Nothing fancy about that ;D.
 * @author alex
 *
 */
public class TSimpleText {

	PApplet parent;
	int textColor = 0;
	
	/**
	 * Initialize class. The parent object is needed because it holds the necessary
	 * drawing references.
	 * @param aParent main processing application
	 */
	public TSimpleText(PApplet aParent){
		this.parent = aParent;		
	}
	
	/**
	 * Draw text message depending on the supplied value. The range goes from
	 * 0 to 1. The "best" output will be drawn for every value higher or equal one.
	 * @param crowd value that defines the text output
	 */
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
