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

/**
 * @invisible
 * This class provides some utilities functions.
 */
public class GLUtils
{
	
    static public int parsePrimitiveType(String typeStr)
    {
        if (typeStr.equals("points")) return GL.GL_POINTS;
        else if (typeStr.equals("line_strip")) return GL.GL_LINE_STRIP;
        else if (typeStr.equals("line_loop")) return GL.GL_LINE_LOOP;
        else if (typeStr.equals("lines")) return GL.GL_LINES;
        else if (typeStr.equals("triangle_strip")) return GL.GL_TRIANGLE_STRIP;
        else if (typeStr.equals("triangle_fan")) return GL.GL_TRIANGLE_FAN;
        else if (typeStr.equals("triangles")) return GL.GL_TRIANGLES;
        else if (typeStr.equals("quad_strip")) return GL.GL_QUAD_STRIP;
        else if (typeStr.equals("quads")) return GL.GL_QUADS;
        else if (typeStr.equals("polygon")) return GL.GL_POLYGON;
        else
        {
            System.err.println("Unrecognized geometry mode. Using points.");
            return GL.GL_POINTS;
        }
    }
    
    static void printFramebufferError(int status)
    {
        if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT) return;
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT)");
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)");
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)");
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)");
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)");
        }
        else if (status == GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT)");
        }
        else if (status == GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT)
        {
        	System.err.println("Frame buffer is incomplete (GL_FRAMEBUFFER_UNSUPPORTED_EXT)");
        }
        else
        {
        	System.err.println("Frame buffer is incomplete (unknown error code)");
        }    
    }
}
