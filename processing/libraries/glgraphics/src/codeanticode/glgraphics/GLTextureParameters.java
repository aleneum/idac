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

/**
 * This class stores the parameters for a texture: target, internal format, minimization filter
 * and magnification filter. 
 */
public class GLTextureParameters implements GLConstants 
{
    /**
     * Creates an instance of GLTextureParameters, setting all the parameters to default values.
     */
    public GLTextureParameters()
    {
        target = TEX2D;
        format = COLOR;
        minFilter = LINEAR;
        magFilter = LINEAR;		
    }
	
    public GLTextureParameters(int format)
    {
        target = TEX2D;
        this.format = format;
        minFilter = LINEAR;
        magFilter = LINEAR;		
    }

    public GLTextureParameters(int format, int filter)
    {
        target = TEX2D;
        this.format = format;
        minFilter = filter;
        magFilter = filter;		
    }

    /**
     * Texture target.
     */
    public int target;
	
    /**
     * Texture internal format.
     */
    public int format;
	
    /**
     * Texture minimization filter.
     */
    public int minFilter;
	
    /**
     * Texture magnification filter.
     */
    public int magFilter;	
}

