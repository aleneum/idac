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
import processing.opengl.*;

import javax.media.opengl.*;

import com.sun.opengl.util.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * This class holds a 3D model composed of vertices, normals, colors (per vertex) and 
 * texture coordinates (also per vertex). All this data is stored in Vertex Buffer Objects
 * (VBO) for fast access. 
 * This is class is still undergoing development, the API will probably change quickly
 * in the following months as features are tested and refined.
 * In particular, with the settings of the VBOs in this first implementation (GL.GL_DYNAMIC_DRAW_ARB)
 * it is assumed that the coordinates will change often during the lifetime of the model.
 * For static models a different VBO setting (GL.GL_STATIC_DRAW_ARB) should be used.
 */
public class GLModel implements PConstants, GLConstants 
{
	public GLModel(PApplet parent, int numVert, int mode, int usage)
	{
        pgl = (PGraphicsOpenGL)parent.g;
        gl = pgl.gl;		
		
        size = numVert;
        
        tintR = tintG = tintB = tintA = 0.0f;
    	pointSize = 1.0f;
    	lineWidth = 1.0f;
    	usingPointSprites = false;        
    	blend = false;
    	blendMode = ADD;
    	
        
        if (mode == POINTS) vertexMode = GL.GL_POINTS;
        else if (mode == POINT_SPRITES)
        {
        	vertexMode = GL.GL_POINTS;
        	usingPointSprites = true;
            float[] tmp = { 0.0f };
            gl.glGetFloatv(GL.GL_POINT_SIZE_MAX_ARB, tmp, 0);
            maxPointSize = tmp[0];
            pointSize = maxPointSize;
            spriteFadeSize = 0.6f * pointSize;
        }
        else if (mode == LINES) vertexMode = GL.GL_LINES;
        else if (mode == LINE_STRIP) vertexMode = GL.GL_LINE_STRIP;
        else if (mode == LINE_LOOP) vertexMode = GL.GL_LINE_LOOP;
        else if (mode == TRIANGLES) vertexMode = GL.GL_TRIANGLES; 
        else if (mode == TRIANGLE_FAN) vertexMode = GL.GL_TRIANGLE_FAN;
        else if (mode == TRIANGLE_STRIP) vertexMode = GL.GL_TRIANGLE_STRIP;  
        else if (mode == QUADS) vertexMode = GL.GL_QUADS;
        else if (mode == QUAD_STRIP) vertexMode = GL.GL_QUAD_STRIP;
        else if (mode == POLYGON) vertexMode = GL.GL_POLYGON;        

        if (usage == STATIC) vboUsage = GL.GL_STATIC_DRAW_ARB;
        else if (usage == DYNAMIC) vboUsage = GL.GL_DYNAMIC_DRAW_ARB;
        else if (usage == STREAM) vboUsage = GL.GL_STREAM_COPY;
        
	    gl.glGenBuffersARB(1, vertCoordsVBO, 0);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vertCoordsVBO[0]);
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, size * 4 * BufferUtil.SIZEOF_FLOAT, null, vboUsage);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	public int getVBO() { return vertCoordsVBO[0]; }
	
	/*
	Everywhere in GLGraphics should be checked the deletion of openGL objects (does JOGL handle
	this automatically), i.e.:
	glDeleteBuffersARB(1, vertCoordsVBO);
	*/
	
	public void initNormals()
	{
		normCoordsVBO = new int[1];
	    gl.glGenBuffersARB(1, normCoordsVBO, 0);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, normCoordsVBO[0]);
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, size * 4 * BufferUtil.SIZEOF_FLOAT, null, vboUsage);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}	
		
	public void initColors()
	{
		colorsVBO = new int[1];
	    gl.glGenBuffersARB(1, colorsVBO, 0);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, colorsVBO[0]);
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, size * 4 * BufferUtil.SIZEOF_FLOAT, null, vboUsage);
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	public void initTexures(int n)
	{
		numTextures = n;
    			
		texCoordsVBO = new int[numTextures];
		textures = new GLTexture[numTextures];
        gl.glGenBuffersARB(numTextures, texCoordsVBO, 0);
        for (n = 0; n < numTextures; n++)
        {
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, texCoordsVBO[n]); // Bind the buffer.
            gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, size * 2 * BufferUtil.SIZEOF_FLOAT, null, vboUsage);
        }
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);     
	}
	
	public void setTexture(int i, GLTexture tex)
	{
		textures[i] = tex;
	}
	
	public void beginUpdateVertices()
	{
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vertCoordsVBO[0]);
		vertices = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY).asFloatBuffer();
	}
	
	public void endUpdateVertices()
	{
		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	public void updateVertices(float[] vertArray)
	{
		beginUpdateVertices();
		vertices.put(vertArray);
		endUpdateVertices();
	}
	
	public void beginUpdateColors()
	{
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, colorsVBO[0]);
		colors = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY).asFloatBuffer();
	}
	
	public void endUpdateColors()
	{
		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	public void updateColors(float[] colArray)
	{
		beginUpdateColors();
		colors.put(colArray);
		endUpdateColors();
	}
	
	public void beginUpdateTexCoords(int n)
	{
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, texCoordsVBO[n]);
		texCoords = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY).asFloatBuffer();
	}
	
	public void endUpdateTexCoords()
	{
		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	public void updateTexCoords(int n, float[] texCoordsArray)
	{
		beginUpdateTexCoords(n);
		texCoords.put(texCoordsArray);
		endUpdateTexCoords();
	}
	
	public void beginUpdateNormals()
	{
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, normCoordsVBO[0]);
		normals = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY).asFloatBuffer();
	}
	
	public void endUpdateNormals()
	{
		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	public void updateNormals(float[] normArray)
	{
		beginUpdateNormals();
		normals.put(normArray);
		endUpdateNormals();
	}

	public void setLineWidth(float w)
	{
    	lineWidth = w;
    }

	public void setPointSize(float s)
	{
    	pointSize = s;
    }

	public float getMaxPointSize()
	{
    	return maxPointSize;
    }

	public void setSpriteFadeSize(float s)
	{
		spriteFadeSize = s;
    }	
	
    /**
     * Disables blending.
     */    
    public void noBlend()
    {
        blend = false;
    }	
	
    /**
     * Enables blending and sets the mode.
     * @param MODE int
     */    
    public void setBlendMode(int MODE)
    {
        blend = true;
        blendMode = MODE;
    }   	
	
	public void setTint(int color)
	{
        int ir, ig, ib, ia;

        ia = (color >> 24) & 0xff;
        ir = (color >> 16) & 0xff;
        ig = (color >> 8) & 0xff;
        ib = color & 0xff; 

        tintA = ia / 255.0f;
        tintR = ir / 255.0f;
        tintG = ig / 255.0f;
        tintB = ib / 255.0f;
	}
	
	public void render()
	{
	    render(0);
	}
	
	public void render(int first)
	{
	    gl.glColor4f(tintR, tintG, tintB, tintA);
	    gl.glLineWidth(lineWidth);
	    if (usingPointSprites) gl.glPointSize(PApplet.min(pointSize, maxPointSize));
	    else gl.glPointSize(pointSize);
	    
	    // Setting-up blending.
	    blend0 = gl.glIsEnabled(GL.GL_BLEND);
        if (blend) 
        {
            gl.glEnable(GL.GL_BLEND);
            if (blendMode == BLEND) gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            else if (blendMode == ADD) gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            else if (blendMode == MULTIPLY) gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_SRC_COLOR);
            else if (blendMode == SUBTRACT) gl.glBlendFunc(GL.GL_ONE_MINUS_DST_COLOR, GL.GL_ZERO);
//            how to implement all these other blending modes:
//            else if (blendMode == LIGHTEST)
//            else if (blendMode == DIFFERENCE)
//            else if (blendMode == EXCLUSION)
//            else if (blendMode == SCREEN)
//            else if (blendMode == OVERLAY)
//            else if (blendMode == HARD_LIGHT)
//            else if (blendMode == SOFT_LIGHT)
//            else if (blendMode == DODGE)
//            else if (blendMode == BURN)
        }
        
	    if (normCoordsVBO != null)
	    {
	    	gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, normCoordsVBO[0]);
            gl.glNormalPointer(GL.GL_FLOAT, 4 * BufferUtil.SIZEOF_FLOAT, 0);
	    }
	    	    
	    if (colorsVBO != null)
	    {
	    	gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, colorsVBO[0]);
	        gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
	    }

	    if (texCoordsVBO != null)
	    {
	    	gl.glEnable(textures[0].getTextureTarget());
            if (usingPointSprites)
            {
            	// Texturing with point sprites.
            	
                // This is how will our point sprite's size will be modified by 
                // distance from the viewer            	
            	float quadratic[] = {1.0f, 0.0f, 0.01f, 1};
                ByteBuffer temp = ByteBuffer.allocateDirect(16);
                temp.order(ByteOrder.nativeOrder());            	
                gl.glPointParameterfvARB(GL.GL_POINT_DISTANCE_ATTENUATION_ARB, (FloatBuffer) temp.asFloatBuffer().put(quadratic).flip());
                                
                // The alpha of a point is calculated to allow the fading of points 
                // instead of shrinking them past a defined threshold size. The threshold 
                // is defined by GL_POINT_FADE_THRESHOLD_SIZE_ARB and is not clamped to 
                // the minimum and maximum point sizes.
                gl.glPointParameterfARB(GL.GL_POINT_FADE_THRESHOLD_SIZE_ARB, spriteFadeSize);
                gl.glPointParameterfARB(GL.GL_POINT_SIZE_MIN_ARB, 1.0f);
                gl.glPointParameterfARB(GL.GL_POINT_SIZE_MAX_ARB, maxPointSize);

                // Specify point sprite texture coordinate replacement mode for each 
                // texture unit
                gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);

                gl.glEnable(GL.GL_POINT_SPRITE_ARB );
                
                // Binding texture units.
                for (int n = 0; n < numTextures; n++)
                {
                    gl.glClientActiveTexture(GL.GL_TEXTURE0 + n);
                    gl.glBindTexture(GL.GL_TEXTURE_2D, textures[n].getTextureID()); 
                }
            }
            else
            {
            	// Regular texturing.
                gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                for (int n = 0; n < numTextures; n++)
                {
                    gl.glClientActiveTexture(GL.GL_TEXTURE0 + n);
                    gl.glBindTexture(GL.GL_TEXTURE_2D, textures[n].getTextureID());                
                    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, texCoordsVBO[n]);
                    gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
                }
            }
	    }
	    
	    // Drawing the vertices:
	    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
	    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vertCoordsVBO[0]);
	    
	    // The vertices in the array have 4 components, but only 3 are needed to
	    // draw (x, y, z). The first parameters indicates the latter, and then
	    // a stride of 4 * BufferUtil.SIZEOF_FLOAT (which is the memory space each vertex
	    // occupies in memory) allows to jump to the right place in the vertex array
	    // as it is drawn.
	    gl.glVertexPointer(3, GL.GL_FLOAT, 4 * BufferUtil.SIZEOF_FLOAT, 0);
	    
	    gl.glDrawArrays(vertexMode, first, size);
	    gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	    
	    if (texCoordsVBO != null) 
	    {	
	    	if (usingPointSprites)
	    	{
	    		gl.glDisable(GL.GL_POINT_SPRITE_ARB);
	    	}
	    	else
	    	{
	    	    gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	    	}
	    	gl.glDisable(textures[0].getTextureTarget());
	    }
	    if (colorsVBO != null) gl.glDisableClientState(GL.GL_COLOR_ARRAY);
	    if (normCoordsVBO != null) gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
	    
	 // If there was noblending originally
        if (!blend0 && blend) gl.glDisable(GL.GL_BLEND); 
	}	
	
    protected GL gl;	
    protected PGraphicsOpenGL pgl;	
	protected int size;
	protected int vertexMode;
	protected int vboUsage;
	protected int[] vertCoordsVBO = { 0 };

	protected int[] colorsVBO = null;	
	protected int[] normCoordsVBO = null;	
	protected int[] texCoordsVBO = null;

	protected float tintR, tintG, tintB, tintA;
	protected float pointSize;
	protected float maxPointSize;
	protected float lineWidth;
	protected boolean usingPointSprites;
	protected boolean blend;
	protected boolean blend0;
	protected int blendMode;
	protected float spriteFadeSize;
	
	protected int numTextures;
	
	public GLTexture[] textures;
	
	public FloatBuffer vertices;
	public FloatBuffer colors;
	public FloatBuffer normals;
	public FloatBuffer texCoords;
	
    public static final int STATIC = 0;
    public static final int DYNAMIC = 1;
    public static final int STREAM = 2;	
}
