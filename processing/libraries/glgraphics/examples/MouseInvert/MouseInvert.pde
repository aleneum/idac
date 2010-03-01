// This example shows how to pass mouse position information to a filter.
// The movie pixels are inverted according to their distance to the mouse
// cursor. This distance can be adjusted with the cursor keys.
//  
// By Andres Colubri. 

import processing.opengl.*;
import codeanticode.glgraphics.*;
import codeanticode.gsvideo.*;

GSMovie movie;
GLTexture srcTex, destTex;
GLTextureFilter invert;
float mouseDist = 50.0;

void setup()
{
  size(640, 480, GLConstants.GLGRAPHICS);

  movie = new GSMovie(this, "station.mov"); 
  movie.loop();

  invert = new GLTextureFilter(this, "MouseInvert.xml");
  srcTex = new GLTexture(this);
  destTex = new GLTexture(this);
}

void movieEvent(GSMovie movie)
{
  movie.read();
}

void draw()
{
  // GLMovie.newFrame() returns true when a new frame has been read.
  if (movie.newFrame())
  {
    srcTex.putPixelsIntoTexture(movie);

    // Mouse coordinates need to be mapped to the texture size:
    float x = map(mouseX, 0, width, 0, srcTex.width);
    float y = map(mouseY, 0, height, 0, srcTex.height);
    invert.setParameterValue("mpos", new float[]{x, y});
    invert.setParameterValue("mdist", mouseDist);
    invert.apply(srcTex, destTex);
    // The last line is equivalent to:
    // srcTex.filter(invert, destTex);    
 
    image(destTex, 0, 0, width, height);
    
    // The current video frame is marked as "old", so video is not processed again
    // until a new frame is read.
    movie.oldFrame();
  }
}

void keyPressed()
{
  if (key == CODED) 
  {
    if (keyCode == UP) 
    {
      mouseDist = constrain(mouseDist + 5.0, 0.0, 200.0);
    } 
    else if (keyCode == DOWN)
    {
      mouseDist = constrain(mouseDist - 5.0, 0.0, 200.0);
    }
  }
}

