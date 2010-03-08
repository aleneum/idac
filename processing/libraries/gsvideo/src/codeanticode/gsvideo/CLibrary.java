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

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * This JNA interface provides access to the environment variable-related functions in the C library.
 * How to use:
 * CLibrary clib = CLibrary.INSTANCE;		    	
 * String s = clib.getenv("DYLD_LIBRARY_PATH");
 */    
public interface CLibrary extends Library {
    CLibrary INSTANCE = (CLibrary)Native.loadLibrary("c", CLibrary.class);
    
    int setenv(String name, String value, int overwrite);
    String getenv(String name);
    int unsetenv(String name);
    int putenv(String string);
}
