package codeanticode.glgraphics;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;

public class GLGraphicsOffScreenMultisample extends PGraphicsOpenGL implements GLConstants
{
	// Framebuffer object and textures needed for the color, depth and stencil buffers.
	protected int[] mfbo = { 0 };
	protected int[] colorBuffer = { 0 };
	protected int[] depthBuffer = { 0 };
	protected int[] fbo = { 0 };
	protected GLTexture colorTex;
	protected GLState glstate;

	protected int m_multisamples = 4;
	
	GLTextureParameters r_params;

	public GLGraphicsOffScreenMultisample(PApplet parent, int iwidth, int iheight, GLTextureParameters params)
	{
		super();
		
		r_params = params;
		
		setParent(parent);
		setSize(iwidth, iheight);
	}

	public GLGraphicsOffScreenMultisample(PApplet parent, int iwidth, int iheight)
	{
		super();
		
		r_params = null;
		
		setParent(parent);
		setSize(iwidth, iheight);
	}
	
	// Setup offscreen framebuffer.
	protected void initFramebuffer()
	{
		gl = context.getGL();

		glstate = new GLState(gl);

//		gl.glEnable(colorTex.texTarget);

		gl.glGenRenderbuffersEXT(1, colorBuffer, 0);
		gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, colorBuffer[0]); // Binding render buffer
		gl.glRenderbufferStorageMultisampleEXT(GL.GL_RENDERBUFFER_EXT, m_multisamples, GL.GL_RGBA8, width, height);

		// Creating handle for depth buffer
		gl.glGenRenderbuffersEXT(1, depthBuffer, 0);
		gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthBuffer[0]); // Binding depth buffer
		// Allocating space for multisampled depth buffer
		gl.glRenderbufferStorageMultisampleEXT(GL.GL_RENDERBUFFER_EXT, m_multisamples, GL.GL_DEPTH_COMPONENT, width, height);

		// Creating handle for FBO
		gl.glGenFramebuffersEXT(1, mfbo, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, mfbo[0]); // Binding FBO
		gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_RENDERBUFFER_EXT, colorBuffer[0]);
		gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, depthBuffer[0]);

		// Creating texture
		if(r_params!=null)
			colorTex = new GLTexture(parent, width, height, r_params);
		else
			colorTex = new GLTexture(parent, width, height);
		
		colorTex.clear(0, 0);

		// Creating actual resolution FBO
		gl.glGenFramebuffersEXT(1, fbo, 0);

		glstate.pushFramebuffer();
		glstate.setFramebuffer(fbo[0]);

		// Attaching texture to FBO
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, colorTex.texTarget, colorTex.getTextureID(), 0);

		int stat = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		
		if (stat != GL.GL_FRAMEBUFFER_COMPLETE_EXT)
			System.err.println("Framebuffer Object error!");

		glstate.popFramebuffer();
	}

	public GLTexture getTexture()
	{
		return colorTex;
	}

	protected void allocate()
	{
		if (glstate == null)
		{
			PGraphicsOpenGL pgl = (PGraphicsOpenGL) parent.g;
			context = pgl.getContext();
			drawable = null;

			initFramebuffer();

			settingsInited = false;
		}
		else
			reapplySettings();
	}

	public void beginDraw()
	{
		if ((parent.width != width) || (parent.height != height))
		{

			// Saving current viewport.
			gl.glPushAttrib(GL.GL_VIEWPORT_BIT);

			// Saving current GL matrices.
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();

			// Setting the appropriate viewport.
			gl.glViewport(0, 0, width, height);
		}

		super.beginDraw();

		// gl.glScalef(1.0f, -1.0f, 1.0f);

		// Directing rendering to the texture...
		glstate.pushFramebuffer();
		glstate.setFramebuffer(mfbo[0]);
		gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);

		// Clearing Z-buffer to ensure that the new elements are drawn properly.
		gl.glClearColor(0f, 0f, 0f, 0.0f);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
	}

	public void endDraw()
	{
		super.endDraw();

		// Restoring render to previous framebuffer.
		glstate.popFramebuffer();

		// Then downsample the multisampled to the normal buffer with a blit
		gl.glBindFramebufferEXT(GL.GL_READ_FRAMEBUFFER_EXT, mfbo[0]); // source
		gl.glBindFramebufferEXT(GL.GL_DRAW_FRAMEBUFFER_EXT, fbo[0]); // dest
		gl.glBlitFramebufferEXT(0, 0, width, height, 0, 0, width, height, GL.GL_COLOR_BUFFER_BIT, GL.GL_NEAREST);

		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

		if ((parent.width != width) || (parent.height != height))
		{
			// Restoring previous GL matrices.
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPopMatrix();

			// Restoring previous viewport.
			gl.glPopAttrib();
		}
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
				gl.glEnable(colorTex.texTarget);
				report("after enable");

				float uscale = 1.0f;
				float vscale = 1.0f;

				PImage texture = textures[textureIndex];
				if (texture instanceof GLTexture)
				{
					GLTexture tex = (GLTexture) texture;
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
					gl.glTexCoord2f((cx + sx * a[U]) * uscale, (cy + sy * a[V]) * vscale);
					gl.glNormal3f(a[NX], a[NY], a[NZ]);
					gl.glEdgeFlag(a[EDGE] == 1);
					gl.glVertex3f(a[VX], a[VY], a[VZ]);

					gl.glColor4f(br, bg, bb, b[A]);
					gl.glTexCoord2f((cx + sx * b[U]) * uscale, (cy + sy * b[V]) * vscale);
					gl.glNormal3f(b[NX], b[NY], b[NZ]);
					gl.glEdgeFlag(a[EDGE] == 1);
					gl.glVertex3f(b[VX], b[VY], b[VZ]);

					gl.glColor4f(cr, cg, cb, c[A]);
					gl.glTexCoord2f((cx + sx * c[U]) * uscale, (cy + sy * c[V]) * vscale);
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

					gl.glDisable(colorTex.texTarget);
				}

			}
			else
			{ // no texture
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
}
