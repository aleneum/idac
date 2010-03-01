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

import processing.core.*;
import java.awt.Frame;
import javax.media.opengl.*;

/** 
 * An undecorated ligheweight AWT window to show just a single texture created in the main OpenGL context
 * of the parent PApplet. The renderer in parent must be of type GLGraphics.
 */
public class GLTextureWindow extends GLWindow 
{  
    /**
     * Creates an instance of GLTextureWindow with the specified size (w, h) and position (x, y),
     * which will show texture tex in it.
     * @param parent PApplet
     * @param tex GLTexture
     * @param x int 
     * @param y int
     * @param w int 
     * @param h int
     */	 	
	public GLTextureWindow(PApplet parent, int x, int y, int w, int h)
    {
		this.parent = parent;
		pgl = (GLGraphics)parent.g;
		
		pgl.addWindow(this);
		    
        frame = new Frame("GLTextureWindow frame");
        frame.setSize(w, h);
        frame.setLocation(x, y);
        frame.setUndecorated(true);
        
        // The GL canvas of the AWT window must have exactly the same GL capabilites as the main 
        // renderer for sharing with mainContext to be possible.
        GLContext mainContext = ((GLGraphics)parent.g).getContext();
        GLCapabilities mainCaps = ((GLGraphics)parent.g).getCapabilities();        
        canvas = new GLCanvas(mainCaps, null, mainContext, null);
        
        renderer = new GLRenderer();
        canvas.addGLEventListener(renderer);
  
        frame.add(canvas);
        
        override = false;
        outTex = null;
    }
    
	public void setTexture(GLTexture tex)
	{
        outTex = tex;
	}

    /**
     * Initializes the window.
     */		
	public void init()
    {
        show();
    }

    /**
     * Initializes the window, setting the override property to the desired value.
     * @param override boolean
     */		
	public void init(boolean override)
    {
        show();
        this.override = override;
    }	
	
    /**
     * Shows the window.
     */		
	public void show()
    {
        frame.setVisible(true);
    }

    /**
     * Hides the window.
     */
	public void hide()
    {
		frame.setVisible(false);
    }    
	
	/**
     * Returns whether the window is visible or not.
     * @return boolean
     */	
	public boolean isVisible()
	{
		return frame.isVisible();
	}
	
    /**
     * Sets the texture tint color.
     * @param int color
     */		
	public void tint(int color)
    {
        int ir, ig, ib, ia;

        ia = (color >> 24) & 0xff;
        ir = (color >> 16) & 0xff;
        ig = (color >> 8) & 0xff;
        ib = color & 0xff; 

        renderer.a = ia / 255.0f;
        renderer.r = ir / 255.0f;
        renderer.g = ig / 255.0f;
        renderer.b = ib / 255.0f;
    }
	
	/**
     * Returns true or false depending on whether or not the internal renderer has been
     * initialized.
     * @return boolean
     */	
	public boolean ready()
	{
        return renderer.initalized;
	}
	
    /**
     * Draws the window, if the renderer has been initialized.
     */
	public void render()
	{
        if (renderer.initalized)
        {
        	renderer.started = true;
        	canvas.display();
        }
	}
	
    protected class GLRenderer implements GLEventListener 
    {  
        public GLRenderer()
        {
            super();
            initalized = false;
            started = false;
            r = g = b = a = 1.0f;
        }
 
        public void init(GLAutoDrawable drawable) 
        {    
            gl = drawable.getGL();
            context = drawable.getContext();
  
            gl.glClearColor(0, 0, 0, 0);
            initalized = true;
        }
        
        public void display(GLAutoDrawable drawable) 
        {
            if (!initalized || !started || (outTex == null)) return;
            
        	int w = drawable.getWidth();
            int h = drawable.getHeight();
            
            gl = drawable.getGL();
            context = drawable.getContext();
            detainContext();
            
            // Setting orthographics view to display the texture.  
            gl.glViewport(0, 0, w, h);   
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();      
            gl.glOrtho(0.0, w, 0.0, h, -100.0, +100.0);   
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();    
    
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glEnable(outTex.getTextureTarget());
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(outTex.getTextureTarget(), outTex.getTextureID());
            gl.glColor4f(r, g, b, a);
            gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(0.0f, 0.0f);

                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(w, 0.0f);

                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(w, h);

                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(0.0f, h);
            gl.glEnd();
            gl.glBindTexture(outTex.getTextureTarget(), 0);
                
            gl.glFlush();
            
            releaseContext();
        }
   
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
 
        public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
        
        protected void detainContext()
        {
            try 
            {
                while (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) 
                {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }  
        
        protected void releaseContext()
        {
        	context.release();	
        }
        
        float r, g, b, a;
        GL gl;
        GLContext context;
        boolean initalized;
        boolean started;
    }
        
    protected PApplet parent;
    protected GLRenderer renderer;
    protected GLCanvas canvas;
    protected Frame frame;
    protected GLTexture outTex;
    protected GLGraphics pgl;
}
