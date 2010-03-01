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

import org.gstreamer.*;

import processing.core.PApplet;
import processing.core.PConstants;

import java.io.File;

/**
 * This class contains some basic functions used by the rest of the classes in this library.
 */
public class GSVideo implements PConstants
{	
    public static void init()
    {
        if (INSTANCES_COUNT == 0) 
        {   
        	if (forceGlobalGstreamer) useGlobalGstreamer = true;
        	else lookForGlobalGStreamer();

        	if (PApplet.platform == LINUX) setLinuxPath();
        	else if (PApplet.platform == WINDOWS) setWindowsPath();			        	
        	else if (PApplet.platform == MACOSX) setMacOSXPath();

		    if (!gstreamerBinPath.equals(""))
		    {
                System.setProperty("jna.library.path", gstreamerBinPath);
		    }
		    		    
		    if ((PApplet.platform == WINDOWS) && !useGlobalGstreamer)
		    {
	    	    JnaLibLoaderChain loader = JnaLibLoaderChain.getInstance();
		    }

		    if ((PApplet.platform == MACOSX) && !useGlobalGstreamer)
		    {		    	
	    	    JnaLibLoaderChain loader = JnaLibLoaderChain.getInstance();
		    }
		    
            String[] args = { "" };
            Gst.setUseDefaultContext(useGStreamerDefaultContext);
            Gst.init("GSVideo", args);

		    if (!gstreamerPluginsPath.equals(""))
            {
        	    Registry reg = Registry.getDefault();
        	    boolean res;
        	    res = reg.scanPath(gstreamerPluginsPath);
        	    if (!res) System.err.println("Cannot load GStreamer plugins from " + gstreamerPluginsPath);
            }
        }
        INSTANCES_COUNT++;	    
    }
	
    public static void lookForGlobalGStreamer()
    {
    	if (PApplet.platform == LINUX)
    	{
    		File libPath = new File("/usr/lib");
    		
    		String[] files = libPath.list();
    		for (int i = 0; i < files.length; i++)
    		{
    		    if (-1 < files[i].indexOf("libgstreamer"))
    		    {
    		        useGlobalGstreamer = true;
    		        return;
    		    }
    		}
    		useGlobalGstreamer = false;
    	}
    	else 
    	{
    	    String gst_plugin_path = System.getenv("GST_PLUGIN_PATH");
    	    useGlobalGstreamer = gst_plugin_path != null && !gst_plugin_path.equals("");
        }
    }

	public static void setLinuxPath()
	{
		if (useGlobalGstreamer)
		{
		    gstreamerBinPath = "";
		    gstreamerPluginsPath = "";	
		}
		else
		{
		    LibraryPath libPath = new LibraryPath();
		
	        String path = libPath.get();

		    gstreamerBinPath = path + "/gstreamer/linux";
		    gstreamerPluginsPath = path + "/gstreamer/linux/plugins";
		}
    }	
        
	public static void setWindowsPath()
	{
		if (useGlobalGstreamer)
		{
		    gstreamerBinPath = "";
		    gstreamerPluginsPath = "";	
		}
		else
		{
	        LibraryPath libPath = new LibraryPath();
			
     	    String path = libPath.get();
	    
		    gstreamerBinPath = path + "\\gstreamer\\win";
		    gstreamerPluginsPath = path + "\\gstreamer\\win\\plugins";	
		}
    }	
        
	public static void setMacOSXPath()
	{
		if (useGlobalGstreamer)
		{
		    gstreamerBinPath = "/opt/local/lib:/System/Library/GStreamer/lib";
		    gstreamerPluginsPath = "";	
		}
		else
		{
		    LibraryPath libPath = new LibraryPath();
		
	        String path = libPath.get();

		    gstreamerBinPath = path + "/gstreamer/macosx";
		    gstreamerPluginsPath = path + "/macosx/gstreamer/plugins";
		}
    }	
	
	protected static long INSTANCES_COUNT = 0;
	
	protected static String gstreamerBinPath = "";
    protected static String gstreamerPluginsPath = "";
    
    public static boolean useGStreamerDefaultContext = false;
    public static boolean forceGlobalGstreamer = false;
    public static boolean useGlobalGstreamer = false;    
 }
