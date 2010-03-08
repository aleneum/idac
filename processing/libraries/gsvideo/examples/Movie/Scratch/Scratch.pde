/**
 * Scratch. 
 * by Andres Colubri
 * 
 * Move the cursor horizontally across the screen to set  
 * the position in the movie file.
 */
 
import codeanticode.gsvideo.*;

GSMovie myMovie;


void setup() {
  size(640, 480);
  background(0);
  // Load and play the video in a loop
  myMovie = new GSMovie(this, "station.mov");
  myMovie.play();
}


void movieEvent(GSMovie myMovie) {
  myMovie.read();
}

void draw() {
  if ((1 < myMovie.width) && (1 < myMovie.height)) {
    myMovie.jump(myMovie.duration() * mouseX / width);
    image(myMovie, 0, 0, width, height);
  }
}
