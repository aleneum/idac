// Applying a filter to a movie (it also uses the gsvideo library).
// By Andres Colubri
// The gsvideo library is available here:
// http://users.design.ucla.edu/~acolubri/processing/gsvideo/home/

import processing.opengl.*;
import codeanticode.glgraphics.*;
import codeanticode.gsvideo.*;

GSMovie movie;
GLTexture tex0, tex1, tex2;

GLTextureFilter blur, pixelate;

void setup()
{
    size(640, 480, GLConstants.GLGRAPHICS);
   
    movie = new GSMovie(this, "station.mov");
    movie.loop();
   
    tex0 = new GLTexture(this);
    tex1 = new GLTexture(this);
    tex2 = new GLTexture(this);
   
    blur = new GLTextureFilter(this, "gaussBlur.xml");
    pixelate = new GLTextureFilter(this, "pixelate.xml");    
}

void movieEvent(GSMovie movie)
{
    movie.read();
}

void draw()
{
    if ((1 < movie.width) && (1 < movie.height))
    {
        tex0.putPixelsIntoTexture(movie);

        pixelate.setParameterValue("pixel_size", map(mouseX, 20, 640, 1, 30)); 
        tex0.filter(blur, tex1);
        tex1.filter(pixelate, tex2);
        
        image(tex2, 0, 0, width, height);
    }
    else background(0);
}
