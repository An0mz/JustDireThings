package com.direwolf20.justdirethings.client.blockentityrenders.baseber;

import com.direwolf20.justdirethings.client.renderers.RenderHelpers;
import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import java.awt.*;

public class AreaAffectingBER implements BlockEntityRenderer<BlockEntity> {

    @Override
    public void render(BlockEntity blockentity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        Matrix4f matrix4f = matrixStackIn.last().pose();
        if (blockentity instanceof AreaAffectingBE areaAffectingBE) {
            if (areaAffectingBE.getAreaAffectingData().renderArea) {
                RenderHelpers.renderLines(matrixStackIn, areaAffectingBE.getAABB(BlockPos.ZERO), Color.GREEN, bufferIn);
                RenderHelpers.renderBoxSolid(matrix4f, bufferIn, areaAffectingBE.getAABB(BlockPos.ZERO), 1, 0, 0, 0.125f);
                if (areaAffectingBE.getAreaAffectingData().xRadius > 0 || areaAffectingBE.getAreaAffectingData().yRadius > 0 || areaAffectingBE.getAreaAffectingData().zRadius > 0) {
                    RenderHelpers.renderLines(matrixStackIn, areaAffectingBE.getAABBOffsetOnly(BlockPos.ZERO), Color.WHITE, bufferIn);
                    RenderHelpers.renderBoxSolid(matrix4f, bufferIn, areaAffectingBE.getAABBOffsetOnly(BlockPos.ZERO), 0, 0, 1, 0.125f);
                }
            }
        }
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.offset(-10, -10, -10), pos.offset(10, 10, 10));
    }
}
