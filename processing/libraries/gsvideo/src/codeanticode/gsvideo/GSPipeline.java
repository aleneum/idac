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

import java.nio.*;
import java.lang.reflect.*;
import java.util.ArrayList;

import org.gstreamer.*;
import org.gstreamer.elements.*;

/**
 * This class allows to create a custom GStreamer pipeline.
 */
public class GSPipeline extends PImage implements PConstants 
{
    /**
     * Creates an instance of GSPipeline using the pipeline specified in the string pipe.
     * @param parent PApplet
     * @param pipe String 
     */
    public GSPipeline(PApplet parent, String pipeStr) 
    {
        // this creates a fake image so that the first time this
        // attempts to draw, something happens that's not an exception
        super(1, 1, RGB);
		
        this.parent = parent;

        gpipe = null;
	
        GSVideo.init();

        // register methods
        parent.registerDispose(this);

        try 
        {
            pipelineEventMethod = parent.getClass().getMethod("pipelineEvent", new Class[] { GSPipeline.class });
        } 
        catch (Exception e) 
        {
            // no such method, or an error.. which is fine, just ignore
        }
		
        // Determining if the last element is fakesink or filesink.
		int idx; 
		String lastElem, lastElemName; 
		String[] parts;
		
		idx = pipeStr.lastIndexOf('!');
		lastElem = pipeStr.substring(idx + 1, pipeStr.length()).trim();

		parts = lastElem.split(" ");
		if (0 < parts.length) lastElemName = parts[0];
		else lastElemName = "";
		
        boolean fakeSink = lastElemName.equals("fakesink");
        boolean fileSink = lastElemName.equals("filesink");        
        
        if (fakeSink || fileSink)
        {
        	// If the pipeline ends in a fakesink or filesink element, the RGBDataSink is not added at the end of it...
        	gpipe = Pipeline.launch(pipeStr);
        }
        else
        {
        	// ...otherwise it is.
        	gpipe = Pipeline.launch(pipeStr + " ! fakesink name=VideoSink");
            videoSink = new RGBDataSink("rgb", gpipe,
                new RGBDataSink.Listener() 
                {
                    public void rgbFrame(int w, int h, IntBuffer buffer) 
                    {
                        invokeEvent(w, h, buffer);
                    }
                }
            );
        }
        
        gpipe.setState(State.PLAYING); 
    }

    /**
     * Returns "true" when a new video frame is available to read.
     * @return String[]
     */  
    public boolean available() 
    {
        return available;
    }
    
    /**
     *  Return true or false depending on whether a new frame has been read.
     * @return boolean
     */ 
    public boolean newFrame() 
    {
        return newFrame;
    }    

    /**
     *  Sets the value of newFrame to false.
     */ 
    public void oldFrame() 
    {
        newFrame = false;
    }    
        
    /**
     * Reads the current video frame.
     */
    public void read() 
    {
        if (firstFrame) 
        {
            super.init(pipeWidth, pipeHeight, RGB);
            loadPixels();
            firstFrame = false;
        }		
        pipePixels.get(pixels);
        updatePixels();
	
        available = false;
        newFrame = true;        
    }	

    /**
     * Resumes the pipeline.
     */
    public void resume()
    {
        gpipe.setState(State.PLAYING);
    }

    /**
     * Stops the pipeline.
     */  
    public void stop()
    {
        gpipe.setState(State.PAUSED);  
    }

    /**
     * @invisible
     */
    protected void invokeEvent(int w, int h, IntBuffer buffer) 
    {
        available = true;  
        pipeWidth = w;
        pipeHeight = h;
        pipePixels = buffer;
        //parent.println("getting frame: " + w + " " + h);
	  
        // Creates a pipelineEvent.
        if (pipelineEventMethod != null) 
        {
            try 
            {
                pipelineEventMethod.invoke(parent, new Object[] { this });
            } 
            catch (Exception e) 
            {
                System.err.println("error, disabling pipelineEvent() for GSPipeline");
                e.printStackTrace();
                pipelineEventMethod = null;
            }
        }
    }

    /**
     * @invisible
     */
    public void dispose() 
    {
    }

    /**
     * @invisible
     */
    Method pipelineEventMethod;

    /**
     * @invisible
     */
    boolean available;

    /**
     * @invisible
     */
    boolean firstFrame = true;
  
    /**
     * @invisible
     */
    int pipeWidth;

    /**
     * @invisible
     */
    int pipeHeight;

    /**
     * @invisible
     */
    IntBuffer pipePixels;
  
    /**
     * @invisible
     */
    Pipeline gpipe;

    /**
     * @invisible
     */    
	RGBDataSink videoSink;
	
    /**
     * @invisible
     */
    protected boolean newFrame;	
}
