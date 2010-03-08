// Swarming points using GLModel and GLCamera. The GLModel
// class allows to create 3D models (with colors, normals
// and textures) directly in the GPU memory.
// By Andres Colubri.
//
// Different camera motions are possible using the GLCamera
// object. Its API follows the Obsessive Camera Direction library:
// http://www.gdsstudios.com/processing/libraries/ocd/reference/

import processing.opengl.*;
import codeanticode.glgraphics.*;

GLCamera cam;
GLModel model;
float[] coords;
float[] colors;

int numPoints = 10000;

float distance = 500; // Distance of camera from origin.

float sensitivity = 1.0; // Scale factor to control camera motions.

void setup()
{
    size(640, 480, GLConstants.GLGRAPHICS);  

    cam = new GLCamera(this, 0, 0, distance, 0, 0, 0);
    
    model = new GLModel(this, numPoints, POINTS, GLModel.DYNAMIC);
    model.initColors();
    
    coords = new float[4 * numPoints];
    colors = new float[4 * numPoints];
    
    for (int i = 0; i < numPoints; i++)
    {
        for (int j = 0; j < 3; j++) coords[4 * i + j] = 2 * random(-1, 1);
        for (int j = 0; j < 3; j++) colors[4 * i + j] = random(0, 1);
        colors[4 * i + 3] = 0.9;
    }

   model.updateVertices(coords);
   model.updateColors(colors);   
}

void draw()
{    
    GLGraphics renderer = (GLGraphics)g;
    renderer.beginGL();
  
    for (int i = 0; i < numPoints; i++)
        for (int j = 0; j < 3; j++)
            coords[4 * i + j] += random(-0.01, 0.01);
            
    model.updateVertices(coords);

    cam.feed();       // Displays the viewport using current the camera motions.
    cam.clear(0);
    model.render();
    cam.done();       // After all the drawing operations that start after feed().
    
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
    //cam.tumble(radians(sensitivity * (mouseX - pmouseX)), radians(sensitivity * (mouseY - pmouseY)));
    cam.track(sensitivity * (mouseX - pmouseX), sensitivity * (mouseY - pmouseY));
    
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
