// Textured GLModel example.
// By Andres Colubri.
//
// Different camera motions are possible using the GLCamera
// object. Its API follows the Obsessive Camera Direction library:
// http://www.gdsstudios.com/processing/libraries/ocd/reference/

import processing.opengl.*;
import codeanticode.glgraphics.*;

GLCamera cam;
GLModel model;
GLTexture tex;
float[] coords;
float[] colors;
float[] texcoords;

int numPoints = 4;

float distance = 1000;

float sensitivity = 1.0; // Scale factor to control camera motions.

void setup()
{
    size(640, 480, GLConstants.GLGRAPHICS);  

    cam = new GLCamera(this, 0, 0, distance, 0, 0, 0);
    
    model = new GLModel(this, numPoints, QUADS, GLModel.DYNAMIC);
    model.initColors();
    tex = new GLTexture(this, "milan.jpg");
    
    coords = new float[4 * numPoints];
    colors = new float[4 * numPoints];
    texcoords = new float[2 * numPoints];
    
    coords[0] = 0;
    coords[1] = 0;
    coords[2] = 0;
    texcoords[0] = 0;
    texcoords[1] = 0;
    
    coords[4 + 0] = 10;
    coords[4 + 1] = 0;
    coords[4 + 2] = 0;
    texcoords[2 + 0] = 1;
    texcoords[2 + 1] = 0;

    coords[4 * 2 + 0] = 10;
    coords[4 * 2 + 1] = 10;
    coords[4 * 2 + 2] = 0;
    texcoords[2 * 2 + 0] = 1;
    texcoords[2 * 2 + 1] = 1;

    coords[4 * 3 + 0] = 0;
    coords[4 * 3 + 1] = 10;
    coords[4 * 3 + 2] = 0;
    texcoords[2 * 3 + 0] = 0;
    texcoords[2 * 3 + 1] = 1;

    for (int i = 0; i < numPoints; i++)
    {        
        for (int j = 0; j < 3; j++) colors[4 * i + j] = random(0, 1);
        colors[4 * i + 3] = 0.9;
    }
    

   model.updateVertices(coords);
   model.updateColors(colors);   
   model.initTexures(1);
   model.setTexture(0, tex);
}

void draw()
{    
    GLGraphics renderer = (GLGraphics)g;
  
    for (int i = 0; i < numPoints; i++)
        for (int j = 0; j < 3; j++)
            coords[4 * i + j] += random(-0.1, 0.1);
            
    model.updateVertices(coords);
    model.updateTexCoords(0, texcoords);

    cam.feed();
    cam.clear(0);
    model.render();
    cam.done();
  
    renderer.endGL(); 
  
    println(frameRate);
}

void mouseMoved() 
{   
    //cam.zoom(radians(sensitivity * (mouseY - pmouseY)) / 2.0);
    //cam.truck(sensitivity * (mouseX - pmouseX));
    //cam.boom(sensitivity * (mouseY - pmouseY));
    //cam.dolly(sensitivity * (mouseY - pmouseY));
     
    //cam.roll(radians(sensitivity * (mouseX - pmouseX)));
    //cam.arc(radians(sensitivity * (mouseY - pmouseY)));
    //cam.circle(radians(sensitivity * (mouseX - pmouseX)));
    cam.tumble(radians(sensitivity * (mouseX - pmouseX)), radians(sensitivity * (mouseY - pmouseY)));
    //cam.track(sensitivity * (mouseX - pmouseX), sensitivity * (mouseY - pmouseY));
    
    // tilt, pan and look need sensibility much smaller than 1 (0.001 for example).
    //cam.tilt(radians(sensitivity * (mouseY - pmouseY)) / 2.0); 
    //cam.pan(radians(sensitivity * (mouseX - pmouseX)) / 2.0);
    //cam.look(radians(sensitivity * (mouseX - pmouseX)) / 2.0, radians(sensitivity * (mouseY - pmouseY)) / 2.0);
}

void mousePressed() 
{
    //cam.aim(1, 1, 0);    
    //cam.jump(1, 5, 0.5 * distance);
}

void mouseReleased() {
    //cam.aim(0, 0, 0);
    //cam.jump(0, 0, distance);
}
