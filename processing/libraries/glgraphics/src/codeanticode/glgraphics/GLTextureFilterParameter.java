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
import java.nio.*;

/**
 * This class stores the parameter for a filter.  It can be of type int, float, vec2, vec3,
 * vec4, mat2, mat3 or mat4.
 */
public class GLTextureFilterParameter implements GLConstants
{
    /**
     * Creates an instance of GLTextureFilterParameter using the specified parameters. The shader is set to null.
     * @param parent PApplet
     * @param name String
     * @param label String 
     * @param type int 
     */	
    public GLTextureFilterParameter(PApplet parent, String name, String label, int type) 
    {
        this(parent, null, name, label, type);
    }

    /**
     * Creates an instance of GLTextureFilterParameter using the specified parameters.
     * @param parent PApplet
     * @param shader GLSLShader
     * @param name String
     * @param label String 
     * @param type int 
     */	
    public GLTextureFilterParameter(PApplet parent, GLSLShader shader, String name, String label, int type) 
    {
        PGraphicsOpenGL pgl = (PGraphicsOpenGL)parent.g;
        gl = pgl.gl;

        this.shader = shader;
        this.parent = parent;
        this.name = name;
        this.type = type;
        this.label = label;

        allocateValueArray();

        glID = -1;
    }

    /**
     * Sets the shader this parameter corresponds to.
     * @param shader GLSLShader
     */
    public void setShader(GLSLShader shader)
    {
        this.shader = shader;
    }

    /**
     * Generates the OpenGL ID of this parameter.
     */
    public void genID() 
    {
        if (shader != null) glID = shader.getUniformLocation(name);        
    }

    /**
     * Sets the parameter value when the type is int.
     * @param value int
     */   
    public void setValue(int value) 
    {
        if (type == TEX_FILTER_PARAM_INT) valueInt = value;
        else System.err.println("Error in texture filter parameter " + name + ": Wrong type.");
    }

    /**
     * Sets the parameter value when the type is float.
     * @param value float
     */
    public void setValue(float value)
    {
        if (type == TEX_FILTER_PARAM_FLOAT) valueFloat = value;
        else System.err.println("Error in texture filter parameter " + name + ": Wrong type.");
    }
    
    /**
     * Sets the parameter value for any type. When the type is int or float, the first
     * element of the value array is considered.
     * @param value float[]
     */
    public void setValue(float[] value)
    {
        int l = value.length;
        if ((l == 1) && ((type == TEX_FILTER_PARAM_INT) || (type == TEX_FILTER_PARAM_FLOAT)))
        {
            if (type == TEX_FILTER_PARAM_INT) valueInt = (int)value[0];
            else valueFloat = value[0];
        }
        else if (((l == 2) && (type == TEX_FILTER_PARAM_VEC2)) ||
                 ((l == 3) && (type == TEX_FILTER_PARAM_VEC3)) || 
                 ((l == 4) && (type == TEX_FILTER_PARAM_VEC4)) ||
                 ((l == 4) && (type == TEX_FILTER_PARAM_MAT2)) ||
                 ((l == 9) && (type == TEX_FILTER_PARAM_MAT3)) ||
                 ((l == 16) && (type == TEX_FILTER_PARAM_MAT4))) PApplet.arrayCopy(value, valueArray);
        else System.err.println("Error in texture filter parameter " + name + ": Wrong type.");
    }

    /**
     * Sets the ith value for the parameter (only valid for vec or mat types). 
     * @param int i
     * @param value float
     */    
    public void setValue(int i, float value)
    {
        if (((i < 2) && (type == TEX_FILTER_PARAM_VEC2)) ||
            ((i < 3) && (type == TEX_FILTER_PARAM_VEC3)) || 
            ((i < 4) && (type == TEX_FILTER_PARAM_VEC4)) ||
            ((i <4) && (type == TEX_FILTER_PARAM_MAT2)) ||
            ((i < 9) && (type == TEX_FILTER_PARAM_MAT3)) ||
            ((i < 16) && (type == TEX_FILTER_PARAM_MAT4))) 
        {
            valueArray[i] = value;
        }
        else System.err.println("Error in texture filter parameter " + name + ": Wrong type.");
    }

    /**
     * Sets the (ith, jth) value for the parameter (only valid for mat types). 
     * @param int i
     * @param int j 
     * @param value float
     */      
    public void setValue(int i, int j, float value)
    {
        if ((i < 2) && (j < 2) && (type == TEX_FILTER_PARAM_MAT2)) valueArray[2 * i + j] = value;
        else if ((i < 3) && (j < 3) && (type == TEX_FILTER_PARAM_MAT3)) valueArray[3 * i + j] = value;
        else if ((i < 4) && (j < 4) && (type == TEX_FILTER_PARAM_MAT3)) valueArray[4 * i + j] = value;
        else System.err.println("Error in texture filter parameter " + name + ": Wrong type.");
    }

    /**
     * Copies the parameter value to the GPU.
     */ 
    public void setParameter()
    {
        if (glID == -1) {
        	System.err.println("Error in texture filter parameter " + name + ": no valid opengl ID.");
            return;
        }
        if (type == TEX_FILTER_PARAM_INT) gl.glUniform1iARB(glID, valueInt);
        else if (type == TEX_FILTER_PARAM_FLOAT) gl.glUniform1fARB(glID, valueFloat);
        else if (type == TEX_FILTER_PARAM_VEC2) gl.glUniform2fvARB(glID, 1, FloatBuffer.wrap(valueArray));
        else if (type == TEX_FILTER_PARAM_VEC3) gl.glUniform3fvARB(glID, 1, FloatBuffer.wrap(valueArray));
        else if (type == TEX_FILTER_PARAM_VEC4) gl.glUniform4fvARB(glID, 1, FloatBuffer.wrap(valueArray));
        else if (type == TEX_FILTER_PARAM_MAT2) gl.glUniformMatrix2fvARB(glID, 1, false, FloatBuffer.wrap(valueArray));
        else if (type == TEX_FILTER_PARAM_MAT3) gl.glUniformMatrix3fvARB(glID, 1, false, FloatBuffer.wrap(valueArray));
        else if (type == TEX_FILTER_PARAM_MAT4) gl.glUniformMatrix4fvARB(glID, 1, false, FloatBuffer.wrap(valueArray));
        else System.err.println("Error in texture filter parameter " + name + ": Unknown type.");
    }

    /**
     * Returns the int constant that identifies a type, given the corresponding string.
     * @param String typeStr 
     * @return int 
     */ 
    public static int getType(String typeStr)
    {
        if (typeStr.equals("int")) return TEX_FILTER_PARAM_INT;
        else if (typeStr.equals("float")) return TEX_FILTER_PARAM_FLOAT;
        else if (typeStr.equals("vec2")) return TEX_FILTER_PARAM_VEC2;
        else if (typeStr.equals("vec3")) return TEX_FILTER_PARAM_VEC3;
        else if (typeStr.equals("vec4")) return TEX_FILTER_PARAM_VEC4;
        else if (typeStr.equals("mat2")) return TEX_FILTER_PARAM_MAT2;
        else if (typeStr.equals("mat3")) return TEX_FILTER_PARAM_MAT3;
        else if (typeStr.equals("mat4")) return TEX_FILTER_PARAM_MAT4;
        else return -1;
    }

    /**
     * Returns parameter type.
     * @return int 
     */     
    public int getType()
    {
        return type; 
    }

    /**
     * Returns parameter name.
     * @return String
     */    
    public String getName()
    {
        return name; 
    }

    /**
     * Returns parameter label.
     * @return String
     */    
    public String getLabel()
    {
        return label; 
    }    
    
    /**
     * Allocates valueArray to the size corresponding the the parameter type.
     */ 
    protected void allocateValueArray()
    {
        if (type == TEX_FILTER_PARAM_VEC2) valueArray = new float[2];
        else if (type == TEX_FILTER_PARAM_VEC3) valueArray = new float[3];
        else if (type == TEX_FILTER_PARAM_VEC4) valueArray = new float[4];
        else if (type == TEX_FILTER_PARAM_MAT2) valueArray = new float[4];
        else if (type == TEX_FILTER_PARAM_MAT3) valueArray = new float[9];
        else if (type == TEX_FILTER_PARAM_MAT4) valueArray = new float[16];
        else valueArray = null;
    }

    protected GLSLShader shader;
    protected String name;
    protected String label;
    protected int type;
    protected int glID;
    protected int valueInt;
    protected float valueFloat;
    protected float[] valueArray;
    protected PApplet parent;
    protected GL gl;
}
