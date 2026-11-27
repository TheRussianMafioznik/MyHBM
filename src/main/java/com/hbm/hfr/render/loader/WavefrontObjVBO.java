package com.hbm.hfr.render.loader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.hbm.render.amlfrom1710.IModelCustom;
import com.hbm.render.amlfrom1710.TextureCoordinate;
import com.hbm.render.amlfrom1710.Vertex;

public class WavefrontObjVBO implements IModelCustom {

	private static class VBOBufferData {
		String name;
		int vertices = 0;
		int vertexHandle;
		int uvHandle;
		int normalHandle;
	}

	private static final int VERTEX_SIZE = 3;
	private static final int UV_SIZE = 3;
	private static final TextureCoordinate EMPTY_TEX = new TextureCoordinate(0, 0);

	private final List<VBOBufferData> groups = new ArrayList<>();

	public WavefrontObjVBO(HFRWavefrontObject obj) {
		for (S_GroupObject g : obj.groupObjects) {
			VBOBufferData data = new VBOBufferData();
			data.name = g.name;

			int maxVertices = g.faces.size() * 3;
			FloatBuffer vertexData = BufferUtils.createFloatBuffer(maxVertices * VERTEX_SIZE);
			FloatBuffer uvData = BufferUtils.createFloatBuffer(maxVertices * UV_SIZE);
			FloatBuffer normalData = BufferUtils.createFloatBuffer(maxVertices * VERTEX_SIZE);

			for (S_Face face : g.faces) {
				boolean hasTex = face.textureCoordinates != null && face.textureCoordinates.length > 0;
				for (int i = 0; face.vertices.length > i; i++) {
					Vertex vert = face.vertices[i];
					TextureCoordinate tex = hasTex ? face.textureCoordinates[i] : EMPTY_TEX;
					Vertex normal = face.vertexNormals[i];

					data.vertices++;
					vertexData.put(vert.x).put(vert.y).put(vert.z);
					uvData.put(tex.u).put(tex.v).put(tex.w);
					normalData.put(normal.x).put(normal.y).put(normal.z);
				}
			}
			vertexData.flip();
			uvData.flip();
			normalData.flip();

			data.vertexHandle = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, data.vertexHandle);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);

			data.uvHandle = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, data.uvHandle);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvData, GL15.GL_STATIC_DRAW);

			data.normalHandle = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, data.normalHandle);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			groups.add(data);
		}
	}

	@Override
	public String getType() {
		return "obj_vbo";
	}

	private void renderVBO(VBOBufferData data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, data.vertexHandle);
		GL11.glVertexPointer(VERTEX_SIZE, GL11.GL_FLOAT, 0, 0L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, data.uvHandle);
		GL11.glTexCoordPointer(UV_SIZE, GL11.GL_FLOAT, 0, 0L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, data.normalHandle);
		GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0L);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, data.vertices);

		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void renderAll() {
		for (VBOBufferData data : groups) {
			renderVBO(data);
		}
	}

	@Override
	public void renderOnly(String... groupNames) {
		for (VBOBufferData data : groups) {
			for (String name : groupNames) {
				if (data.name.equalsIgnoreCase(name)) {
					renderVBO(data);
					break;
				}
			}
		}
	}

	@Override
	public void renderPart(String partName) {
		for (VBOBufferData data : groups) {
			if (data.name.equalsIgnoreCase(partName)) {
				renderVBO(data);
			}
		}
	}

	@Override
	public void renderAllExcept(String... excludedGroupNames) {
		for (VBOBufferData data : groups) {
			boolean skip = false;
			for (String name : excludedGroupNames) {
				if (data.name.equalsIgnoreCase(name)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				renderVBO(data);
			}
		}
	}

	@Override
	public void tessellateAll(com.hbm.render.amlfrom1710.Tessellator tes) {
		throw new RuntimeException("Tessellate not supported on HFR model");
	}

	@Override
	public void tessellatePart(com.hbm.render.amlfrom1710.Tessellator tes, String name) {
		throw new RuntimeException("Tessellate not supported on HFR model");
	}

	@Override
	public void tessellateOnly(com.hbm.render.amlfrom1710.Tessellator tes, String... names) {
		throw new RuntimeException("Tessellate not supported on HFR model");
	}

	@Override
	public void tessellateAllExcept(com.hbm.render.amlfrom1710.Tessellator tes, String... excluded) {
		throw new RuntimeException("Tessellate not supported on HFR model");
	}
}
