package com.direwolf20.justdirethings.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderHelpers {

	public static void renderLines(PoseStack matrix, BlockPos startPos, BlockPos endPos, Color color,
			MultiBufferSource buffer) {
		// We want to draw from the starting position to the (ending position)+1
		int x = Math.min(startPos.getX(), endPos.getX()), y = Math.min(startPos.getY(), endPos.getY()),
				z = Math.min(startPos.getZ(), endPos.getZ());

		int dx = (startPos.getX() > endPos.getX()) ? startPos.getX() + 1 : endPos.getX() + 1;
		int dy = (startPos.getY() > endPos.getY()) ? startPos.getY() + 1 : endPos.getY() + 1;
		int dz = (startPos.getZ() > endPos.getZ()) ? startPos.getZ() + 1 : endPos.getZ() + 1;

		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.lines());

		matrix.pushPose();
		Matrix4f matrix4f = matrix.last().pose();
		Matrix3f matrix3f = matrix.last().normal();
		int colorRGB = color.getRGB();

		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();

		matrix.popPose();
	}

	public static void renderLines(PoseStack matrix, AABB aabb, Color color, MultiBufferSource buffer) {
		// We want to draw from the starting position to the (ending position)+1
		float x = (float) aabb.minX;
		float y = (float) aabb.minY;
		float z = (float) aabb.minZ;
		float dx = (float) aabb.maxX;
		float dy = (float) aabb.maxY;
		float dz = (float) aabb.maxZ;

		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.lines());

		matrix.pushPose();
		Matrix4f matrix4f = matrix.last().pose();
		Matrix3f matrix3f = matrix.last().normal();
		int colorRGB = color.getRGB();

		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();

		matrix.popPose();
	}

	public static void renderLines(PoseStack matrix, BlockPos startPos, BlockPos endPos, Color color) {
		// We want to draw from the starting position to the (ending position)+1
		int x = Math.min(startPos.getX(), endPos.getX()), y = Math.min(startPos.getY(), endPos.getY()),
				z = Math.min(startPos.getZ(), endPos.getZ());

		int dx = (startPos.getX() > endPos.getX()) ? startPos.getX() + 1 : endPos.getX() + 1;
		int dy = (startPos.getY() > endPos.getY()) ? startPos.getY() + 1 : endPos.getY() + 1;
		int dz = (startPos.getZ() > endPos.getZ()) ? startPos.getZ() + 1 : endPos.getZ() + 1;

		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.lines());

		matrix.pushPose();
		Matrix4f matrix4f = matrix.last().pose();
		Matrix3f matrix3f = matrix.last().normal();
		int colorRGB = color.getRGB();

		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();

		buffer.endBatch(OurRenderTypes.lines()); // @mcp: draw = finish
		matrix.popPose();
	}

	public static void renderSphere(PoseStack matrix, MultiBufferSource buffer, float radius, float r, float g, float b,
			float a) {
		VertexConsumer vc = buffer.getBuffer(OurRenderTypes.BlackSphere);
		Matrix4f m4 = matrix.last().pose();
		int latSegments = 12;
		int lonSegments = 16;
		for (int lat = 0; lat < latSegments; lat++) {
			double theta1 = lat * Math.PI / latSegments;
			double theta2 = (lat + 1) * Math.PI / latSegments;
			float sinT1 = (float) Math.sin(theta1), cosT1 = (float) Math.cos(theta1);
			float sinT2 = (float) Math.sin(theta2), cosT2 = (float) Math.cos(theta2);
			for (int lon = 0; lon < lonSegments; lon++) {
				double phi1 = lon * 2.0 * Math.PI / lonSegments;
				double phi2 = (lon + 1) * 2.0 * Math.PI / lonSegments;
				float cosP1 = (float) Math.cos(phi1), sinP1 = (float) Math.sin(phi1);
				float cosP2 = (float) Math.cos(phi2), sinP2 = (float) Math.sin(phi2);
				float x1 = radius * sinT1 * cosP1, y1 = radius * cosT1, z1 = radius * sinT1 * sinP1;
				float x2 = radius * sinT2 * cosP1, y2 = radius * cosT2, z2 = radius * sinT2 * sinP1;
				float x3 = radius * sinT2 * cosP2, y3 = y2, z3 = radius * sinT2 * sinP2;
				float x4 = radius * sinT1 * cosP2, y4 = y1, z4 = radius * sinT1 * sinP2;
				vc.vertex(m4, x1, y1, z1).color(r, g, b, a).endVertex();
				vc.vertex(m4, x2, y2, z2).color(r, g, b, a).endVertex();
				vc.vertex(m4, x3, y3, z3).color(r, g, b, a).endVertex();
				vc.vertex(m4, x4, y4, z4).color(r, g, b, a).endVertex();
			}
		}
	}

	public static void renderFaceSolid(Matrix4f matrix, MultiBufferSource buffer, BlockPos pos, Direction direction,
			float r, float g, float b, float alpha) {
		float sx = (float) (pos.getX() - 0.001);
		float sy = (float) (pos.getY() - 0.001);
		float sz = (float) (pos.getZ() - 0.001);
		float ex = (float) (pos.getX() + 1.0015);
		float ey = (float) (pos.getY() + 1.0015);
		float ez = (float) (pos.getZ() + 1.0015);

		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.SolidBoxArea);
		switch (direction) {
			case DOWN -> {
				builder.vertex(matrix, sx, sy, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, sy, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, sy, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, sy, ez).color(r, g, b, alpha).endVertex();
			}
			case UP -> {
				builder.vertex(matrix, sx, ey, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, ey, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, ey, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, ey, sz).color(r, g, b, alpha).endVertex();
			}
			case NORTH -> {
				builder.vertex(matrix, sx, sy, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, ey, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, ey, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, sy, sz).color(r, g, b, alpha).endVertex();
			}
			case SOUTH -> {
				builder.vertex(matrix, sx, sy, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, sy, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, ey, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, ey, ez).color(r, g, b, alpha).endVertex();
			}
			case WEST -> {
				builder.vertex(matrix, sx, sy, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, sy, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, ey, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, sx, ey, sz).color(r, g, b, alpha).endVertex();
			}
			case EAST -> {
				builder.vertex(matrix, ex, sy, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, ey, sz).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, ey, ez).color(r, g, b, alpha).endVertex();
				builder.vertex(matrix, ex, sy, ez).color(r, g, b, alpha).endVertex();
			}
		}
	}

	public static void renderBoxSolid(Matrix4f matrix, MultiBufferSource buffer, BlockPos pos, float r, float g,
			float b, float alpha) {
		double x = pos.getX() - 0.001;
		double y = pos.getY() - 0.001;
		double z = pos.getZ() - 0.001;
		double xEnd = pos.getX() + 1.0015;
		double yEnd = pos.getY() + 1.0015;
		double zEnd = pos.getZ() + 1.0015;

		renderBoxSolid(matrix, buffer, x, y, z, xEnd, yEnd, zEnd, r, g, b, alpha);
	}

	public static void renderBoxSolid(Matrix4f matrix, MultiBufferSource buffer, AABB aabb, float r, float g, float b,
			float alpha) {
		float minX = (float) aabb.minX;
		float minY = (float) aabb.minY;
		float minZ = (float) aabb.minZ;
		float maxX = (float) aabb.maxX;
		float maxY = (float) aabb.maxY;
		float maxZ = (float) aabb.maxZ;

		renderBoxSolid(matrix, buffer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, alpha);
	}

	public static void renderBoxSolid(Matrix4f matrix, MultiBufferSource buffer, double x, double y, double z,
			double xEnd, double yEnd, double zEnd, float red, float green, float blue, float alpha) {
		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.SolidBoxArea);

		// careful: mc want's it's vertices to be defined CCW - if you do it the other
		// way around weird cullling issues will arise
		// CCW herby counts as if you were looking at it from the outside
		float startX = (float) x;
		float startY = (float) y;
		float startZ = (float) z;
		float endX = (float) xEnd;
		float endY = (float) yEnd;
		float endZ = (float) zEnd;

		// down
		builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();

		// up
		builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();

		// east
		builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();

		// west
		builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();

		// south
		builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();

		// north
		builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
	}
}
