/**
 * This package provides classes to facilitate the handling of opengl textures, glsl shaders and 
 * off-screen rendering in Processing.
 * @author Andres Colubri
 * @version 0.9.1
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

package codeanticode.glgraphics; 
 
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import java.util.*;

/**
 * @invisible
 * This class that offers some utility methods to set and restore opengl state.
 */
public class GLState
{
    public GLState(GL gl)
    {
        this.gl = gl;

        if (INSTANCES_COUNT == 0) 
        {
        	getVersionNumbers();
            getAvailableExtensions();
        	            
            int[] val = { 0 };
            gl.glGetIntegerv(GL.GL_MAX_COLOR_ATTACHMENTS_EXT, val, 0);
            glMaxColorAttach = val[0];
            
            glu = new GLU();
            FBO = new GLFramebufferObject(gl);

            fboStack = new Stack<GLFramebufferObject>();
            destTexStack = new Stack<GLTexture[]>();
            screenFBO = new GLFramebufferObject(gl, true);
            currentFBO = screenFBO;
            emptyDestTex = new GLTexture[0];
            currentDestTex = emptyDestTex;
            
            singleFBO = false;
        }
        INSTANCES_COUNT++;
    }
  
    public void setOrthographicView(int w, int h)
    {
        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        //gl.glOrtho(0.0, w, 0.0, h, -100.0, +100.0);
        glu.gluOrtho2D(0.0, w, 0.0, h);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void saveView()
    {
        gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
        saveGLMatrices();
    }

    public void restoreView()
    {
        restoreGLMatrices();
        gl.glPopAttrib();
    }
    
    public void saveGLState()
    {
        gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
        saveGLMatrices();
    }      
   
    public void restoreGLState()
    {
        restoreGLMatrices();
        gl.glPopAttrib();
    }
    
    public void saveGLMatrices()
    {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();    	
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        //gl.glMatrixMode(GL.GL_TEXTURE);
        //gl.glPushMatrix();
    }

    public void restoreGLMatrices()
    {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
    	gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();    	

        //gl.glMatrixMode(GL.GL_TEXTURE);
        //gl.glPopMatrix();
    }

	public void clearColorBuffer(int color)
	{
        float r, g, b, a;
        int ir, ig, ib, ia;

        ia = (color >> 24) & 0xff;
        ir = (color >> 16) & 0xff;
        ig = (color >> 8) & 0xff;
        ib = color & 0xff; 

        a = ia / 255.0f;
        r = ir / 255.0f;
        g = ig / 255.0f;
        b = ib / 255.0f;
        
        gl.glClearColor(r, g, b, a);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}
        
    public void clearTex(int glid, int target, int color)
    {
        float r, g, b, a;
        int ir, ig, ib, ia;

        ia = (color >> 24) & 0xff;
        ir = (color >> 16) & 0xff;
        ig = (color >> 8) & 0xff;
        ib = color & 0xff; 

        a = ia / 255.0f;
        r = ir / 255.0f;
        g = ig / 255.0f;
        b = ib / 255.0f;

        pushFramebuffer();
        setFramebuffer(FBO);
        FBO.setDrawBuffer(target, glid);
      
        gl.glClearColor(r, g, b, a);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        popFramebuffer();
    }

	public void paintTex(int glid, int target, int w, int h, int color)
    {
        float r, g, b, a;
        int ir, ig, ib, ia;

        ia = (color >> 24) & 0xff;
        ir = (color >> 16) & 0xff;
        ig = (color >> 8) & 0xff;
        ib = color & 0xff;

        a = ia / 255.0f;
        r = ir / 255.0f;
        g = ig / 255.0f;
        b = ib / 255.0f;

        pushFramebuffer();
        setFramebuffer(FBO);
        FBO.setDrawBuffer(target, glid);

        saveView();
        setOrthographicView(w, h);
        gl.glColor4f(r, g, b, a);
        gl.glBegin(GL.GL_QUADS);
            gl.glVertex2f(0.0f, 0.0f);
            gl.glVertex2f(w, 0.0f);
            gl.glVertex2f(w, h);
            gl.glVertex2f(0.0f, h);
        gl.glEnd();
        restoreView();

        popFramebuffer();
    }

	static public void enableSingleFBO()
	{
	    singleFBO = true;	
	}

	static public void disableSingleFBO()
	{
	    singleFBO = false;	
	}

	static public boolean isSingleFBO()
	{
	    return singleFBO;	
	}
	
	static public void enablePopFramebuffer()
	{
        popFramebufferEnabled = true;
	}

	static public void disablePopFramebuffer()
	{
		popFramebufferEnabled = false;
	}

	static public boolean isPopFramebufferEnabled()
	{
        return popFramebufferEnabled;
	}	
	
	static public void enablePushFramebuffer()
	{
		pushFramebufferEnabled = true;	
	}

	static public void disablePushFramebuffer()
	{
		pushFramebufferEnabled = false;		
	}
	
	static public boolean isPushFramebufferEnabled()
	{
        return pushFramebufferEnabled;
	}
	
	static public void setFramebufferFixed(boolean newVal)
	{
	    framebufferFixed = newVal;
	}
	
	static public boolean isFramebufferFixed()
	{
	    return framebufferFixed;	
	}
	
	static public String getGLVersion()  
	{
	   return glVersion; 
	}
	
	static public int getGLMajor()  
	{
	   return glMajor; 
	}

	static public int getGLMinor()  
	{
	   return glMinor; 
	}

	static public String getGLSLVersion()  
	{
	    return glslVersion;
	}

    protected void pushFramebuffer()
    {
    	if (pushFramebufferEnabled) fboStack.push(currentFBO);
    }
        
    protected void setFramebuffer(GLFramebufferObject fbo)
    {
    	if (framebufferFixed) return;
    	
        currentFBO = fbo;
        currentFBO.bind();
    }

    protected void popFramebuffer()
    {
    	if (!popFramebufferEnabled) return;
    		
        try {
        	currentFBO = fboStack.pop();
        	currentFBO.bind();
        } catch (EmptyStackException e) {
            System.out.println("Empty framebuffer stack");
        }
    }
    
    protected void pushDestTextures()
    {
    	destTexStack.push(currentDestTex);    	
    }

    protected void setDestTexture(GLTexture destTex)
    {   
    	setDestTextures(new GLTexture[]{ destTex }, 1);
    }
        
    protected void setDestTextures(GLTexture[] destTex)
    {   
    	setDestTextures(destTex, destTex.length);
    }

    protected void setDestTextures(GLTexture[] destTex, int n)
    {   
    	currentDestTex = destTex;
    	currentFBO.setDrawBuffers(currentDestTex, n);
    }
        
    protected void popDestTextures()
    {
        try {
        	currentDestTex = destTexStack.pop();
        	currentFBO.setDrawBuffers(currentDestTex);
        } catch (EmptyStackException e) {
            System.out.println("Empty texture stack");
        }
    }
    
    protected void getVersionNumbers()
    {
        glVersion = gl.glGetString(GL.GL_VERSION);
        glMajor = Character.getNumericValue(glVersion.charAt(0));
        glMinor = Character.getNumericValue(glVersion.charAt(2));
        glslVersion = gl.glGetString(GL.GL_SHADING_LANGUAGE_VERSION_ARB);    	
    }
    
    protected void getAvailableExtensions()
    {
        // For a complete list of extensions, go to this sections in the openGL registry:
        // http://www.opengl.org/registry/#arbextspecs
        // http://www.opengl.org/registry/#otherextspecs
    	String extensions = gl.glGetString(GL.GL_EXTENSIONS);
            
        if (extensions.indexOf("GL_ARB_multitexture") == -1)
        {
            multiTexAvailable = false;
            System.out.println("GL_ARB_multitexture extension not available");
        }
            
        if (extensions.indexOf("GL_ARB_vertex_buffer_object") == -1)
        {
            vbosAvailable = false;
            System.out.println("GL_ARB_vertex_buffer_object extension not available");
        }            
            
        if (extensions.indexOf("GL_EXT_framebuffer_object") == -1)
        {
        	fbosAvailable = false;
            System.out.println("GL_EXT_framebuffer_object extension not available");
        }
        
        if (extensions.indexOf("GL_ARB_shader_objects") == -1)
        {
            shadersAvailable = false;
            System.out.println("GL_ARB_shader_objects extension not available");
        }
        
        if (extensions.indexOf("GL_EXT_geometry_shader4") == -1)
        {
            geoShadersAvailable = false;
            System.out.println("GL_ARB_geometry_shader4 extension not available");
        }
        
        if (extensions.indexOf("GL_ARB_vertex_shader") == -1)
        {
            vertShadersAvailable = false;
            System.out.println("GL_ARB_vertex_shader extension not available");
        }
        
        if (extensions.indexOf("GL_ARB_fragment_shader") == -1)
        {
        	fragShadersAvailable = false;
            System.out.println("GL_ARB_fragment_shader extension not available");
        }
        
        if (extensions.indexOf("GL_ARB_shading_language_100") == -1)
        {
            glsl100Available = false;
            System.out.println("GL_ARB_shading_language_100 extension not available");
        }

        if (extensions.indexOf("GL_ARB_texture_float") == -1)
        {
            floatTexAvailable = false;
            System.out.println("GL_ARB_texture_float extension not available");
        }           
        
        if (extensions.indexOf("GL_ARB_texture_non_power_of_two") == -1)
        {
            nonTwoPowTexAvailable = false;
            System.out.println("GL_ARB_texture_non_power_of_two extension not available");
        }
    }
    
    public static boolean multiTexAvailable = true;
    public static boolean vbosAvailable = true;
    public static boolean fbosAvailable = true;
    public static boolean shadersAvailable = true;
    public static boolean geoShadersAvailable = true;
    public static boolean vertShadersAvailable = true;
    public static boolean fragShadersAvailable = true;
    public static boolean glsl100Available = true;
    public static boolean floatTexAvailable = true;
    public static boolean nonTwoPowTexAvailable = true;
    
    public static GLU glu;
    public GL gl;
    protected static long INSTANCES_COUNT = 0;
    
    protected static boolean singleFBO;
    protected static boolean popFramebufferEnabled = true;
    protected static boolean pushFramebufferEnabled = true;
    protected static boolean framebufferFixed = false;
    
    protected static String glVersion;
    protected static int glMajor;
    protected static int glMinor;   
    protected static String glslVersion;
    protected static int glMaxColorAttach;

    protected static GLFramebufferObject FBO;
    protected static GLFramebufferObject currentFBO;    
    protected static GLFramebufferObject screenFBO;
    protected static GLTexture[] currentDestTex;
    protected static GLTexture[] emptyDestTex;
    protected static Stack<GLFramebufferObject> fboStack;
    protected static Stack<GLTexture[]> destTexStack;    
}
