/**
 * Audio. 
 * Audio playback using the GSPlayer object.
 * By Ryan Kelln
 * 
 * Move the cursor across the screen to change volume. 
 */
 
import codeanticode.gsvideo.*;

GSPlayer sample;

void setup()
{
    size(100, 100);
    sample = new GSPlayer(this, "groove.mp3", false);
    sample.loop();
}

void draw()
{
    //sample.jump(float(mouseX) / width * sample.duration());
    
    sample.volume(float(mouseX) / width);
}
