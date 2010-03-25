package totem.img;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Simple motion detection based on difference between two pictures. 
 * The difference is calculated for every pixel of the images. The threshold
 * determines if a difference is detected. 
 * @author alex
 *
 */
public class TMotionDetection {
	
	// threshold that determines when movement is detected
	int threshold;
	
	/**
	 * Initialize the TMotionDetection object and set the threshold.S
	 */
	public TMotionDetection() {
		threshold = 50;
	}
	
	/**
	 * Sets the threshold that determines if pixel differences 
	 * are interpreted as motion.
	 * @param value new threshold value
	 */
	public void setThreshold(int value){
		this.threshold = value;		
	}
	
	
	/**
	 * A simple motion detection base on image comparison. 
	 * Both images are compared pixelwise. If the difference between related
	 * pixels is bigger than threshold the resulting pixel is black, otherwise
	 * its white. The result is returned as a new PImage.
	 * @param parent object that holds drawing methods like draw and color
	 * @param prev first image to be compared
	 * @param next second image to be compared
	 * @return Image
	 */
	public int[] detectMotion(PApplet parent, PImage prev, PImage next) {
		
		// create a new PImage to be filled and returned later.
		PImage returnImage = new PImage(next.width,next.height);
		
		for (int x = 0; x < next.width; x++) {
			for (int y = 0; y < next.height; y++) {
				int loc = x + y * next.width; 		// Step 1, what is the 1D pixel
													// location
				int current = next.pixels[loc]; 	// Step 2, what is the
													// current color
				int previous = prev.pixels[loc]; 	// Step 3, what is the
													// previous color

				// Step 4, compare colors (previous vs. current)
				float r1 = parent.red(current);
				float g1 = parent.green(current);
				float b1 = parent.blue(current);
				float r2 = parent.red(previous);
				float g2 = parent.green(previous);
				float b2 = parent.blue(previous);
				float diff = PApplet.dist(r1, g1, b1, r2, g2, b2);

				// Step 5, How different are the colors?
				// If the color at that pixel has changed, then there is motion
				// at that pixel.
				if (diff > threshold) {
					// If motion, display black
					returnImage.pixels[loc] = parent.color(0);
				} else {
					// If not, display white
					returnImage.pixels[loc] = parent.color(255);
				}
			}
		}
		return returnImage.pixels;
	}
}
