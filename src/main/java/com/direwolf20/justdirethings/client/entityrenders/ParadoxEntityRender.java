package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.client.renderers.OurRenderTypes;
import com.direwolf20.justdirethings.client.renderers.RenderHelpers;
import com.direwolf20.justdirethings.client.sounds.ParadoxAmbientSound;
import com.direwolf20.justdirethings.common.entities.ParadoxEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Random;

public class ParadoxEntityRender extends EntityRenderer<ParadoxEntity> {

	private float savedPulseScale = -1;

	public ParadoxEntityRender(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(ParadoxEntity entity, float yaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		if (!entity.isCollapsing())
			ParadoxAmbientSound.playFor(entity);

		float shrinkScale = entity.getShrinkScale();
		if (shrinkScale <= 0.01f)
			return;

		poseStack.pushPose();

		// Sphere
		poseStack.pushPose();
		float currentRadius = entity.getRadius() + 1;
		float targetRadius = entity.getTargetRadius() + 1;
		int pulseCycleDuration = 50;
		float tickProgress = (entity.tickCount + partialTick) % pulseCycleDuration;
		float pulsePhase = tickProgress / pulseCycleDuration;
		float pulseScale = 0.25f + 0.025f * (1 - Math.abs(2 * pulsePhase - 1));

		if (entity.getGrowthTicks() > 0) {
			if (savedPulseScale == -1)
				savedPulseScale = pulseScale;
			float growthProgress = (float) entity.getGrowthTicks() / (float) entity.growthDuration;
			currentRadius = Mth.lerp(growthProgress, currentRadius, targetRadius);
		} else {
			savedPulseScale = -1;
		}
		if (savedPulseScale != -1)
			pulseScale = savedPulseScale;

		poseStack.translate(0, 0.5, 0);
		poseStack.scale(pulseScale * shrinkScale, pulseScale * shrinkScale, pulseScale * shrinkScale);
		RenderHelpers.renderSphere(poseStack, buffer, 0.25f * (float) Math.pow(currentRadius, 1.25), 0.0f, 0.0f, 0.0f,
				1.0f);
		poseStack.popPose();

		// Lightning arcs
		Color baseRed = new Color(100, 0, 0, 255);
		Color basePurple = new Color(75, 0, 0, 255);
		Random rng = new Random(entity.tickCount);
		float mixRatio = rng.nextFloat();
		int red = (int) (baseRed.getRed() * (1 - mixRatio) + basePurple.getRed() * mixRatio);
		int green = (int) (baseRed.getGreen() * (1 - mixRatio) + basePurple.getGreen() * mixRatio);
		int blue = (int) (baseRed.getBlue() * (1 - mixRatio) + basePurple.getBlue() * mixRatio);
		renderLightning(entity, poseStack, buffer, new Color(red, green, blue, 255), 0.025f,
				(float) Math.pow(entity.getRadius() + 1, 1.25), 5, 0.25f, 5, true);

		poseStack.popPose();

		super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
	}

	private void renderLightning(ParadoxEntity entity, PoseStack poseStack, MultiBufferSource buffer, Color color,
			float frequency, float maxLength, int numBranches, float branchChance, int segments,
			boolean branchAnywhere) {
		Random rand = new Random(entity.tickCount);

		for (int i = 0; i < (int) (frequency * 100); i++) {
			if (rand.nextFloat() > frequency)
				continue;

			poseStack.pushPose();
			poseStack.translate(0, 0.5, 0);

			float angle = rand.nextFloat() * 360.0f;
			float pitch = rand.nextFloat() * 180.0f - 90.0f;
			float length = rand.nextFloat() * maxLength;

			float startX = 0.0F, startY = 0.0F, startZ = 0.0F;
			for (int segment = 0; segment < segments; segment++) {
				float segmentLength = length / segments;
				float x = startX + segmentLength * Mth.cos((float) Math.toRadians(angle))
						* Mth.cos((float) Math.toRadians(pitch));
				float y = startY + segmentLength * Mth.sin((float) Math.toRadians(pitch));
				float z = startZ + segmentLength * Mth.sin((float) Math.toRadians(angle))
						* Mth.cos((float) Math.toRadians(pitch));

				VertexConsumer vc = buffer.getBuffer(OurRenderTypes.LINE_STRIP);
				Matrix4f matrix = poseStack.last().pose();
				vc.vertex(matrix, startX, startY, startZ)
						.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				vc.vertex(matrix, x, y, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
						.endVertex();

				startX = x;
				startY = y;
				startZ = z;

				for (int branch = 0; branch < numBranches; branch++) {
					if (rand.nextFloat() < branchChance && (branchAnywhere || segment == segments - 1)) {
						renderBranch(poseStack, buffer, color, startX, startY, startZ, segmentLength, rand);
					}
				}
			}

			poseStack.popPose();
		}
	}

	private void renderBranch(PoseStack poseStack, MultiBufferSource buffer, Color color, float startX, float startY,
			float startZ, float segmentLength, Random rand) {
		float branchAngle = rand.nextFloat() * 360.0f;
		float branchPitch = rand.nextFloat() * 180.0f - 90.0f;
		float branchLength = segmentLength * (0.5f + rand.nextFloat() * 0.5f);

		float bx = startX + branchLength * Mth.cos((float) Math.toRadians(branchAngle))
				* Mth.cos((float) Math.toRadians(branchPitch));
		float by = startY + branchLength * Mth.sin((float) Math.toRadians(branchPitch));
		float bz = startZ + branchLength * Mth.sin((float) Math.toRadians(branchAngle))
				* Mth.cos((float) Math.toRadians(branchPitch));

		VertexConsumer vc = buffer.getBuffer(OurRenderTypes.LINE_STRIP);
		Matrix4f matrix = poseStack.last().pose();
		vc.vertex(matrix, startX, startY, startZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		vc.vertex(matrix, bx, by, bz).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
				.endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(ParadoxEntity entity) {
		return new ResourceLocation("minecraft", "textures/misc/white.png");
	}
}
