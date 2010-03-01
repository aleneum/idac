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
import com.sun.opengl.util.*;

import java.io.IOException;
import java.net.URL;
import java.nio.*;

// ToDo: 
// Add support for CG shaders (they work on Intel X3100 GPUs). 
// Define an abstract base GLShader from which GLSLShader and GLCGShader class are derived.

/**
 * This class encapsulates a glsl shader. Based in the code by JohnG (http://www.hardcorepawn.com/)
 */
public class GLSLShader
{
    /**
     * Creates an instance of GLSLShader.
     * @param parent PApplet
     */
    public GLSLShader(PApplet parent)
    {
        this.parent = parent;
       
        pgl = (PGraphicsOpenGL)parent.g;
        gl = pgl.gl;
		
        programObject = gl.glCreateProgramObjectARB();
        vertexShader = -1;
        geometryShader = -1;        
        fragmentShader = -1;
    }

    /**
     * Loads and compiles the vertex shader contained in file.
     * @param file String
     */	
    public void loadVertexShader(String file)
    {
        String shaderSource = PApplet.join(parent.loadStrings(file), "\n");
        attachVertexShader(shaderSource, file);
    }

    /**
     * Loads and compiles the vertex shader contained in the URL.
     * @param file String
     */
    public void loadVertexShader(URL url) 
    {
    	String shaderSource;
		try {
			shaderSource = PApplet.join(PApplet.loadStrings(url.openStream()),"\n");
			attachVertexShader(shaderSource, url.getFile());
		} catch (IOException e) {
			System.err.println("Cannot load file " + url.getFile() );
		}
    }

    /**
     * @invisible
     * @param shaderSource a string containing the shader's code
     * @param filename the shader's filename, used to print error log information
     */
    private void attachVertexShader(String shaderSource, String file) 
    {
        vertexShader = gl.glCreateShaderObjectARB(GL.GL_VERTEX_SHADER_ARB);
        gl.glShaderSourceARB(vertexShader, 1, new String[]{shaderSource}, (int[]) null, 0);
        gl.glCompileShaderARB(vertexShader);
        checkLogInfo("Vertex shader " + file + " compilation: ", vertexShader);
        gl.glAttachObjectARB(programObject, vertexShader);                
    }
    
    /**
     * Loads and compiles the geometry shader contained in file.
     * @param file String
     */	
    public void loadGeometryShader(String file)
    {
        String shaderSource = PApplet.join(parent.loadStrings(file), "\n");
        attachGeometryShader(shaderSource, file);
    }

    /**
     * Loads and compiles the geometry shader contained in the URL
     * @param url URL
     */
    public void loadGeometryShader(URL url) 
    {
    	String shaderSource;
		try {
			shaderSource = PApplet.join(PApplet.loadStrings(url.openStream()),"\n");
			attachGeometryShader(shaderSource, url.getFile());
		} catch (IOException e) {
			System.err.println("Cannot load file " + url.getFile() );
		}
    }
    
    /**
     * @invisible
     * @param shaderSource a string containing the shader's code
     * @param filename the shader's filename, used to print error log information
     */
    private void attachGeometryShader(String shaderSource, String file) 
    {
        geometryShader = gl.glCreateShaderObjectARB(GL.GL_GEOMETRY_SHADER_EXT);
        gl.glShaderSourceARB(geometryShader, 1, new String[]{shaderSource},(int[]) null, 0);
        gl.glCompileShaderARB(geometryShader);
        checkLogInfo("Geometry shader " + file + " compilation: ", geometryShader);
        gl.glAttachObjectARB(geometryShader, geometryShader);
    }
    
    /**
     * Loads and compiles the fragment shader contained in file.
     * @param file String
     */	
    public void loadFragmentShader(String file)
    {
        String shaderSource = PApplet.join(parent.loadStrings(file), "\n");
        attachFragmentShader(shaderSource, file);
    }
    
    /**
     * Loads and compiles the fragment shader contained in the URL. 
     * @param url URL
     */
    public void loadFragmentShader(URL url) 
    {
    	String shaderSource;
		try {
			shaderSource = PApplet.join(PApplet.loadStrings(url.openStream()),"\n");
			attachFragmentShader(shaderSource, url.getFile());
		} catch (IOException e) {
			System.err.println("Cannot load file " + url.getFile() );
		}
    }
    
    /**
     * @invisible
     * @param shaderSource a string containing the shader's code
     * @param filename the shader's filename, used to print error log information
     */
    private void attachFragmentShader(String shaderSource, String file) 
    {
        fragmentShader = gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
        gl.glShaderSourceARB(fragmentShader, 1, new String[]{shaderSource},(int[]) null, 0);
        gl.glCompileShaderARB(fragmentShader);
        checkLogInfo("Fragment shader " + file + " compilation: ", fragmentShader);
        gl.glAttachObjectARB(programObject, fragmentShader);
    }
    
    /**
     * Returns the ID location of the attribute parameter given its name.
     * @param name String
     * @return int
     */
    public int getAttribLocation(String name)
    {
        return(gl.glGetAttribLocationARB(programObject, name));
    }

    /**
     * Returns the ID location of the uniform parameter given its name.
     * @param name String
     * @return int
     */
    public int getUniformLocation(String name)
    {
        return(gl.glGetUniformLocationARB(programObject, name));
    }

    /**
     * Configures the geometry shader by setting the primitive types that it will take as input
     * and return as output, and the maximum number of vertices that will generate. This method
     * needs to be called after creating the geometry shader and before linking the program. 
     * @param inGeoPrim String
     * @param outGeoPrim String 
     * @param maxNumOutVert int
     */    
    public void setupGeometryShader(String inGeoPrim, String outGeoPrim, int maxNumOutVert)
    {
        // Setting up the geometry shader.
    	
    	int inGeo = GLUtils.parsePrimitiveType(inGeoPrim);
    	int outGeo = GLUtils.parsePrimitiveType(outGeoPrim);
    	
        // First, what type of primitive this shader will accept.
	    gl.glProgramParameteriEXT(programObject, GL.GL_GEOMETRY_INPUT_TYPE_EXT, inGeo);
        // Second, what type of primitive this shader will output.
        gl.glProgramParameteriEXT(programObject, GL.GL_GEOMETRY_OUTPUT_TYPE_EXT, outGeo);

        // Third, setting the maximum number of vertices that the shader can output.
        // Using the maximum allowed value if the parameter is 0 or invalid.
	    IntBuffer buf = IntBuffer.allocate(1);
	    int n;
	    gl.glGetIntegerv(GL.GL_MAX_GEOMETRY_OUTPUT_VERTICES_EXT, buf);
	    n = buf.get(0);
        if ((0 < maxNumOutVert) && (maxNumOutVert < n)) n = maxNumOutVert;  
	
	    gl.glProgramParameteriEXT(programObject, GL.GL_GEOMETRY_VERTICES_OUT_EXT, n);
    }    
    
    /**
     * Links the shader program and validates it.
     */	
    public void linkProgram()
    {
        gl.glLinkProgramARB(programObject);
        gl.glValidateProgramARB(programObject);
        checkLogInfo("GLSL program validation: ", programObject);
    }

    /**
     * Starts the execution of the shader program.
     */
    public void start()
    {
        gl.glUseProgramObjectARB(programObject);
    }

    /**
     * Stops the execution of the shader program.
     */	
    public void stop()
    {
        gl.glUseProgramObjectARB(0);
    }

    /**
     * @invisible
     * Check the log error for the opengl object obj. Prints error message if needed.
     */		
    protected void checkLogInfo(String title, int obj)
    {
        IntBuffer iVal = BufferUtil.newIntBuffer(1);
        gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);

        int length = iVal.get();
        
        if (length <= 1) 
        {
            return;
        }    
        
        // Some error ocurred...
        ByteBuffer infoLog = BufferUtil.newByteBuffer(length);
        iVal.flip();
        gl.glGetInfoLogARB(obj, length, iVal, infoLog);
        byte[] infoBytes = new byte[length];
        infoLog.get(infoBytes);
        System.err.println(title);
        System.err.println(new String(infoBytes));
    }
    
    /**
     * @invisible
     */
    protected PApplet parent;
	
    /**
     * @invisible
     */
    protected GL gl;
	
    /**
     * @invisible
     */
    protected PGraphicsOpenGL pgl;
	
    /**
     * @invisible
     */
    protected int programObject;
	
    /**
     * @invisible
     */		
    protected int vertexShader;

    /**
     * @invisible
     */		
    protected int geometryShader;
    
    /**
     * @invisible
     */
    protected int fragmentShader;    
}
