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
import processing.xml.*;

import javax.media.opengl.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

// Changes:
// ToDo: 
// Finish blending modes.

/**
 * This class defines a 2D filter to apply on GLTexture objects. A filter is basically
 * a glsl shader program with a set of predefined uniform attributes and a 2D grid 
 * where the input textures are mapped on. The points of the 2D grid can be altered in
 * the vertex stage of the filter, allowing for arbitrary distorsions in the shape of
 * the mesh.
 * The filter is specified in a xml file where the files names of the vertex and fragment 
 * shaders stored, as well as the definition of the grid (resolution and spacing).
 */
public class GLTextureFilter implements PConstants
{
    /**
     * Default constructor. 
     */
    public GLTextureFilter()
    {
        this.parent = null;
    }

    /**
     * Creates an instance of GLTextureFilter, loading the filter from filename. 
     * @param parent PApplet
     * @param filename String
     */
    public GLTextureFilter(PApplet parent, String filename)
    {
        this.parent = parent;
        initFilter(filename);
    }
	
    
    /**
     * Creates an instance of GLTextureFilter, loading the filter from a URL.
     */
    public GLTextureFilter(PApplet parent, URL url) 
    {
    	this.parent = parent;
    	initFilter(url);
    }
    
    /**
     * Returns the description of the filter.
     * @return String
     */   
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the maximum number of input or source textures supported by this filter. It can be called with less than that number
     * @return int
     */	
    public int getNumInputTextures()
    {
        return numInputTex;  
    }

    /**
     * Returns the maximum number of output or destination textures supported by this filter.
     * @return int
     */	
    public int getNumOutputTextures()
    {
        return numOutputTex;
    }
	
    /**
     * Applies the shader program on texture srcTex, writing the output to the texture destTex.
     * Sets fade constant to 1.
     * @param srcTex GLTexture
     * @param destTex GLTexture
     */		
    public void apply(GLTexture srcTex, GLTexture destTex)
    {
        apply(new GLTexture[] { srcTex }, new GLTexture[] { destTex }, null, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Applies the shader program on texture srcTex, writing the output to the texture destTex and
     * model destModel.
     * Sets fade constant to 1.
     * @param srcTex GLTexture
     * @param destTex GLTexture
     * @param destTex GLModel 
     */		    
    public void apply(GLTexture srcTex, GLTexture destTex, GLModel destModel)
    {
        apply(new GLTexture[] { srcTex }, new GLTexture[] { destTex }, destModel, 1.0f, 1.0f, 1.0f, 1.0f);
    }    
    
    /**
     * Applies the shader program on texture srcTex, writing the output to the texture destTex.
     * @param srcTex GLTexture
     * @param destTex GLTexture 
     * @param destA float
     */	
    public void apply(GLTexture srcTex, GLTexture destTex, float destA)
    {
        apply(new GLTexture[] { srcTex }, new GLTexture[] { destTex }, null, 1.0f, 1.0f, 1.0f, destA);
    }

    /**
     * Applies the shader program on texture srcTex, writing the output to the texture destTex.
     * @param srcTex GLTexture
     * @param destTex GLTexture 
     * @param destR float
     * @param destG float
     * @param destB float   
     * @param destA float
     */	
    public void apply(GLTexture srcTex, GLTexture destTex, float destR, float destG, float destB, float destA)
    {
        apply(new GLTexture[] { srcTex }, new GLTexture[] { destTex }, null, destR, destG, destB, destA);
    }    
    
    /**
     * Applies the shader program on textures srcTex, writing the output to the texture destTex.
     * Sets fade constant to 1.
     * @param srcTex GLTexture[] 
     * @param destTex GLTexture 
     */	
    public void apply(GLTexture[] srcTex, GLTexture destTex)
    {
        apply(srcTex, new GLTexture[] { destTex }, null, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Applies the shader program on textures srcTex, writing the output to the texture destTex.
     * @param srcTex GLTexture[] 
     * @param destTex GLTexture 
     * @param destA float
     */	
    public void apply(GLTexture[] srcTex, GLTexture destTex, float destA)
    {
        apply(srcTex, new GLTexture[] { destTex }, null, 1.0f, 1.0f, 1.0f, destA);
    }

    /**
     * Applies the shader program on textures srcTex, writing the output to the texture destTex.
     * @param srcTex GLTexture[] 
     * @param destTex GLTexture 
     * @param destR float
     * @param destG float
     * @param destB float   
     * @param destA float
     */	
    public void apply(GLTexture[] srcTex, GLTexture destTex, float destR, float destG, float destB, float destA)
    {
        apply(srcTex, new GLTexture[] { destTex }, null, destR, destG, destB, destA);
    }    
    
    /**
     * Applies the shader program on textures srcTex, writing the output to the textures destTex. 
     * Sets color constant to 1.
     * @param srcTex GLTexture[] 
     * @param destTex GLTexture[]
     */		
    public void apply(GLTexture[] srcTex, GLTexture[] destTex)
    {
        apply(srcTex, destTex, null, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Applies the shader program on textures srcTex, writing the output to the textures destTex.
     * Color (r, g, b, a) can be used by the shader to colorize the output texture(s).
     * @param srcTex GLTexture[] 
     * @param destTex GLTexture[]
     * @param destR float
     * @param destG float
     * @param destB float   
     * @param destA float
     */	
    public void apply(GLTexture[] srcTex, GLTexture[] destTex, GLModel destModel, float destR, float destG, float destB, float destA)
    {
        int srcWidth = srcTex[0].width;
        int srcHeight = srcTex[0].height;

        int destWidth = destTex[0].width;
        int destHeight = destTex[0].height;
        
        pgl.beginGL();
        
        checkDestTex(destTex, srcWidth, srcHeight);
        
        setGLConf(destWidth, destHeight);
        
        bindDestFBO(); 
		
        bindDestTexToFBO(destTex);
       
        shader.start();
        
        setupShader(srcTex, destWidth, destHeight, destR, destG, destB, destA);
        
        bindSrcTex(srcTex);
        
        if (grid.isUsingSrcTexRes())
        {
            srcWidth = srcTex[grid.srcTexInUse()].width;
            srcHeight = srcTex[grid.srcTexInUse()].height;
        }
        grid.render(srcWidth, srcHeight, destWidth, destHeight, srcTex.length);
        
        unbindSrcTex(srcTex);
		
        shader.stop();
        
        if (destModel != null) copyToModel(0, destTex[0], destModel);
        
        unbindDestFBO();
       
        restoreGLConf();
        
        pgl.endGL();
        
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
    
    /**
     * Sets the parameter value when the type is int.
     * @param String paramName
     * @param int value
     */        
    public void setParameterValue(String paramName, int value)
    {
        if (paramsHashMap.containsKey(paramName))
        {
            GLTextureFilterParameter param = (GLTextureFilterParameter) paramsHashMap.get(paramName);
            param.setValue(value);
        }
    }

    /**
     * Sets the parameter value when the type is float.
     * @param String paramName
     * @param float value
     */            
    public void setParameterValue(String paramName, float value)
    {
        if (paramsHashMap.containsKey(paramName))
        {
            GLTextureFilterParameter param = (GLTextureFilterParameter) paramsHashMap.get(paramName);
            param.setValue(value);
        }
    }

    /**
     * Sets the parameter value for any type. When the type is int or float, the first
     * element of the value array is considered.
     * @param String paramName 
     * @param value float[]
     */
    public void setParameterValue(String paramName, float[] value)
    {
        if (paramsHashMap.containsKey(paramName))
        {
            GLTextureFilterParameter param = (GLTextureFilterParameter) paramsHashMap.get(paramName);
            param.setValue(value);
        }
    }

    /**
     * Sets the ith value for the parameter (only valid for vec or mat types). 
     * @param String paramName  
     * @param int i
     * @param value float
     */      
    public void setParameterValue(String paramName, int i, float value)
    {
        if (paramsHashMap.containsKey(paramName))
        {
            GLTextureFilterParameter param = (GLTextureFilterParameter) paramsHashMap.get(paramName);
            param.setValue(i, value);
        }
    }

    /**
     * Sets the (ith, jth) value for the parameter (only valid for mat types). 
     * @param String paramName 
     * @param int i
     * @param int j 
     * @param value float
     */     
    public void setParameterValue(String paramName, int i, int j, float value)
    {
        if (paramsHashMap.containsKey(paramName))
        {
            GLTextureFilterParameter param = (GLTextureFilterParameter) paramsHashMap.get(paramName);
            param.setValue(i, j, value);
        }
    }

    /**
     * Sets all the value for all the parameters, by means of a parameter list of variable length.
     * values is an array of float[].
     * @param float[] values 
     */    
    public void setParameterValues(float[]...values)
    {
        float[] value;
        for (int i = 0; i < values.length; i++)
        {
        	value = values[i];
            paramsArray[i].setValue(value);
        }
    }

    /**
     * Get number of parameters.
     * @return int 
     */
    public int getParameterCount()
    {
        return paramsArray.length;
    }

    /**
     * Returns the type of the i-th parameter.
     * @return int 
     */
    public int getParameterType(int i)
    {
        return paramsArray[i].getType();
    }

    /**
     * Returns the name of the i-th parameter.
     * @return String 
     */
    public String getParameterName(int i)
    {
        return paramsArray[i].getName();
    }

    /**
     * Returns the label of the i-th parameter.
     * @return String 
     */    
    public String getParameterLabel(int i)
    {
        return paramsArray[i].getLabel();
    }    

    /**
     * Returns the i-th parameter.
     * @return GLTextureFilterParameter
     */
    public GLTextureFilterParameter getParameter(int i)
    {
        return paramsArray[i];
    }      

    /**
     * Sets the parameter value when the type is int.
     * @param int n
     * @param int value
     */        
    public void setParameterValue(int n, int value)
    {
    	paramsArray[n].setValue(value);
    }

    /**
     * Sets the parameter value when the type is float.
     * @param int n
     * @param float value
     */            
    public void setParameterValue(int n, float value)
    {
       	paramsArray[n].setValue(value);
    }

    /**
     * Sets the parameter value for any type. When the type is int or float, the first
     * element of the value array is considered.
     * @param int n 
     * @param value float[]
     */
    public void setParameterValue(int n, float[] value)
    {
       	paramsArray[n].setValue(value);
    }

    /**
     * Sets the ith value for the parameter (only valid for vec or mat types). 
     * @param int n  
     * @param int i
     * @param value float
     */      
    public void setParameterValue(int n, int i, float value)
    {
        paramsArray[n].setValue(i, value);
    }

    /**
     * Sets the (ith, jth) value for the parameter (only valid for mat types). 
     * @param int n 
     * @param int i
     * @param int j 
     * @param value float
     */     
    public void setParameterValue(int n, int i, int j, float value)
    {
        paramsArray[n].setValue(i, j, value);
    }
    
    /**
     * Returns the parameter with the provided name.
     * @return GLTextureFilterParameter
     */    
    public GLTextureFilterParameter getParameter(String paramName)
    {
        if (paramsHashMap.containsKey(paramName))
        {
            GLTextureFilterParameter param = (GLTextureFilterParameter) paramsHashMap.get(paramName);
            return param;
        }
        return null;
    }    
    
    /**
     * @invisible
     */
    protected void setGLConf(int w, int h)
    {
    	
        blend0 = gl.glIsEnabled(GL.GL_BLEND);
        int[] buf = new int[1];
        gl.glGetIntegerv(GL.GL_POLYGON_MODE, buf, 0);
        polyMode = buf[0];
      
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
        else gl.glDisable(GL.GL_BLEND);

        gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

        glstate.saveView();
        glstate.setOrthographicView(w, h);
    }

    /**
     * @invisible
     * Copies the pixel data in destTex to destModel, by using the VBO of destModel as a
     * PBO.
     */    
    protected void copyToModel(int attachPt, GLTexture destTex, GLModel destModel)
    {
        // destTex is the texture currently attached to GL.GL_COLOR_ATTACHMENT0_EXT attachment point.
		gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0_EXT + attachPt);  
		
		// The VBO of destModel is used as a PBO to copy pixel data to.
		gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_EXT, destModel.getVBO());

		// The pixel read above should take place in GPU memory (from the draw buffer
		
		// set to destTex to the VBO of destModel, viewed as a PBO).
		gl.glReadPixels(0, 0, destTex.width, destTex.height, GL.GL_RGBA, GL.GL_FLOAT, 0);	
		//gl.glReadBuffer(GL.GL_NONE);
		gl.glBindBuffer(GL.GL_PIXEL_PACK_BUFFER_EXT, 0);
    }
    
    /**
     * @invisible
     */
    protected void restoreGLConf()
    {
        glstate.restoreView();
        
        if (blend0) gl.glEnable(GL.GL_BLEND);
        else gl.glDisable(GL.GL_BLEND);

	    gl.glPolygonMode(GL.GL_FRONT, polyMode);
    }
    
    /**
     * @invisible
     */
    protected void bindSrcTex(GLTexture[] srcTex)
    {
        gl.glEnable(srcTex[0].getTextureTarget());

        for (int i = 0; i < srcTex.length; i++)
        {
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            gl.glBindTexture(srcTex[i].getTextureTarget(), srcTex[i].getTextureID());
        }      
    }

    /**
     * @invisible
     */	
    protected void unbindSrcTex(GLTexture[] srcTex)
    {
        for (int i = srcTex.length; 0 < i; i--)
        {
            gl.glActiveTexture(GL.GL_TEXTURE0 + i - 1);
            gl.glBindTexture(srcTex[i - 1].getTextureTarget(), 0);
        }
        
        gl.glDisable(srcTex[0].getTextureTarget());
    }

    /**
     * @invisible
     */
    protected void bindDestFBO()
    {
        glstate.pushFramebuffer();
        glstate.setFramebuffer(destFBO);
    }
  
    /**
     * @invisible
     */
    protected void unbindDestFBO()
    {
        glstate.popFramebuffer();
    }  
  
    /**
     * @invisible
     */ 
    protected void bindDestTexToFBO(GLTexture[] destTex)
    {
        destFBO.setDrawBuffers(destTex, numOutputTex);
    }    
        
    /**
     * @invisible
     */
    protected void initFBO()
    {
    	destFBO = new GLFramebufferObject(gl);
    }

    /**
     * @invisible
     */
    protected void initFilter(String filename)
    {
        initFilterCommon();
        
        filename = filename.replace('\\', '/');
    	XMLElement xml = new XMLElement(parent, filename);
    	
    	loadXML(xml);
      
		initShader(filename, false);
    }

    /**
     * @invisible
     */
    protected void initFilter(URL url) 
    {
    	initFilterCommon();
    	
    	try 
    	{
    		String xmlText = PApplet.join(PApplet.loadStrings(url.openStream()),"\n");
         	XMLElement xml = new XMLElement(xmlText);
       		loadXML(xml);
		} 
    	catch (IOException e) 
		{
			System.err.println("Error loading filter: " + e.getMessage());
	    }
    	
    	initShader(url.toString(), true);
    }
    
    /**
     * Common initialization code
     * @invisible
     */
    private void initFilterCommon() {
        pgl = (PGraphicsOpenGL)parent.g;
        gl = pgl.gl;
        glstate = new GLState(gl);        
        initFBO();
        
        numInputTex = 1;
        numOutputTex = 1;
        
        grid = null;

        paramsHashMap = new HashMap<String, GLTextureFilterParameter>();
    	paramsArray = new GLTextureFilterParameter[0];
    }
    
    /**
     * @invisible
     */
    protected void loadXML(XMLElement xml)
    {   
        // Parsing xml configuration.
    	
        int n = xml.getChildCount();
        String name, gridMode;
        String parName, parTypeStr, parValueStr, parLabelStr;
        int parType;
        GLTextureFilterParameter param;
        XMLElement child;
        vertexFN = geometryFN = fragmentFN = "";
        for (int i = 0; i < n; i++) 
        {
            child = xml.getChild(i);
            name = child.getName();
            if (name.equals("description"))
            {
                description = child.getContent();
            }            
            else if (name.equals("vertex"))
            {
                //vertexFN = fixShaderFilename(child.getContent(), rootPath);                
            	vertexFN = child.getContent();
            }
            else if (name.equals("geometry"))
            {
                //geometryFN = fixShaderFilename(child.getContent(), rootPath);
                geometryFN = child.getContent();
            	inGeoPrim = child.getStringAttribute("input");
                outGeoPrim = child.getStringAttribute("output");
                maxNumOutVert = child.getIntAttribute("vertcount");
            }
            else if (name.equals("fragment"))
            {
                //fragmentFN = fixShaderFilename(child.getContent(), rootPath);                
            	fragmentFN = child.getContent();
            }
            else if (name.equals("textures"))
            {
                numInputTex = child.getIntAttribute("input");
                numOutputTex = child.getIntAttribute("output");
            }

            else if (name.equals("parameter"))
            {
                float[] parValue;
                parName = child.getStringAttribute("name");
                parTypeStr = child.getStringAttribute("type");
                parLabelStr = child.getStringAttribute("label");
                parValueStr = child.getContent();
                parType = GLTextureFilterParameter.getType(parTypeStr);

                parValue = PApplet.parseFloat(PApplet.split(parValueStr, ' '));

                if ((-1 < parType) && !paramsHashMap.containsKey(parName))
                {
                    param = new GLTextureFilterParameter(parent, parName, parLabelStr, parType);
                    param.setValue(parValue);
                    paramsHashMap.put(parName, param);
                    paramsArray = (GLTextureFilterParameter[])PApplet.append(paramsArray, param);                    
                }
            }

            else if (name.equals("grid"))
            {
                gridMode = child.getStringAttribute("mode");
                
		        if (gridMode == null) gridMode = "direct";
				
                if (gridMode.equals("direct"))
                {
                    grid = new GLTextureGridDirect(gl, child);
                }
				else if (gridMode.equals("compiled"))
                {
                    grid = new GLTextureGridCompiled(gl, child);
                }
		        else
		        {
                    System.err.println("Unrecognized grid mode!");
                }
            }
            else
            {
                System.err.println("Unrecognized element in filter config file!");
            }
        }
    }
 
    /**
     * @invisible
     */	    
    String fixShaderFilename(String filename, String rootPath)
    {
    	String fixedFN = filename.replace('\\', '/');
        if (!rootPath.equals("") && (fixedFN.indexOf(rootPath) != 0))
        	fixedFN = rootPath + fixedFN;
        return fixedFN;
    }
    
    /**
     * Initialize the GLSLShader object.
     * 
     * @param xmlFilename the XML filename for this filter, used to generate the proper path
     * for the shader's programs
     * @param useURL if true, URL objects will be created to load the shader programs instead
     * of direct filenames 
     * @invisible
     */	
    protected void initShader(String xmlFilename, boolean useURL) 
    {  
    	// Getting the root path of the xml file
    	int idx;
    	String rootPath = ""; 
    	idx = xmlFilename.lastIndexOf('/');	
    	if (-1 < idx)
    	{	
    		rootPath = xmlFilename.substring(0, idx + 1);
    	}
    	
        if (grid == null)
        {
            // Creates a 1x1 grid.
            grid = new GLTextureGridDirect(gl);
        }

        // Initializing shader.
        shader = new GLSLShader(parent);
        
        if (!vertexFN.equals(""))
        {
        	vertexFN = fixShaderFilename(vertexFN, rootPath);
            if (useURL)
            {
				try {
					shader.loadVertexShader(new URL(vertexFN));
				} catch (MalformedURLException e) {
					System.err.println(e.getMessage());
				}
            }
			else shader.loadVertexShader(vertexFN);
        }
        
        if (!geometryFN.equals(""))
        {
        	geometryFN= fixShaderFilename(geometryFN, rootPath);
        	if (useURL) 
        	{
				try {
					shader.loadGeometryShader(new URL(geometryFN));
				} catch (MalformedURLException e) {
					System.err.println(e.getMessage());
				}
        	}
			else shader.loadGeometryShader(geometryFN);
            shader.setupGeometryShader(inGeoPrim, outGeoPrim, maxNumOutVert);
        } 
        
        if (!fragmentFN.equals(""))
        {
        	fragmentFN = fixShaderFilename(fragmentFN, rootPath);
        	if (useURL)
        	{
				try {
					shader.loadFragmentShader(new URL(fragmentFN));
				} catch (MalformedURLException e) {
					System.err.println(e.getMessage());
				}
        	}
			else shader.loadFragmentShader(fragmentFN);
        }            

        shader.linkProgram();
           
        // Initializing list of input textures.
        srcTexUnitUniform = new int[numInputTex];
        srcTexOffsetUniform = new int[numInputTex];
            
        // Gettting uniform parameters.
        for (int i = 0; i < numInputTex; i++)
        {
	        srcTexUnitUniform[i] = shader.getUniformLocation("src_tex_unit" + i);
	        srcTexOffsetUniform[i] = shader.getUniformLocation("src_tex_offset" + i);
        }

        clockDataUniform = shader.getUniformLocation("clock_data");
        destColorUniform = shader.getUniformLocation("dest_color");
        destTexSizeUniform = shader.getUniformLocation("dest_tex_size");

        // Putting the parameters into an array.
        for (int i = 0; i < paramsArray.length; i++)
        {
        	paramsArray[i].setShader(shader);
            paramsArray[i].genID();
        }
    }
    
    /**
     * @invisible
     */
    void setupShader(GLTexture[] srcTex, int w, int h, float destR, float destG, float destB, float destA)
    {
        int n = PApplet.min(numInputTex, srcTex.length); 

        for (int i = 0; i < n; i++)
        {
           if (-1 < srcTexUnitUniform[i]) gl.glUniform1iARB(srcTexUnitUniform[i], i);
           if (-1 < srcTexOffsetUniform[i]) gl.glUniform2fARB(srcTexOffsetUniform[i], 1.0f / srcTex[i].width, 1.0f / srcTex[i].height);
        }
        
        if (-1 < clockDataUniform)
        {
            int fcount = parent.frameCount;
            int msecs = parent.millis();          
            gl.glUniform2fARB(clockDataUniform, fcount, msecs);
        }
		
        if (-1 < destColorUniform)
        	
        	gl.glUniform4fARB(destColorUniform, destR, destG, destB, destA);		

        if (-1 < destTexSizeUniform) gl.glUniform2fARB(destTexSizeUniform, w, h);
        
        for (int i = 0; i < paramsArray.length; i++) paramsArray[i].setParameter();
    }
	
    /**
     * @invisible
     */
    protected void checkDestTex(GLTexture[] destTex, int w, int h)
    {
        for (int i = 0; i < destTex.length; i++)
        if (!destTex[i].available())
	    {
	        destTex[i].init(w, h);
        }
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
    int polyMode;
	
    /**
     * @invisible
     */
    protected PGraphicsOpenGL pgl;
	
    /**
     * @invisible
     */
    protected GLState glstate;    
	
    /**
     * @invisible
     */	
    protected GLFramebufferObject destFBO;
	
    /**
     * @invisible
     */	
    protected GLTextureGrid grid;
	
    /**
     * @invisible
     */
    protected HashMap<String, GLTextureFilterParameter> paramsHashMap;

    /**
     * @invisible
     */
    protected GLTextureFilterParameter[] paramsArray;
    
    /**
     * @invisible
     */
    protected String description;
    
    /**
     * @invisible
     */
    protected boolean blend;

    /**
     * @invisible
     */
    protected boolean blend0;
	
    /**
     * @invisible
     */
    protected int blendMode;

    /**
     * @invisible
     */
    protected GLSLShader shader;
	
    /**
     * @invisible
     */
    protected int numInputTex;

    /**
     * @invisible
     */
    protected int numOutputTex;
	
    /**
     * @invisible
     */
    protected int[] srcTexUnitUniform;    
	
    /**
     * @invisible
     */	
    protected int[] srcTexOffsetUniform;
	
    /**
     * @invisible
     */
    protected int clockDataUniform;

    /**
     * @invisible
     */	
    protected int destColorUniform;
	
    /**
     * @invisible
     */
    protected int destTexSizeUniform;

    /**
     * @invisible
     */
    protected String vertexFN;

    /**
     * @invisible
     */	
    protected String geometryFN; 

    /**
     * @invisible
     */
    protected String fragmentFN;
    
    /**
     * @invisible
     */    
    protected String inGeoPrim;
    
    /**
     * @invisible
     */    
    protected String outGeoPrim;
    
    /**
     * @invisible
     */    
    protected int maxNumOutVert;
}
