/**
 * This package provides classes to facilitate the handling of opengl textures, glsl shaders and 
 * off-screen rendering in Processing.
 * @author Andres Colubri
 * @author Ilias Bergstrom (http://www.onar3d.com/): Multisampled FBO option.
 * 
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
import processing.opengl.*;
import javax.media.opengl.*;

/**
 * This class implements an opengl renderer for off-screen rendering to texture.
 */
public class GLGraphicsOffScreen extends PGraphicsOpenGL implements GLConstants 
{  
    /**
     * Creates an instance of GLGraphicsOffScreen with specified size, with mulisampling disabled.
     * @param parent PApplet
     * @param iwidth int
     * @param iheight int 
     */
    public GLGraphicsOffScreen(PApplet parent, int iwidth, int iheight) 
    {    
        super();
        multisampleEnabled = false;
        colorTexParams = new GLTextureParameters();
        multisampleLevel = 4;    
        setParent(parent);
        setSize(iwidth, iheight);
    }

    /**
     * Creates an instance of GLGraphicsOffScreen with specified size and enabled/disabled 
     * multisampling, according to the value of the argument multi. If multisampling is enabled
     * the multisampling level is set by default to 4x.
     * @param parent PApplet
     * @param iwidth int
     * @param iheight int
     * @param multi int 
     */    
    public GLGraphicsOffScreen(PApplet parent, int iwidth, int iheight, boolean multi) 
    {    
        super();
        multisampleEnabled = multi;
        colorTexParams = new GLTextureParameters();
        multisampleLevel = 4;    	
        setParent(parent);
        setSize(iwidth, iheight);
    }
  
    /**
     * Creates an instance of GLGraphicsOffScreen with specified size and enabled/disabled 
     * multisampling and multisampling level, according to the values of the arguments multi
     * and level.
     * @param parent PApplet
     * @param iwidth int
     * @param iheight int
     * @param multi int
     * @param level int 
     */        
    public GLGraphicsOffScreen(PApplet parent, int iwidth, int iheight, boolean multi, int level) 
    {    
	    super();
        multisampleEnabled = multi;
	    colorTexParams = new GLTextureParameters();
        multisampleLevel = level;	
	    setParent(parent);
	    setSize(iwidth, iheight);
    }
    
    /**
     * Creates an instance of GLGraphicsOffScreen with specified size and parameters for the
     * destination texture. Multisampling is disabled.
     * @param parent PApplet
     * @param iwidth int
     * @param iheight int
     * @param params GLTextureParameters
     */    
    public GLGraphicsOffScreen(PApplet parent, int iwidth, int iheight, GLTextureParameters params) 
    {    
        super();
        multisampleEnabled = false;
        colorTexParams = params;
        multisampleLevel = 4;    
        setParent(parent);
        setSize(iwidth, iheight);
    }

    /**
     * Creates an instance of GLGraphicsOffScreen with specified size and parameters for the
     * destination texture. Multisampling is enabled/disabled according to the value of the 
     * argument multi. If multisampling is enabled, the multisampling level is set by default to 4x.
     * @param parent PApplet
     * @param iwidth int
     * @param iheight int
     * @param params GLTextureParameters
     * @param multi int 
     */      
    public GLGraphicsOffScreen(PApplet parent, int iwidth, int iheight, GLTextureParameters params, boolean multi) 
    {    
        super();
        multisampleEnabled = multi;
        colorTexParams = params;
        multisampleLevel = 4;      
        setParent(parent);
        setSize(iwidth, iheight);
    }

    /**
     * Creates an instance of GLGraphicsOffScreen with specified size and parameters for the
     * destination texture. Multisampling is enabled/disabled according to the value of the 
     * argument multi, and its level is contolled by the argument level.
     * @param parent PApplet
     * @param iwidth int
     * @param iheight int
     * @param params GLTextureParameters
     * @param multi int 
     * @param level int 
     */    
    public GLGraphicsOffScreen(PApplet parent, int iwidth, int iheight, GLTextureParameters params, boolean multi, int level) 
    {    
        super();
	    multisampleEnabled = multi;
        colorTexParams = params;
        multisampleLevel = level;     
        setParent(parent);
        setSize(iwidth, iheight);
    }
  
    /**
     * Returns the texture where this offscreen renderer draws to.
     * @return GLTexture
     */    
    public GLTexture getTexture() 
    {
        return colorTex;
    }  
   
    /**
     * Returns the OpenGL capabilities of the drawable associated to this renderer.
     * @return GLCapabilities
     */  
    public GLCapabilities getCapabilities()
    {
        return glcaps; 
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
     * Creates a new context for drawable that is shared with the context of GLGraphics.
     * @param GLDrawable drawable
     */  
    public void shareContext(GLDrawable drawable)
    {
	    if (context != null) drawable.createContext(context);  	  
    }
    
    /**
     * Any drawing operation with this rendered must be enclosed between a call to beginDraw()
     * and endDraw().
     */      
    public void beginDraw() 
    {
        //System.out.println("  Begin Draw");  
        if ((parent.width != width) || (parent.height != height)) 
        {
            // Saving current viewport.
            gl.glPushAttrib(GL.GL_VIEWPORT_BIT);

            // Saving current projection matrix.
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPushMatrix();
 
            // Setting the appropriate viewport.
            gl.glViewport(0, 0, width, height);
        }

        super.beginDraw();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glScalef(1.0f, -1.0f, 1.0f);
	    
        // Directing rendering to the texture...
        if (multisampleEnabled)
        {
        	glstate.pushFramebuffer();
        	glstate.setFramebuffer(multisampleFBO);
            gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
        }
        else
        {
            glstate.pushFramebuffer();
            glstate.setFramebuffer(FBO);
            FBO.setDrawBuffer(colorTex);
        }
	    
        // Clearing Z-buffer to ensure that the new elements are drawn properly.
	    gl.glClearColor(0f, 0f, 0f, 0.0f);
	    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);	    
    }

    /**
     * Any drawing operation with this rendered must be enclosed between a call to beginDraw()
     * and endDraw().
     */
    public void endDraw() 
    {
        super.endDraw();

        // Restoring render to previous framebuffer.        
        if (multisampleEnabled) 
	    {
            // Then downsample the multisampled to the normal buffer with a blit
            gl.glBindFramebufferEXT(GL.GL_READ_FRAMEBUFFER_EXT, multisampleFBO.getFramebufferID()); // source
            gl.glBindFramebufferEXT(GL.GL_DRAW_FRAMEBUFFER_EXT, FBO.getFramebufferID()); // dest
            gl.glBlitFramebufferEXT(0, 0, width, height, 0, 0, width, height, GL.GL_COLOR_BUFFER_BIT, GL.GL_NEAREST);
            glstate.popFramebuffer();
        }
        else 
        {	
            glstate.popFramebuffer();        	
        }
	        
        if ((parent.width != width) || (parent.height != height)) 
	    {       
            // Restoring previous GL matrices.
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPopMatrix();
	   
            // Restoring previous viewport.
            gl.glPopAttrib();
        }
	    
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
	    
        //System.out.println("  End Draw");
    }
    
    //Setup offscreen framebuffer. 
    protected void initFramebuffer() 
    {
        int stat;

        gl = context.getGL();    
	
        glstate = new GLState(gl);

        // Creating arrays for FBO and depth&stencil buffer.
        FBO = new GLFramebufferObject(gl);
        depthStencilBuffer = new int[1];
        depthStencilBuffer[0] = 0;

        // Create depth and stencil buffers.
        gl.glGenRenderbuffersEXT(1, depthStencilBuffer, 0);
    
        // Creating color texture.    
        colorTex = new GLTexture(parent, width, height, colorTexParams);
        colorTex.clear(0, 0);		
    
        if (multisampleEnabled) 
        {
            multisampleFBO = new GLFramebufferObject(gl);
            
            colorBufferMulti = new int[1];
            colorBufferMulti[0] = 0;
        	        	
        	// Creating handle for multisampled color buffer.
            gl.glGenRenderbuffersEXT(1, colorBufferMulti, 0);
            gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, colorBufferMulti[0]); // Binding render buffer
            gl.glRenderbufferStorageMultisampleEXT(GL.GL_RENDERBUFFER_EXT, multisampleLevel, GL.GL_RGBA8, width, height);

            // Creating handle for multisampled depth buffer.
            gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthStencilBuffer[0]); // Binding depth buffer
            // Allocating space for multisampled depth buffer
            gl.glRenderbufferStorageMultisampleEXT(GL.GL_RENDERBUFFER_EXT, multisampleLevel, GL_DEPTH24_STENCIL8, width, height);
		
            // Creating handle for multisampled FBO
            glstate.pushFramebuffer();
            glstate.setFramebuffer(multisampleFBO);
		
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_RENDERBUFFER_EXT, colorBufferMulti[0]);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, depthStencilBuffer[0]);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_STENCIL_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, depthStencilBuffer[0]);
		
            glstate.pushFramebuffer();
            glstate.setFramebuffer(FBO);
	    
            gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, colorTex.getTextureID(), 0);
	    
            stat = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
            GLUtils.printFramebufferError(stat);	
	    	    
            glstate.popFramebuffer();
            glstate.popFramebuffer();    	
        }
        else 
        {
        	/// Regular, no multisampling, FBO setup.
            gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthStencilBuffer[0]);
            gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL_DEPTH24_STENCIL8, width, height);
	    
            glstate.pushFramebuffer();
            glstate.setFramebuffer(FBO);
	    
            gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, colorTex.getTextureTarget(), colorTex.getTextureID(), 0);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, depthStencilBuffer[0]);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_STENCIL_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, depthStencilBuffer[0]);
	    
            FBO.checkFBO();
	    	
            glstate.popFramebuffer();
        }    
    }

    protected void allocate() 
    {  
        if (glstate == null) 
        {
            PGraphicsOpenGL pgl = (PGraphicsOpenGL)parent.g;
            if (pgl instanceof GLGraphics)
            {	
                glcaps = ((GLGraphics)pgl).getCapabilities();
            }
            else
            {
                glcaps = null;
            }
            context = pgl.getContext();
            drawable = null;

            initFramebuffer();
            settingsInited = false;
        } 
        else reapplySettings();
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

                        //System.out.println(a[U] + " " + a[V] + " " + uscale + " " + vscale);
                        //System.out.println(ar + " " + ag + " " + ab + " " + a[A]);
                        //ar = ag = ab = 1;
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
    
    // Framebuffer object and textures needed for the color, depth and stencil buffers.
    protected GLFramebufferObject multisampleFBO;
    protected GLFramebufferObject FBO;
    protected int[] colorBufferMulti;
    protected int[] depthStencilBuffer;  
    protected GLTexture colorTex;
    protected GLState glstate;
    protected GLCapabilities glcaps;

    protected int multisampleLevel = 4;
    protected boolean multisampleEnabled;
    protected GLTextureParameters colorTexParams;  
}
