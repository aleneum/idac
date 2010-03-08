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

import java.net.URL;

import com.sun.jna.Platform;

class LibraryPath
{
	// This method returns the folder inside which the gstreamer library folder should be located.
	String get()
	{
	    URL url = this.getClass().getResource("LibraryPath.class");
	    if (url != null)
	    {
	    	// Convert URL to string, taking care of spaces represented by the "%20" string.
	        String path = url.toString().replace("%20", " ");
	        int n0 = path.indexOf('/');
	        
	        int n1 = -1;
	        if (Platform.isWindows())
	        {
	            n1 = path.indexOf("/lib/gsvideo.jar");           // location of gsvideo.jar in exported apps.
	            if (n1 == -1) n1 = path.indexOf("/gsvideo.jar"); // location of gsvideo.jar in library folder.
	            
	        	// In Windows, path string starts with "jar file/C:/..."
	        	// so the substring up to the first / is removed.
	            n0++;
	        }
	        else if (Platform.isMac())
	        {
	        	int n2 = path.indexOf(".app/Contents/");
	        	if (-1 < n2)
	        	{
	        		// Using GSVideo on Mac in an exported application.
	        		// gsvideo.jar is (sohould be) located in a folder with the
	        		// following name:
	        		// ".../<application folder>/<sketch name>.app/Contents/Resources/Java/gsvideo.jar"
	        		// By default <application folder> is "application.macosx", but the user might have
	        		// renamed to something else. So we don't assume that. So, we want to get the index
	        		// of the first letter of the sketch name in path (minus 1). It should be the last
	        		// occurrence of the forward slash in the substring ".../<application folder>/<sketch name>"
	        		// So:
	        		String s = path.substring(0, n2);
                    n1 = s.lastIndexOf('/');
                    if (-1 < n1) n1++;
	        	}
	        	
	        	if (n1 == -1) n1 = path.indexOf("gsvideo.jar"); // location of gsvideo.jar in library folder.
	        }
	        else if (Platform.isLinux())
	        {
	        	// To be done!
	        }

	        if ((-1 < n0) && (-1 < n1)) 
	        {	
	        	return path.substring(n0, n1);
	        }
	        else return "";
	    }
	    return "";
	}
}