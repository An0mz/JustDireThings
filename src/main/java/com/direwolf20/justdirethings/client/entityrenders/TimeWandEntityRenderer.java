package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.client.renderers.RenderHelpers;
import com.direwolf20.justdirethings.common.entities.TimeWandEntity;
import com.direwolf20.justdirethings.setup.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TimeWandEntityRenderer extends EntityRenderer<TimeWandEntity> {

    public TimeWandEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TimeWandEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockPos blockPos = entity.blockPosition();
        if (entity.level().getBlockState(blockPos).isAir())
            return;

        poseStack.pushPose();

        float tickRateProgress = entity.getTickSpeed() / (float) Config.logBase2(Config.TIME_WAND_MAX_MULTIPLIER.get());
        float timeProgress = (float) entity.getRemainingTime() / entity.getTotalTime();

        renderProgressBarOnSide(poseStack, buffer, 0.3f, tickRateProgress, 0.0f, 1.0f, 0.0f, 0.5f, packedLight);
        renderProgressBarOnSide(poseStack, buffer, 0.6f, timeProgress, 1.0f, 0.0f, 0.0f, 0.5f, packedLight);

        String timeRate = "x" + (int) entity.getAccelerationRate();
        float timeInSeconds = entity.getRemainingTime() / 20.0f;
        String timeRemains = String.format("%.2f", timeInSeconds) + "s";

        float pad = 0.13F;
        int white = ChatFormatting.WHITE.getColor();
        drawText(poseStack, buffer, timeRate,   new Vector3f(-pad,  0.39f,  0.51F), Axis.YP.rotationDegrees(0),    white);
        drawText(poseStack, buffer, timeRate,   new Vector3f( pad,  0.39f, -0.51F), Axis.YP.rotationDegrees(180F), white);
        drawText(poseStack, buffer, timeRate,   new Vector3f( 0.51F, 0.39f, pad),   Axis.YP.rotationDegrees(90F),  white);
        drawText(poseStack, buffer, timeRate,   new Vector3f(-0.51F, 0.39f, -pad),  Axis.YP.rotationDegrees(-90F), white);
        drawText(poseStack, buffer, timeRate,   new Vector3f(-pad,  1.01F, 0.5f - 0.39f), Axis.XP.rotationDegrees(90F),  white);
        drawText(poseStack, buffer, timeRate,   new Vector3f(-pad, -0.01F, -0.5f + 0.39f), Axis.XP.rotationDegrees(-90F), white);

        drawText(poseStack, buffer, timeRemains, new Vector3f(-pad,  0.69f,  0.51F), Axis.YP.rotationDegrees(0),    white);
        drawText(poseStack, buffer, timeRemains, new Vector3f( pad,  0.69f, -0.51F), Axis.YP.rotationDegrees(180F), white);
        drawText(poseStack, buffer, timeRemains, new Vector3f( 0.51F, 0.69f,  pad),  Axis.YP.rotationDegrees(90F),  white);
        drawText(poseStack, buffer, timeRemains, new Vector3f(-0.51F, 0.69f, -pad),  Axis.YP.rotationDegrees(-90F), white);
        drawText(poseStack, buffer, timeRemains, new Vector3f(-pad,  1.01F, 0.5f - 0.69f), Axis.XP.rotationDegrees(90F),  white);
        drawText(poseStack, buffer, timeRemains, new Vector3f(-pad, -0.01F, -0.5f + 0.69f), Axis.XP.rotationDegrees(-90F), white);

        poseStack.popPose();
    }

    private void renderProgressBarOnSide(PoseStack poseStack, MultiBufferSource buffer, float yStart, float progress, float r, float g, float b, float a, int packedLight) {
        float barWidth = 0.8f;
        float barHeight = 0.1f;
        float barProgress = barWidth * progress;

        renderBarFace(poseStack, buffer, -0.4f, yStart, 0.5f, barProgress, barHeight, r, g, b, a, new Quaternionf().rotateY(0.0f));
        renderBarFace(poseStack, buffer, -0.4f, yStart, 0.5f, barProgress, barHeight, r, g, b, a, new Quaternionf().rotateY((float) Math.PI));
        renderBarFace(poseStack, buffer, -0.4f, yStart, 0.5f, barProgress, barHeight, r, g, b, a, new Quaternionf().rotateY((float) Math.PI / 2.0f));
        renderBarFace(poseStack, buffer, -0.4f, yStart, 0.5f, barProgress, barHeight, r, g, b, a, new Quaternionf().rotateY((float) -Math.PI / 2.0f));
        renderBarFace(poseStack, buffer, -0.4f, yStart - 0.5f, 1f, barProgress, barHeight, r, g, b, a, new Quaternionf().rotateX((float) -Math.PI / 2.0f));
        renderBarFace(poseStack, buffer, -0.4f, yStart - 0.5f, 0f, barProgress, barHeight, r, g, b, a, new Quaternionf().rotateX((float) Math.PI / 2.0f));
    }

    private void renderBarFace(PoseStack poseStack, MultiBufferSource buffer, float xStart, float yStart, float zStart, float barWidth, float barHeight, float r, float g, float b, float a, Quaternionf rotation) {
        poseStack.pushPose();
        poseStack.mulPose(rotation);
        RenderHelpers.renderBoxSolid(poseStack.last().pose(), buffer, new AABB(xStart, yStart, zStart, xStart + barWidth, yStart + barHeight, zStart), r, g, b, a);
        poseStack.popPose();
    }

    private void drawText(PoseStack poseStack, MultiBufferSource source, String text, Vector3f pos, Quaternionf rotate, int color) {
        poseStack.pushPose();
        poseStack.translate(pos.x(), pos.y(), pos.z());
        poseStack.scale(0.01F, -0.01F, 0.01F);
        poseStack.mulPose(rotate);
        getFont().drawInBatch(text, 0, 0, -1, false, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TimeWandEntity entity) {
        return null;
    }
}
