/**
 * This package provides classes to handle video in Processing. The API is compatible with the built-in video library of Processing. 
 * GSVideo uses the multimedia toolkit GStreamer (http://www.gstreamer.net/)  through the gstreamer-java bindings by Wayne Meissener:
 * http://code.google.com/p/gstreamer-java/ 
 * @author Andres Colubri
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

import java.io.File;
import java.nio.ByteBuffer;

import org.gstreamer.Buffer;
import org.gstreamer.elements.RGBDataFileSink;

/**
 * This class makes movies from a running program.
 */
public class GSMovieMaker 
{
    public GSMovieMaker(PApplet parent, int requestWidth, int requestHeight, String filename) 
    {
        init(parent, requestWidth, requestHeight, filename, THEORA, MEDIUM, 30);
    }
  
    public GSMovieMaker(PApplet parent, int requestWidth, int requestHeight, String filename, int codecType, int ifps) 
    {
    	init(parent, requestWidth, requestHeight, filename, codecType, MEDIUM, ifps);
    }

    public GSMovieMaker(PApplet parent, int requestWidth, int requestHeight, String filename, int codecType, int codecQuality, int ifps) 
    {
    	init(parent, requestWidth, requestHeight, filename, codecType, codecQuality, ifps);
    }
    
    public void addFrame(int[] pixels) 
    {
    	if (recording && pixels.length == width * height)
    	{
            Buffer srcBuffer = new Buffer(width * height * 4);
            
            ByteBuffer tmpBuffer = srcBuffer.getByteBuffer();
            tmpBuffer.clear();
            tmpBuffer.asIntBuffer().put(pixels);

            int n = recorder.getNumQueuedFrames();
            if (0 < n)
            {
            	nConsecutiveFilledQueue++;
            	if (3 < nConsecutiveFilledQueue)
            	    System.err.println("Warning: GSMovieMaker is filling up with unprocessed frames. " +
            	    		           "Try using a lower framerate (both for drawing and saving), " + 
            	    		           "or a smaller video resolution.");
            }
            else nConsecutiveFilledQueue = 0;
            
            recorder.pushRGBFrame(srcBuffer);
    	}
    }
    
    public void start() 
    { 
    	recorder.start();
    	recording = true;
    }
  
    public void finish() 
    {
    	recording = false;
    	recorder.stop();
    }
    
    public boolean isRecording()
    {
    	return recording;
    }
   
    public void dispose() 
    {
    }

    protected void init(PApplet parent, int requestWidth, int requestHeight, String filename, int codecType, int codecQuality, int ifps)
    {
        this.parent = parent;
        
        width = requestWidth; 
        height = requestHeight;
        
        GSVideo.init();	   
	  
        // register methods
        parent.registerDispose(this);
        
        String[] propNames = null;
        Object[] propValues = null;
        
        String encoder = "";
        String muxer = "";
        
        if (codecType == THEORA)
        {
        	encoder = "theoraenc";
        	muxer = "oggmux";
        	
        	propNames = new String[1];
        	propNames[0] = "quality";
        	propValues = new Object[1];
        	
        	Integer q = 31;
        	if (codecQuality == WORST) q = 0;
        	else if (codecQuality == LOW) q = 15;
            else if (codecQuality == MEDIUM) q = 31;
            else if (codecQuality == HIGH) q = 47;
            else if (codecQuality == BEST) q = 63;
        	propValues[0] = q;	
        }
        else if (codecType == XVID)
        {
        	encoder = "xvidenc";
        	muxer = "avimux";        	
        }
        else if (codecType == X264)
        {
        	encoder = "x264enc";
        	muxer = "avimux";        	
        	
        }
        else
        {
        	parent.die("Unrecognized video format", null);
        }
        
        File file = new File(parent.savePath(filename));
        
        recorder = new RGBDataFileSink("MovieMaker", width, height, ifps, encoder, propNames, propValues, muxer, file);
        recorder.setQueueSize(100);
        recording = false;
        nConsecutiveFilledQueue = 0;    
    }
        
    private PApplet parent;
    private boolean recording;
    private RGBDataFileSink recorder;
    private int width, height;
    private int nConsecutiveFilledQueue;
  
    public static final int THEORA = 0;  
    public static final int XVID = 1;
    public static final int X264 = 2;
    
    public static final int WORST = 0;
    public static final int LOW = 1;
    public static final int MEDIUM = 2;
    public static final int HIGH = 3;
    public static final int BEST = 4;
}
