// Two opengl-window output example. The second window is used 
// to show a single GLTexture. Press a key to hide/show the 
// second window.
//  
// By Andres Colubri. 
//
// An application of this technique would be an application where 
// the main processing window is used to interface with the user 
// and create the graphics depending on the input, while the second 
// window only outputs the resulting graphics (to a secondary
// screen or a projector, for instance).

import processing.opengl.*;
import codeanticode.glgraphics.*;

GLTexture tex, ftex; 
GLTextureFilter pixelate;
GLTextureWindow texWin;

void setup() {
  size(300, 300, GLConstants.GLGRAPHICS);
  // The window is located at (0, 0) and has a size of (400, 400).
  // Important: the texture window has be be created before creating any
  // texture, filter or any other OpenGL-related element.
  texWin = new GLTextureWindow(this, 0, 0, 400, 400);
  
  tex = new GLTexture(this, "milan_rubbish.jpg");
  ftex = new GLTexture(this, tex.width, tex.height);
  pixelate = new GLTextureFilter(this, "pixelate.xml");
  
  // Attaching a texture to the window.
  texWin.setTexture(ftex);
  
  // A call to init() is required at the end of setup.
  texWin.init();
  
  /*
  // If setting the override property of the texture window to 
  // false, then it won't be drawn automatically. In this case,
  // it can be manually updated after the main draw is finished
  // using the post event (uncomment the post() function at the
  // bottom of this example):
  texWin.setOverride(true);
  registerPost(this);
  */
}
  
void draw() {
  background(0);
  fill(255);

  pixelate.setParameterValue("pixel_size", map(mouseX, 0, width, 1, 30));   
  tex.filter(pixelate, ftex);
  image(tex, mouseX, mouseY);
}

void keyPressed()
{
    if (texWin.isVisible()) texWin.hide();
    else texWin.show();
}

/*
void post()
{
    texWin.render();  
}
*/
