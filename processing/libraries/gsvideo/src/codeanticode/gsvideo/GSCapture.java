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
import org.gstreamer.*;
import org.gstreamer.elements.*;
import org.gstreamer.interfaces.PropertyProbe;
import org.gstreamer.interfaces.Property;

/**
 * Class for storing and manipulating video frames from an attached capture device such as a camera.
 */
public class GSCapture extends PImage implements PConstants 
{
    public GSCapture(PApplet parent, int requestWidth, int requestHeight)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        initOS(requestWidth, requestHeight, new String[] { }, new int[] { }, new String[] { }, new String[] { }, 25);	
    }

    public GSCapture(PApplet parent, int requestWidth, int requestHeight, int frameRate)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        initOS(requestWidth, requestHeight, new String[] { }, new int[] { }, new String[] { }, new String[] { }, frameRate);	
    }
        
    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String cameraName)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        if (cameraName != null && !cameraName.equals(""))
            initOS(requestWidth, requestHeight, new String[] { }, new int[] { }, new String[]{devicePropertyName()}, new String[]{cameraName}, 25);
        else
        	initOS(requestWidth, requestHeight, new String[] { }, new int[] { }, new String[]{ }, new String[]{ }, 25);
    }	

    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String sourceName, String cameraName)
    {   
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        if (cameraName != null && !cameraName.equals(""))
            init(requestWidth, requestHeight, sourceName, new String[] { }, new int[] { }, new String[]{devicePropertyName()}, new String[]{cameraName}, 25, false);
        else
      	    init(requestWidth, requestHeight, sourceName, new String[] { }, new int[] { }, new String[]{ }, new String[]{ }, 25, false);
    }	
    
    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String cameraName, int frameRate)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        if (cameraName != null && !cameraName.equals(""))
            initOS(requestWidth, requestHeight, new String[] { }, new int[] { }, new String[]{devicePropertyName()}, new String[]{cameraName}, frameRate);
        else
        	initOS(requestWidth, requestHeight, new String[] { }, new int[] { }, new String[]{ }, new String[]{ }, frameRate);
    }	
    
    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String sourceName, String cameraName, int frameRate)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        if (cameraName != null && !cameraName.equals(""))
            init(requestWidth, requestHeight, sourceName, new String[] { }, new int[] { }, new String[]{devicePropertyName()}, new String[]{cameraName}, frameRate, false);
        else
      	   init(requestWidth, requestHeight, sourceName, new String[] { }, new int[] { }, new String[]{ }, new String[]{ }, frameRate, false);        
    }	    
    
    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String[] intPropNames, int[] intPropValues, String[] strPropNames, String[] strPropValues, int frameRate)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        initOS(requestWidth, requestHeight, intPropNames, intPropValues, strPropNames, strPropValues, frameRate);
    }	

    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String sourceName, String[] intPropNames, int[] intPropValues, String[] strPropNames, String[] strPropValues, int frameRate)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        init(requestWidth, requestHeight, sourceName, intPropNames, intPropValues, strPropNames, strPropValues, frameRate, false);
    }

    public GSCapture(PApplet parent, int requestWidth, int requestHeight, String sourceName, String[] intPropNames, int[] intPropValues, String[] strPropNames, String[] strPropValues, int frameRate, boolean addDecoder)
    {
        super(requestWidth, requestHeight, RGB);
        this.parent = parent;
        init(requestWidth, requestHeight, sourceName, intPropNames, intPropValues, strPropNames, strPropValues, frameRate, addDecoder);
    }
    
    
    /**
     * Reads the current video frame.
     */
    public void read() 
    {
        if (firstFrame) 
        {
            //super.init(captureWidth, captureHeight, RGB);
            loadPixels();
            firstFrame = false;
        }		
        capturePixels.get(pixels);
        updatePixels();
	
        available = false;
        newFrame = true;
    }
    
    static public String[] list()
    {
        if (PApplet.platform == LINUX) return list("v4l2src");
        else if (PApplet.platform == WINDOWS) return list("ksvideosrc");
        else if (PApplet.platform == MACOSX) return list("osxvideosrc");
        
        else return null;
    }
    
    /**
     * Get a list of all available captures as a String array.
     * i.e. println(Capture.list()) will show you the goodies.
     * @param sourceName String 
     * @return String[]
     */  
    static public String[] list(String sourceName) 
    {
    	GSVideo.init();
        String[] deviceListing = new String[0];	
        Element videoSource = ElementFactory.make(sourceName, "Source");
        PropertyProbe probe = PropertyProbe.wrap(videoSource);
        if (probe != null)
        {
        	Property property = probe.getProperty(devicePropertyName());
        	if (property != null)
        	{
    	        Object[] values = probe.getValues(property);
    	        if (values != null)
    	        {
        	        deviceListing = new String[values.length];	
    	            for (int i = 0; i < values.length; i++)
                        if (values[i] instanceof String)
                            deviceListing[i] = (String)values[i];
    	        }
        	}
        }
        return deviceListing;
    }
 
    /**
     * Set the frameRate for how quickly new frames are read
     * from the capture device.
     * NOT IMPLEMENTED.
     * @param iframeRate int
     */
    public void frameRate(int iframeRate) 
    {
    }
 
    /**
     * Returns "true" when a new video frame is available to read.
     * @return boolean
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
     * Resumes the capture pipeline.
     */
    public void resume()
    {
        gpipe.setState(State.PLAYING);
    }

    /**
     * Stops the capture pipeline.
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
        captureWidth = w;
        captureHeight = h;
        capturePixels = buffer;
	  
        // Creates a movieEvent.
        if (captureEventMethod != null) 
        {
            try 
            {
                captureEventMethod.invoke(parent, new Object[] { this });
            } 
            catch (Exception e) 
            {
                System.err.println("error, disabling captureEvent() for videotestsrc");
                e.printStackTrace();
                captureEventMethod = null;
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
    protected void initOS(int requestWidth, int requestHeight, String[] intPropNames, int[] intPropValues, String[] strPropNames, String[] strPropValues, int frameRate)
    {
        if (PApplet.platform == LINUX) 
        {
            init(requestWidth, requestHeight, "v4l2src", intPropNames, intPropValues, strPropNames, strPropValues, frameRate, false);
        }
        else if (PApplet.platform == WINDOWS) 
        {
            init(requestWidth, requestHeight, "ksvideosrc", intPropNames, intPropValues, strPropNames, strPropValues, frameRate, false);
        	//init(requestWidth, requestHeight, "dshowvideosrc", intPropNames, intPropValues, strPropNames, strPropValues, frameRate);
        }
        else if (PApplet.platform == MACOSX) 
        {
            init(requestWidth, requestHeight, "osxvideosrc", intPropNames, intPropValues, strPropNames, strPropValues, frameRate, false);
        }
	    else 
        {
            parent.die("Error: unrecognized platform.", null);
        }
    }

    /**
     * @invisible
     */
    protected void init(int requestWidth, int requestHeight, String sourceName, String[] intPropNames, int[] intPropValues, String[] strPropNames, String[] strPropValues, int frameRate, boolean addDecoder)
    {
        gpipe = null;
        
        GSVideo.init();

        // register methods
        parent.registerDispose(this);

        try 
        {
            captureEventMethod = parent.getClass().getMethod("captureEvent", new Class[] { GSCapture.class });
        } 
        catch (Exception e) 
        {
            // no such method, or an error.. which is fine, just ignore
        }
	  
        gpipe = new Pipeline("GSCapturePipeline");
	  
        Element videoSource = ElementFactory.make(sourceName, "Source");
        
        if (intPropNames.length != intPropValues.length) 
        {
            parent.die("Error: number of integer property names is different from number of values.", null);
        }
	
        for (int i = 0; i < intPropNames.length; i++) 
        {
            videoSource.set(intPropNames[i], intPropValues[i]);
        }

        if (strPropNames.length != strPropValues.length) 
        {
            parent.die("Error: number of string property names is different from number of values.", null);
        }		
		
        for (int i = 0; i < strPropNames.length; i++) 
        {
            videoSource.set(strPropNames[i], strPropValues[i]);
        }
		        
        // conv is needed for dshowvideosrc to work, but is incompatible with matroxcapture... FIX!
        Element conv = ElementFactory.make("ffmpegcolorspace", "ColorConverter");
	
        Element videofilter = ElementFactory.make("capsfilter", "ColorFilter");
        videofilter.setCaps(new Caps("video/x-raw-rgb, width=" + requestWidth + ", height=" + requestHeight + ", bpp=32, depth=24"));
	
        RGBDataSink videoSink = new RGBDataSink("rgb",
            new RGBDataSink.Listener() 
            {
                public void rgbFrame(int w, int h, IntBuffer buffer) 
                {
                    invokeEvent(w, h, buffer);
                }
            }	
        );
	  
        if (addDecoder)
        {
            Element decoder = ElementFactory.make("decodebin", "Decoder");
            gpipe.addMany(videoSource, decoder, conv, videofilter, videoSink);
            Element.linkMany(videoSource, decoder, conv, videofilter, videoSink);
        }
        else
        {
            gpipe.addMany(videoSource, conv, videofilter, videoSink);
            Element.linkMany(videoSource, conv, videofilter, videoSink);  	
        }
        
        gpipe.setState(State.PLAYING);
        
        newFrame = false;
        
        //Object g = videoSource.get("device-name");
        //PApplet.println(g);
    }

    static protected String devicePropertyName()
    {
        if (PApplet.platform == LINUX) 
        {
            return "device"; //Is this correct?
        }
        else if (PApplet.platform == WINDOWS) 
        {
            return "device-name";
        }
        else if (PApplet.platform == MACOSX) 
        {
            return "device-name";
        }        
	    else 
        {
            return "";
        }
    }
    
    /**
     * @invisible
     */
    protected Method captureEventMethod; 
  
    /**
     * @invisible
     */
    protected boolean available;

    /**
     * @invisible
     */
    protected boolean newFrame;
    
    /**
     * @invisible
     */
    protected boolean firstFrame = true;
  
    /**
     * @invisible
     */
    protected int captureWidth;

    /**
     * @invisible
     */
    protected int captureHeight;

    /**
     * @invisible
     */
    protected IntBuffer capturePixels;

    /**
     * @invisible
     */
    protected Pipeline gpipe;
}
