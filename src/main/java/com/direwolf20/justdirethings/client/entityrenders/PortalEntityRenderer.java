package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.common.entities.PortalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {

    private static final double HALF_WIDTH = 0.45;
    private static final double HEIGHT = 1.9;

    public PortalEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PortalEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int color = entity.getPortalColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = 0.6f;

        Direction facing = entity.getFacing();
        poseStack.pushPose();

        VertexConsumer vc = buffer.getBuffer(RenderType.translucent());

        float x0, x1, z0, z1;
        if (facing.getAxis() == Direction.Axis.Z) {
            x0 = (float) -HALF_WIDTH;
            x1 = (float) HALF_WIDTH;
            z0 = 0f;
            z1 = 0f;
        } else {
            x0 = 0f;
            x1 = 0f;
            z0 = (float) -HALF_WIDTH;
            z1 = (float) HALF_WIDTH;
        }

        PoseStack.Pose pose = poseStack.last();

        // Front face
        addQuad(vc, pose, x0, 0f, z0, x1, (float) HEIGHT, z1, r, g, b, a, packedLight);
        // Back face (reversed winding)
        addQuad(vc, pose, x1, 0f, z1, x0, (float) HEIGHT, z0, r, g, b, a, packedLight);

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    private void addQuad(VertexConsumer vc, PoseStack.Pose pose,
                         float x0, float y0, float z0,
                         float x1, float y1, float z1,
                         float r, float g, float b, float a, int light) {
        int overlay = 0;
        // Two triangles forming a quad: (x0,y0,z0), (x1,y0,z1), (x1,y1,z1), (x0,y1,z0)
        vc.vertex(pose.pose(), x0, y0, z0).color(r, g, b, a).uv(0, 1).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0, 0, 1).endVertex();
        vc.vertex(pose.pose(), x1, y0, z1).color(r, g, b, a).uv(1, 1).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0, 0, 1).endVertex();
        vc.vertex(pose.pose(), x1, y1, z1).color(r, g, b, a).uv(1, 0).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0, 0, 1).endVertex();
        vc.vertex(pose.pose(), x0, y1, z0).color(r, g, b, a).uv(0, 0).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0, 0, 1).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(PortalEntity entity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
