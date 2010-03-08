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

import java.util.ArrayList;

import processing.core.*;
import processing.opengl.*;
import javax.media.opengl.*;

/**
 * This class makes the OpenGL renderer compatible with the GLTexture object.
 */
public class GLGraphics extends PGraphicsOpenGL implements GLConstants 
{
    /**
     * Default constructor.
     */	
    public GLGraphics() 
    {
        super();        
    }

    /**
     * Adds a GLWindow to be handled by this renderer.
     * @param win GLWindow
     */
    public void addWindow(GLWindow win)
    {
    	if (windowsList == null) windowsList = new ArrayList<GLWindow>();
    	windowsList.add(win);
    }
    
    /**
     * Returns the OpenGL capabilities of the drawable associated to this renderer.
     * @return GLCapabilities
     */  
    public GLCapabilities getCapabilities()
    {
      return drawable.getChosenGLCapabilities(); 
    }
  
    /**
     * Creates a new context for canvas that is shared with the context of GLGraphics.
     * @param GLCanvas canvas
     */
    public void shareContext(GLCanvas canvas)
    {
	    if (context != null) canvas.createContext(context);  	  
    }

    /**
     * Adds the additional check for all the GL windows.
     */
    public boolean canDraw()
    {
    	if (!allWindowsReady()) return false;
        return super.canDraw();
    }
    
    /**
     * Creates a new context for drawable that is shared with the context of GLGraphics.
     * @param GLDrawable drawable
     */  
    public void shareContext(GLDrawable drawable)
    {
	    if (context != null) drawable.createContext(context);  	  
    }
     
    public void endDraw() 
    {
    	super.endDraw(); // The openGL context of the renderer is released here...
    	renderWindows(); // ...this is important since the windows have their own contexts
        // that share data with the main context.
    	
    }
    
    protected void renderTriangles(int start, int stop) 
    {
        report("render_triangles in");

        for (int i = start; i < stop; i++) 
        {
            float a[] = vertices[triangles[i][VERTEX1]];
            float b[] = vertices[triangles[i][VERTEX2]];
            float c[] = vertices[triangles[i][VERTEX3]];

            // This is only true when not textured.
            // We really should pass specular straight through to triangle rendering.
            float ar = clamp(triangleColors[i][0][TRI_DIFFUSE_R] + triangleColors[i][0][TRI_SPECULAR_R]);
            float ag = clamp(triangleColors[i][0][TRI_DIFFUSE_G] + triangleColors[i][0][TRI_SPECULAR_G]);
            float ab = clamp(triangleColors[i][0][TRI_DIFFUSE_B] + triangleColors[i][0][TRI_SPECULAR_B]);
            float br = clamp(triangleColors[i][1][TRI_DIFFUSE_R] + triangleColors[i][1][TRI_SPECULAR_R]);
            float bg = clamp(triangleColors[i][1][TRI_DIFFUSE_G] + triangleColors[i][1][TRI_SPECULAR_G]);
            float bb = clamp(triangleColors[i][1][TRI_DIFFUSE_B] + triangleColors[i][1][TRI_SPECULAR_B]);
            float cr = clamp(triangleColors[i][2][TRI_DIFFUSE_R] + triangleColors[i][2][TRI_SPECULAR_R]);
            float cg = clamp(triangleColors[i][2][TRI_DIFFUSE_G] + triangleColors[i][2][TRI_SPECULAR_G]);
            float cb = clamp(triangleColors[i][2][TRI_DIFFUSE_B] + triangleColors[i][2][TRI_SPECULAR_B]);

            int textureIndex = triangles[i][TEXTURE_INDEX];
            if (textureIndex != -1)
            {
                report("before enable");
                gl.glEnable(GL.GL_TEXTURE_2D);
                report("after enable");

	            float uscale = 1.0f;
	            float vscale = 1.0f;
 		
                PImage texture = textures[textureIndex];
	            if (texture instanceof GLTexture)
	            {
	                GLTexture tex = (GLTexture)texture;
  	                gl.glBindTexture(tex.getTextureTarget(), tex.getTextureID());
			
	                uscale *= tex.getMaxTextureCoordS();
	                vscale *= tex.getMaxTextureCoordT();

	                float cx = 0.0f;
	                float sx = +1.0f;
	                if (tex.isFlippedX())
	                {
	                    cx = 1.0f;			
	                    sx = -1.0f;
	                }

	                float cy = 0.0f;
	                float sy = +1.0f;
	                if (tex.isFlippedY())
                    {
                        cy = 1.0f;			
	                    sy = -1.0f;
	                }

                    gl.glBegin(GL.GL_TRIANGLES);

                        gl.glColor4f(ar, ag, ab, a[A]);
                        gl.glTexCoord2f((cx +  sx * a[U]) * uscale, (cy +  sy * a[V]) * vscale);		
	                    gl.glNormal3f(a[NX], a[NY], a[NZ]);
	                    gl.glEdgeFlag(a[EDGE] == 1);
	                    gl.glVertex3f(a[VX], a[VY], a[VZ]);

	                    gl.glColor4f(br, bg, bb, b[A]);
	                    gl.glTexCoord2f((cx +  sx * b[U]) * uscale, (cy +  sy * b[V]) * vscale);
	                    gl.glNormal3f(b[NX], b[NY], b[NZ]);
	                    gl.glEdgeFlag(a[EDGE] == 1);
	                    gl.glVertex3f(b[VX], b[VY], b[VZ]);

	                    gl.glColor4f(cr, cg, cb, c[A]);
	                    gl.glTexCoord2f((cx +  sx * c[U]) * uscale, (cy +  sy * c[V]) * vscale);			
	                    gl.glNormal3f(c[NX], c[NY], c[NZ]);
	                    gl.glEdgeFlag(a[EDGE] == 1);
                        gl.glVertex3f(c[VX], c[VY], c[VZ]);
			
                   gl.glEnd();
      	  
	               gl.glBindTexture(tex.getTextureTarget(), 0);
                } 
	            else 
                {
	                // Default texturing using a PImage.

                    report("before bind");
                    bindTexture(texture);
                    report("after bind");

                    ImageCache cash = (ImageCache) texture.getCache(this);
                    uscale = (float) texture.width / (float) cash.twidth;
                    vscale = (float) texture.height / (float) cash.theight;

	                gl.glBegin(GL.GL_TRIANGLES);

                        // System.out.println(a[U] + " " + a[V] + " " + uscale + " " + vscale);
	                    // System.out.println(ar + " " + ag + " " + ab + " " + a[A]);
                        // ar = ag = ab = 1;
                        gl.glColor4f(ar, ag, ab, a[A]);
                        gl.glTexCoord2f(a[U] * uscale, a[V] * vscale);
                        gl.glNormal3f(a[NX], a[NY], a[NZ]);
                        gl.glEdgeFlag(a[EDGE] == 1);
                        gl.glVertex3f(a[VX], a[VY], a[VZ]);

                        gl.glColor4f(br, bg, bb, b[A]);
                        gl.glTexCoord2f(b[U] * uscale, b[V] * vscale);
                        gl.glNormal3f(b[NX], b[NY], b[NZ]);
                        gl.glEdgeFlag(a[EDGE] == 1);
                        gl.glVertex3f(b[VX], b[VY], b[VZ]);

                        gl.glColor4f(cr, cg, cb, c[A]);
                        gl.glTexCoord2f(c[U] * uscale, c[V] * vscale);
                        gl.glNormal3f(c[NX], c[NY], c[NZ]);
	                    gl.glEdgeFlag(a[EDGE] == 1);
                        gl.glVertex3f(c[VX], c[VY], c[VZ]);
                    gl.glEnd();

                    report("non-binding 6");

                    gl.glDisable(GL.GL_TEXTURE_2D);
                }
            } 
            else 
            {  
                // no texture
                gl.glBegin(GL.GL_TRIANGLES);

                    gl.glColor4f(ar, ag, ab, a[A]);
                    gl.glNormal3f(a[NX], a[NY], a[NZ]);
                    gl.glEdgeFlag(a[EDGE] == 1);
                    gl.glVertex3f(a[VX], a[VY], a[VZ]);

                    gl.glColor4f(br, bg, bb, b[A]);
                    gl.glNormal3f(b[NX], b[NY], b[NZ]);
                    gl.glEdgeFlag(a[EDGE] == 1);
                    gl.glVertex3f(b[VX], b[VY], b[VZ]);

                    gl.glColor4f(cr, cg, cb, c[A]);
                    gl.glNormal3f(c[NX], c[NY], c[NZ]);
                    gl.glEdgeFlag(a[EDGE] == 1);
                    gl.glVertex3f(c[VX], c[VY], c[VZ]);

                gl.glEnd();
            }
        }

        report("render_triangles out");
    }
    
    protected boolean allWindowsReady()
    {
    	
    	if (windowsList == null) return true;
    	boolean res = true;    	
        for (int i = 0; i < windowsList.size(); i++)
        {
            GLWindow win = (GLWindow)windowsList.get(i);
            if (!win.ready())
            {
            	res = false;
            	break;
            }
        }
        return res;
    }
    
    protected void renderWindows()
    {
    	if (windowsList == null) return;
        for (int i = 0; i < windowsList.size(); i++)
        {
            GLWindow win = (GLWindow)windowsList.get(i);
            if (!win.getOverride()) win.render();
        }    	
    }
    
    ArrayList<GLWindow> windowsList = null;
}
