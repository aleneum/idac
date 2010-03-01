// Rendering the result of a texture filter operation to a 3D model.
// By Andres Colubri.
//
// Basically, the luminance of of the pixels that result of applying 
// a pulsating emboss filter on an image are used as the Z coordinates 
// of the model. The number of vertices in the model equals  the 
// resolution of the source image, in this case 200x200 = 40000. 
// All of this can be done very quickly because the model is stored and updated
// directly in the GPU memory. There are no transfers between CPU and GPU.

import processing.opengl.*;
import codeanticode.glgraphics.*;

GLTextureFilter pulse, zMap;
GLTexture srcTex, tmpTex, destTex;
GLModel destModel;
GLCamera cam;

void setup()
{
  size(640, 480, GLConstants.GLGRAPHICS);
  cam = new GLCamera(this, 0, 0, 10000, 0, 0, 0);
   
  srcTex = new GLTexture(this, "milan_rubbish.jpg");

  pulse = new GLTextureFilter(this, "pulsatingEmboss.xml");
  zMap = new GLTextureFilter(this, "brightExtract.xml");
  
  tmpTex = new GLTexture(this, srcTex.width, srcTex.height);  
  destTex = new GLTexture(this, srcTex.width, srcTex.height, GLTexture.FLOAT, GLTexture.NEAREST);
  
  int numPoints = srcTex.width * srcTex.height;
  destModel = new GLModel(this, numPoints, POINTS, GLModel.STREAM);
  destModel.initColors();
  
  float[] colors;  
  colors = new float[4 * numPoints];  
  for (int i = 0; i < numPoints; i++)
  {
      for (int j = 0; j < 3; j++) colors[4 * i + j] = 1.0;
      colors[4 * i + 3] = 0.5;
  } 
  
  destModel.updateColors(colors);
}

void draw()
{
  pulse.apply(srcTex, tmpTex);
  zMap.apply(tmpTex, destTex, destModel);

  GLGraphics renderer = (GLGraphics)g;
  renderer.beginGL();

  cam.feed();
  cam.clear(0);
  destModel.render();
  cam.done();
  
  renderer.endGL(); 
   
  println(frameRate);
}

void mouseMoved() 
{   
    cam.arc(radians(mouseY - pmouseY));  
    cam.circle(radians(mouseX - pmouseX));
}
