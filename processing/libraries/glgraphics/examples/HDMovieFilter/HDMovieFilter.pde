// Applying GPU filters to an HD movie (it also uses the gsvideo library).
// By Andres Colubri
// Use it in present mode, since it runs at full screen resolution.
// The HD mov file is not included for space constraints, but here:
// http://www.hd-trailers.net/
// you can download HD trailers to test the example.
// The gsvideo library is available here:
// http://users.design.ucla.edu/~acolubri/processing/gsvideo/home/

import processing.opengl.*;
import codeanticode.glgraphics.*;
import codeanticode.gsvideo.*;

GSMovie movie;
GLTexture texSrc, texFiltered;

GLTextureFilter blur, emboss, edges, poster, currentFilter;
String filterStr;

PFont font;

void setup()
{
    size(screen.width, screen.height, GLConstants.GLGRAPHICS);
   
    movie = new GSMovie(this, "hdclip.mov");
    movie.loop();
   
    texSrc = new GLTexture(this);
    texFiltered = new GLTexture(this);

   
    blur = new GLTextureFilter(this, "gaussBlur.xml");
    emboss = new GLTextureFilter(this, "emboss.xml");
    edges = new GLTextureFilter(this, "edgeDetect.xml");
    poster = new GLTextureFilter(this, "posterize.xml");
    
    font = loadFont("EstrangeloEdessa-24.vlw");
    textFont(font, 24);      
    
    currentFilter = edges;
    filterStr = "edges";
}

void movieEvent(GSMovie movie)
{
    movie.read();
}

void draw()
{
    background(0);
    
    if ((1 < movie.width) && (1 < movie.height))
    {
        texSrc.putPixelsIntoTexture(movie);

        // Calculating height to keep aspect ratio.      
        float h = width * texSrc.height / texSrc.width;
        float b = 0.5 * (height - h);

        if (currentFilter == null) image(texSrc, 0, b, width, h);
        else 
        {
            texSrc.filter(currentFilter, texFiltered);
            image(texFiltered, 0, b, width, h);            
        }          
    }

    text("Movie resolution: " + movie.width + "x" + movie.height + " | Screen resolution: " + width + "x" + height + " | Filter: " + filterStr, 10, 30);
    text("B - bur filter | E - emboss filter | D - edges filter | P - posterize filter | X - disable filter", 10, height - 30); 
    text("FPS: " + frameRate, width - 200, height - 30);
}

void keyPressed()
{
    if ((key == 'B') || (key == 'b')) { currentFilter = blur; filterStr = "blur"; }
    else if ((key == 'E') || (key == 'e')) { currentFilter = emboss; filterStr = "emboss"; }
    else if ((key == 'D') || (key == 'd')) { currentFilter = edges; filterStr = "edges"; }  
    else if ((key == 'P') || (key == 'p')) { currentFilter = poster; filterStr = "posterize"; }
    else if ((key == 'X') || (key == 'x')) { currentFilter = null; filterStr = "none"; }
}
