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

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

// Library loader class by Tal Shalif
public class JnaLibLoaderChain {

    public interface DummyLibrary extends Library {
    }    
    
    private static JnaLibLoaderChain instance;

    static final Object[][] WIN32_DEPENDENCIES = {    	
        { "SDL", new String[] { }, false },    	
        { "glew32", new String[] { }, false },    	 	
        { "iconv", new String[] { }, false },    	
        { "liba52-0", new String[] { }, false },    	    	
        { "libbz2", new String[] { }, false },    	    	
        { "libcairo-2", new String[] { }, false },    		
        { "libdca-0", new String[] { }, false },    		
        { "libdl", new String[] { }, false },    	 	
        { "libdvdcss-2", new String[] { }, false },    	
        { "libdvdnav-4", new String[] { }, false },    	 	
        { "libdvdnavmini-4", new String[] { }, false },    	 	
        { "libdvdread-4", new String[] { }, false },    	 	
        { "libfaac-0", new String[] { }, false },    		
        { "libfaad-2", new String[] { }, false },    		
        { "libfontconfig-1", new String[] { }, false },    	
        { "libfreetype-6", new String[] { }, false },    	
        { "libgcrypt-11", new String[] { }, false },    	
        { "libgnutls-26", new String[] { }, false },    	
        { "libgnutls-extra-26", new String[] { }, false },    	
        { "libgnutls-openssl-26", new String[] { }, false },    	  	
        { "libgnutlsxx-26", new String[] { }, false },    	
        { "libgpg-error-0", new String[] { }, false },    	    	
        { "libjpeg", new String[] { }, false },    	
        { "libmms-0", new String[] { }, false },    	
        { "libmp3lame-0", new String[] { }, false },    	 	
        { "libmpeg2-0", new String[] { }, false },    	    	
        { "libmpeg2convert-0", new String[] { }, false },    		
        { "libneon-27", new String[] { }, false },    	
        { "libnice", new String[] { }, false },    	
        { "libogg-0", new String[] { }, false },    	
        { "liboil-0.3-0", new String[] { }, false },    	
        { "libopenjpeg-2", new String[] { }, false },    	
        { "libpango-1.0-0", new String[] { }, false },    	
        { "libpangocairo-1.0-0", new String[] { }, false },    	
        { "libpangoft2-1.0-0", new String[] { }, false },    	
        { "libpangowin32-1.0-0", new String[] { }, false },    	
        { "libpixman-1-0", new String[] { }, true },    	
        { "libpng12-0", new String[] { }, false },    	
        { "libschroedinger-1.0-0", new String[] { }, false },    	
        { "libsoup-2.4-1", new String[] { }, false },    	
        { "libspeex-1", new String[] { }, false },    	
        { "libspeexdsp-1", new String[] { }, false },    	
        { "libtheora-0", new String[] { }, false },    	
        { "libtheoradec-1", new String[] { }, false },    	
        { "libtheoraenc-1", new String[] { }, false },    	
        { "libvorbis-0", new String[] { }, false },    	
        { "libvorbisenc-2", new String[] { }, false },    	
        { "libvorbisfile-3", new String[] { }, false },    	
        { "libwavpack-1", new String[] { }, false },    	
        { "libx264-67", new String[] { }, false },    	
        { "libxml2-2", new String[] { }, false },    	
        { "pthreadGC2", new String[] { }, false },    	
        { "xvidcore", new String[] { }, false },    	
        { "zlib1", new String[] { }, false },    	

    	{ "gio-2.0", new String[] { }, true },
    	{ "glib-2.0", new String[] { }, true },
    	{ "gmodule-2.0", new String[] { }, true },
    	{ "gobject-2.0", new String[] { }, true },
    	{ "gthread-2.0", new String[] { }, true },
    	
    	{ "gstapp-0.10", new String[] { }, true },
    	{ "gstaudio-0.10", new String[] { }, true },
    	{ "gstbase-0.10", new String[] { }, true },
    	{ "gstcdda-0.10", new String[] { }, true },
    	{ "gstcontroller-0.10", new String[] { }, true },
    	{ "gstdataprotocol-0.10", new String[] { }, true },
    	{ "gstdshow-0.10", new String[] { }, true },
    	{ "gstfarsight-0.10", new String[] { }, true },
    	{ "gstfft-0.10", new String[] { }, true },
    	{ "gstgl-0.10", new String[] { }, true },
    	{ "gstinterfaces-0.10", new String[] { }, true },
    	{ "gstnet-0.10", new String[] { }, true },
    	{ "gstnetbuffer-0.10", new String[] { }, true },
    	{ "gstpbutils-0.10", new String[] { }, true },
    	{ "gstphotography-0.10", new String[] { }, true },
    	{ "gstreamer-0.10", new String[] { }, true },
    	{ "gstriff-0.10", new String[] { }, true },
    	{ "gstrtp-0.10", new String[] { }, true },
    	{ "gstrtsp-0.10", new String[] { }, true },
    	{ "gstsdp-0.10", new String[] { }, true },
    	{ "gsttag-0.10", new String[] { }, true },
    	{ "gstvideo-0.10", new String[] { }, true }
    };
    
    static final Object[][] OSX_DEPENDENCIES = {
        { "gstreamer-0.10", new String[] { }, true },
        { "gstbase-0.10", new String[] { "gstreamer-0.10"  }, true },
        { "gstinterfaces-0.10", new String[] { "gstreamer-0.10" }, true },
        { "gstcontroller-0.10", new String[] { "gstreamer-0.10"  }, true },
        { "gstaudio-0.10", new String[] { "gstbase-0.10" }, true },
        { "gstvideo-0.10", new String[] { "gstbase-0.10" }, true },    	
    };
    
    static final Object[][] DEFAULT_DEPENDENCIES = {
        { "gstreamer-0.10", new String[] { }, true },
        { "gstbase-0.10", new String[] { "gstreamer-0.10"  }, true },
        { "gstinterfaces-0.10", new String[] { "gstreamer-0.10" }, true },
        { "gstcontroller-0.10", new String[] { "gstreamer-0.10"  }, true },
        { "gstaudio-0.10", new String[] { "gstbase-0.10" }, true },
        { "gstvideo-0.10", new String[] { "gstbase-0.10" }, true },
    };
    
    
    static final Object[][] dependencies = Platform.isWindows() ? WIN32_DEPENDENCIES : 
    	                                   Platform.isMac()     ? OSX_DEPENDENCIES : 
	                                	                          DEFAULT_DEPENDENCIES; 
    
    private static final Map<String, Object> loadedMap = new
HashMap<String, Object>();

    private static final int RECURSIVE_LOAD_MAX_DEPTH = 5;

    private JnaLibLoaderChain() {
    }

    private void preLoadLibs() {
        for (Object[] a : dependencies) {
        	load(a[0].toString(), DummyLibrary.class, true, 0, (Boolean)a[2]);
        }
    }

    private String[] findDeps(String name) {

        for (Object[] a : dependencies) {
            if (name.equals(a[0])) {

                return (String[]) a[1];
            }
        }

        return new String[] {}; // library dependancy load chain unspecified - probably client call
    }

    public Object load(String name, Class<?> clazz, boolean reqLib) {
        return load(name, clazz, true, 0, reqLib);
    }

    private Object load(String name, Class<?> clazz, boolean
forceReload, int depth, boolean reqLib) {

        assert depth < RECURSIVE_LOAD_MAX_DEPTH: String.format("recursive max load depth %s has been exceeded", depth);

        Object library = loadedMap.get(name);

        if (null == library || forceReload) {

            //Logger.getAnonymousLogger().info(String.format("%" + ((depth + 1) * 2) + "sloading %s", "->", name));

            try {
                String[] deps = findDeps(name);
                
                for (String lib : deps) {
                    load(lib, DummyLibrary.class, false, depth + 1, reqLib);
                }

                library = loadLibrary(name, clazz, reqLib);

                if (library != null) loadedMap.put(name, library);
            } catch (Exception e) {
            	if (reqLib)
                    throw new RuntimeException(String.format("can not load library %s", name, e));
            	else
            		System.out.println(String.format("can not load library %s", name, e));
            }
        }

        return library;
    }

    private static Object loadLibrary(String name, Class<?> clazz, boolean reqLib) {

        //Logger.getAnonymousLogger().info(String.format("loading %s", name));

        String[] nameFormats;
        nameFormats = Platform.isWindows() ? new String[]{ "lib%s", "lib%s-0", "%s" } : new String[]{ "%s-0", "%s" };
        
        
        UnsatisfiedLinkError linkError = null;

        for (String fmt : nameFormats) {
            try {
            	String s = String.format(fmt, name);
            	//System.out.println("Trying to load library file " + s);
            	Object obj = Native.loadLibrary(s, clazz);
            	//System.out.println("Loaded library " + s + " succesfully!");
                return obj;
            } catch (UnsatisfiedLinkError ex) {
                linkError = ex;
            }
        }
        
    	if (reqLib)
    		throw new UnsatisfiedLinkError(String.format("can't load library %s (%1$s|lib%1$s|lib%1$s-0) with -Djna.library.path=%s. Last error:%s" , name, System.getProperty("jna.library.path"), linkError));
    	else
    	{
    		System.out.println(String.format("can't load library %s (%1$s|lib%1$s|lib%1$s-0) with -Djna.library.path=%s. Last error:%s" , name, System.getProperty("jna.library.path"), linkError));
    		return null;
    	}
    }

    public static synchronized JnaLibLoaderChain getInstance() {

        if (null == instance) {        	
            instance = new JnaLibLoaderChain();
            instance.preLoadLibs();
        }

        return instance;
    }

}
