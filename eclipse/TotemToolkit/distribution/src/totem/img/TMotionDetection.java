package totem.img;

import processing.core.PApplet;
import processing.core.PImage;

public class TMotionDetection {
	
	int threshold;
	public TMotionDetection() {
		threshold = 50;
	}
	
	public void setThreshold(int value){
		this.threshold = value;		
	}
	
	public int[] detectMotion(PApplet parent, PImage prev, PImage next) {
		
		int change = 0;
		
		PImage returnImage = new PImage(next.width,next.height);
		
		for (int x = 0; x < next.width; x++) {
			for (int y = 0; y < next.height; y++) {
				int loc = x + y * next.width; // Step 1, what is the 1D pixel
												// location
				int current = next.pixels[loc]; // Step 2, what is the
													// current color
				int previous = prev.pixels[loc]; // Step 3, what is the
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
					change++;
				} else {
					// If not, display white
					returnImage.pixels[loc] = parent.color(255);
				}
			}
		}
		return returnImage.pixels;
	}
}
