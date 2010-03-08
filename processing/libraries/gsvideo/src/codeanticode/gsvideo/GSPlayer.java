/**
 * This package provides classes to handle video in Processing. The API is compatible with the built-in video library of Processing. 
 * GSVideo uses the multimedia toolkit GStreamer (http://www.gstreamer.net/)  through the gstreamer-java bindings by Wayne Meissener:
 * http://code.google.com/p/gstreamer-java/ 
 * @author Andres Colubri, Ryan Kelln 
 * @version 0.5.1
 *
 * Copyright (c) 2008 Andres Colubri
 *
 * This source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package codeanticode.gsvideo;

import processing.core.*;

import java.io.*;
import java.nio.*;
import java.lang.reflect.*;

import org.gstreamer.*;
import org.gstreamer.elements.*;

/**
  * This class makes it possible to load and play generic media content through playbin (not only movies, but also audio files, etc).
  */
public class GSPlayer extends PImage implements PConstants 
{  
    /**
     * Creates an instance of GSPlayer loading the media file from filename and with fps = 30, assuming that it is a video file .
     * @param parent PApplet
     * @param filename String 
     */
    public GSPlayer(PApplet parent, String filename) 
    {
        this(parent, filename, true, 30);
    } 
	
    /**
     * Creates an instance of GSPlayer loading the media file from filename and with fps = 30,
     * @param parent PApplet
     * @param filename String 
     */
    public GSPlayer(PApplet parent, String filename, boolean loadVideo) 
    {
        this(parent, filename, loadVideo, 30);
    } 

    /**
     * Creates an instance of GSPlayer loading the movie from filename and with fps = ifps.
     * @param parent PApplet
     * @param filename String 
     * @param ifps int 
     */
    public GSPlayer(PApplet parent, String filename, boolean loadVideo, int ifps) 
    {
        // this creates a fake image so that the first time this
        // attempts to draw, something happens that's not an exception
        super(1, 1, RGB);

        this.parent = parent;
        gplayer = null;
	
        File file;
		
        GSVideo.init();
	
        // first check to see if this can be read locally from a file.
        boolean filefound = false;
        try 
        {
            try 
            {
                // first try a local file using the dataPath. usually this will
                // work ok, but sometimes the dataPath is inside a jar file,
                // which is less fun, so this will crap out.
                file = new File(parent.dataPath(filename));
                if (file.exists()) 
                {
                    gplayer = new PlayBin("GSPlayer");
                    gplayer.setInputFile(file);
                    filefound = true;
                }
            } 
            catch (Exception e) { }  // ignored

            // read from a file just hanging out in the local folder.
            // this might happen when the video library is used with some
            // other application, or the person enters a full path name
            if (gplayer == null) 
            {
                try 
                {
                    file = new File(filename);
                    if (file.exists()) 
                    {
                        gplayer = new PlayBin("GSPlayer");
                        gplayer.setInputFile(file);
                        filefound = true;
                    }
                } catch (Exception e) { }
            }
            // Network read needs to be implemented...  
        } 
        catch (SecurityException se) 
        {
            // online, whups. catch the security exception out here rather than
            // doing it three times (or whatever) for each of the cases above.
        }
	
        if (gplayer == null) 
        {
            parent.die("Could not load media file " + filename, null);
        }
	
        // we've got a valid movie! let's rock.
        try 
        {
            this.filename = filename; // for error messages
            fps = ifps;

            // register methods
            parent.registerDispose(this);

            try 
            {
                playerEventMethod = parent.getClass().getMethod("playerEvent", new Class[] { GSPlayer.class });
            } 
            catch (Exception e) 
            {
                // no such method, or an error.. which is fine, just ignore
            }


            if (loadVideo)
            {
	            RGBDataSink videoSink = new RGBDataSink("rgb",
	                new RGBDataSink.Listener() 
	                {
	                    public void rgbFrame(int w, int h, IntBuffer buffer) 
	                    {
	                        invokeEvent(w, h, buffer);
	                    }
	                }	
	            );
	            gplayer.setVideoSink(videoSink); 
            }
            else
            {
                gplayer.setVideoSink(ElementFactory.make("fakesink", "videosink"));
            }
            // Creating bus to handle end-of-stream event.
            gplayer.getBus().connect(
                new Bus.EOS() 
                {
                    public void endOfStream(GstObject element) 
                    { 
                        eosEvent();
                    }
                }
            );

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the full length of this movie (in seconds).
     * @return float
     */  	
    public float duration() 
    {	
        float sec = gplayer.queryDuration().toSeconds();
        float nanosec = gplayer.queryDuration().getNanoSeconds(); 
        return sec + nanoSecToSecFrac(nanosec);
    }

    /**
     * Set how often new frames are to be read from the movie.
     * Does not actually set the speed of the movie playback,
     * that's handled by the speed() method.
     * NOT IMPLEMENTED.
     * @param int ifps 
     * @see speed
     */
    public void frameRate(int ifps) 
    {
    }

    /**
     * Set a multiplier for how fast/slow the movie should be run.
     * The default is 1.0.
     * speed(2) will play the movie at double speed (2x).
     * speed(0.5) will play at half speed.
     * speed(-1) will play backwards at regular speed.
     * NOT IMPLEMENTED.
     * @param float rate
     */
    public void speed(float rate) 
    {
    }
  
    /**
     *  Return the current time in seconds.
     * @return float
     */ 
    public float time() 
    {
        float sec = gplayer.queryPosition().toSeconds();
        float nanosec = gplayer.queryPosition().getNanoSeconds();
        return sec + nanoSecToSecFrac(nanosec);
    }

    /**
     *  Jump to a specific location (in seconds).
     * The number is a float so fractions of seconds can be used.
     * @param float where
     */ 
    public void jump(float where) 
    {
        gplayer.seek(ClockTime.fromNanos(secToNanoLong(where)));
    }  
  
    /**
     *  Return the true or false depending on whether there is a new frame ready to be read.
     * @return boolean
     */ 
    public boolean available() 
    {
        return available;
    }
  
    /**
     * Begin playing the movie, with no repeat.
     */
    public void play() 
    {
        play = true;
        gplayer.play();
    }	
  
    /**
     * Begin playing the movie, with repeat.
     */
    public void loop() 
    {
        play = true;
        repeat = true;
        gplayer.play();
    }

    /**
     * Shut off the repeating loop.
     */
    public void noLoop() 
    {
        repeat = false;
    }

    /**
     * Pause the movie at its current time.
     */
    public void pause() 
    {
        play = false;
        gplayer.pause();
    }
    
    /**
     * Stop the movie, and rewind.
     */
    public void stop() 
    {
        play = false;	
        goToBeginning();
        gplayer.stop();
    }  

    /**
     * Reads the current video frame.
     */
    public void read()
    {
        if (firstFrame) 
        {
            super.init(movieWidth, movieHeight, RGB);
            loadPixels();
            firstFrame = false;
        }
        moviePixels.get(pixels);
        updatePixels();
	
        available = false;
    }

    /**
     * Goes to the first frame of the movie.
     */
    public void goToBeginning() 
    {
        gplayer.seek(ClockTime.fromNanos(0));
    }

	/**
	 * Change the volume. Values are from 0 to 1.
	 */
	public void volume(double v) {
		if (play) {
			gplayer.setVolume(v);
		}
	}
	
	/**
	 * Change the volume. Values are a percent from 0 to 100.
	 */
	public void volume(int v) {
		if (play) {
			gplayer.setVolume(v);			
		}
	}
	
    /**
     * @invisible
     */
    protected void eosEvent()
    {
        if (repeat) 
        {
            goToBeginning();
        }
    }

    /**
     * @invisible
     */
    protected void invokeEvent(int w, int h, IntBuffer buffer) 
    {
        available = true;
        movieWidth = w;
        movieHeight = h;
        moviePixels = buffer;
	
        if (play) 
        {
            // Creates a playerEvent.
            if (playerEventMethod != null) 
            {
                try 
                {
                    playerEventMethod.invoke(parent, new Object[] { this });
                } 
                catch (Exception e) 
                {
                    System.err.println("error, disabling playerEvent() for " + filename);
                    e.printStackTrace();
                    playerEventMethod = null;
                }
            }
        }
    }
  
    /**
     * @invisible
     */
    protected float nanoSecToSecFrac(float nanosec) 
    {
        for (int i = 0; i < 3; i++) nanosec /= 1E3;
        return nanosec;
    }
  
    /**
     * @invisible
     */
    protected long secToNanoLong(float sec) 
    {
        Float f = new Float(sec * 1E9);
        return f.longValue();
    }

    /**
     * @invisible
     */	
    public void dispose() 
    {
        stop();
    }

    /**
     * @invisible
     */	
    protected Method playerEventMethod;

    /**
     * @invisible
     */
    protected String filename;

    /**
     * @invisible
     */
    protected boolean play;

    /**
     * @invisible
     */
    protected boolean repeat;

    /**
     * @invisible
     */
    protected boolean available;

    /**
     * @invisible
     */
    protected boolean firstFrame = true;

    /**
     * @invisible
     */
    protected int fps = 0;

    /**
     * @invisible
     */  
    protected int movieWidth;

    /**
     * @invisible
     */
    protected int movieHeight;

    /**
     * @invisible
     */
    protected IntBuffer moviePixels;
  
    /**
     * @invisible
     */
    protected PlayBin gplayer;
}
