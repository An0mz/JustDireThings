package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.client.renderers.OurRenderTypes;
import com.direwolf20.justdirethings.client.renderers.shader.DireRenderTypes;
import com.direwolf20.justdirethings.common.entities.PortalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {

    private static final double HALF_WIDTH = 0.45;
    private static final double HEIGHT = 1.9;
    private static final float SURFACE_OFFSET = 0.015f;
    private static final float BORDER_SIZE = 0.02f;
    private static final ResourceLocation PORTAL_SHADER_TEXTURE = ResourceLocation.fromNamespaceAndPath("justdirethings", "textures/block/portal_shader.png");

    public PortalEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PortalEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int color = entity.getPortalColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Direction facing = entity.getFacing();
        poseStack.pushPose();
        poseStack.translate(
                facing.getStepX() * SURFACE_OFFSET,
                facing.getStepY() * SURFACE_OFFSET,
                facing.getStepZ() * SURFACE_OFFSET
        );

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
        // Center fill uses custom shader (portal_entity) backed by portal_shader.png.
        float cx0 = x0;
        float cx1 = x1;
        float cz0 = z0;
        float cz1 = z1;
        float y0 = BORDER_SIZE;
        float y1 = (float) HEIGHT - BORDER_SIZE;
        if (facing.getAxis() == Direction.Axis.Z) {
            cx0 += BORDER_SIZE;
            cx1 -= BORDER_SIZE;
        } else {
            cz0 += BORDER_SIZE;
            cz1 -= BORDER_SIZE;
        }

        VertexConsumer surfaceVc = buffer.getBuffer(DireRenderTypes.portalEntity(PORTAL_SHADER_TEXTURE));
        addTexturedQuad(surfaceVc, pose, cx0, y0, cz0, cx1, y1, cz1);

        // Border strips are bright and opaque for quick portal-type identification.
        VertexConsumer borderVc = buffer.getBuffer(OurRenderTypes.PortalEntity);
        if (facing.getAxis() == Direction.Axis.Z) {
            addTintQuad(borderVc, pose, x0, 0f, z0, x0 + BORDER_SIZE, (float) HEIGHT, z1, r, g, b, 0.95f, packedLight);
            addTintQuad(borderVc, pose, x1 - BORDER_SIZE, 0f, z0, x1, (float) HEIGHT, z1, r, g, b, 0.95f, packedLight);
            addTintQuad(borderVc, pose, x0 + BORDER_SIZE, 0f, z0, x1 - BORDER_SIZE, BORDER_SIZE, z1, r, g, b, 0.95f, packedLight);
            addTintQuad(borderVc, pose, x0 + BORDER_SIZE, (float) HEIGHT - BORDER_SIZE, z0, x1 - BORDER_SIZE, (float) HEIGHT, z1, r, g, b, 0.95f, packedLight);
        } else {
            addTintQuad(borderVc, pose, x0, 0f, z0, x1, (float) HEIGHT, z0 + BORDER_SIZE, r, g, b, 0.95f, packedLight);
            addTintQuad(borderVc, pose, x0, 0f, z1 - BORDER_SIZE, x1, (float) HEIGHT, z1, r, g, b, 0.95f, packedLight);
            addTintQuad(borderVc, pose, x0, 0f, z0 + BORDER_SIZE, x1, BORDER_SIZE, z1 - BORDER_SIZE, r, g, b, 0.95f, packedLight);
            addTintQuad(borderVc, pose, x0, (float) HEIGHT - BORDER_SIZE, z0 + BORDER_SIZE, x1, (float) HEIGHT, z1 - BORDER_SIZE, r, g, b, 0.95f, packedLight);
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    private void addTexturedQuad(VertexConsumer vc, PoseStack.Pose pose,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1) {
        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;
        Matrix4f mat = pose.pose();
        vc.vertex(mat, x0, y0, z0).uv(u0, v1).endVertex();
        vc.vertex(mat, x1, y0, z1).uv(u1, v1).endVertex();
        vc.vertex(mat, x1, y1, z1).uv(u1, v0).endVertex();
        vc.vertex(mat, x0, y1, z0).uv(u0, v0).endVertex();
    }

    private void addTintQuad(VertexConsumer vc, PoseStack.Pose pose,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float r, float g, float b, float a, int light) {
        vc.vertex(pose.pose(), x0, y0, z0).color(r, g, b, a).endVertex();
        vc.vertex(pose.pose(), x1, y0, z1).color(r, g, b, a).endVertex();
        vc.vertex(pose.pose(), x1, y1, z1).color(r, g, b, a).endVertex();
        vc.vertex(pose.pose(), x0, y1, z0).color(r, g, b, a).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(PortalEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/white.png");
    }
}
