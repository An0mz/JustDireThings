package com.direwolf20.justdirethings.client.blockentityrenders;

import com.direwolf20.justdirethings.client.blockentityrenders.baseber.AreaAffectingBER;
import com.direwolf20.justdirethings.client.renderers.RenderHelpers;
import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class ParadoxMachineBER extends AreaAffectingBER {

    public ParadoxMachineBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockEntity blockentity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        super.render(blockentity, partialTicks, poseStack, bufferIn, combinedLightsIn, combinedOverlayIn);
        if (!(blockentity instanceof ParadoxMachineBE paradoxBE)) return;
        if (!paradoxBE.renderParadox || paradoxBE.snapshotData.isEmpty()) return;

        Matrix4f matrix = poseStack.last().pose();
        float pulse = (Mth.sin((System.currentTimeMillis() % 3000L) / 3000.0F * Mth.TWO_PI) + 1.0F) * 0.5F;

        if (paradoxBE.targetType != 2) {
            renderGhostBlocks(paradoxBE, matrix, bufferIn, pulse);
        }
        if (paradoxBE.targetType != 1) {
            renderGhostEntities(paradoxBE, matrix, bufferIn, pulse);
        }
    }

    private void renderGhostBlocks(ParadoxMachineBE paradoxBE, Matrix4f matrix, MultiBufferSource bufferIn, float pulse) {
        if (!paradoxBE.snapshotData.contains("blocks")) return;
        ListTag blockList = paradoxBE.snapshotData.getList("blocks", 10);
        float alpha = paradoxBE.isRunning ? (0.3F + pulse * 0.15F) : (0.15F + pulse * 0.1F);
        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag blockTag = blockList.getCompound(i);
            CompoundTag posTag = blockTag.getCompound("pos");
            int rx = posTag.getInt("x");
            int ry = posTag.getInt("y");
            int rz = posTag.getInt("z");
            RenderHelpers.renderBoxSolid(matrix, bufferIn, new BlockPos(rx, ry, rz), 0.4F, 0.2F, 1.0F, alpha);
        }
    }

    private void renderGhostEntities(ParadoxMachineBE paradoxBE, Matrix4f matrix, MultiBufferSource bufferIn, float pulse) {
        if (!paradoxBE.snapshotData.contains("entities")) return;
        ListTag entityList = paradoxBE.snapshotData.getList("entities", 10);
        float alpha = paradoxBE.isRunning ? (0.35F + pulse * 0.15F) : (0.2F + pulse * 0.1F);
        for (int i = 0; i < entityList.size(); i++) {
            CompoundTag entityTag = entityList.getCompound(i);
            CompoundTag relPos = entityTag.getCompound("relativePos");
            // positions are relative to machine center; offset by +0.5 to get BER-local coords
            double rx = relPos.getDouble("vec3x") + 0.5;
            double ry = relPos.getDouble("vec3y") + 0.5;
            double rz = relPos.getDouble("vec3z") + 0.5;
            RenderHelpers.renderBoxSolid(matrix, bufferIn,
                    rx - 0.3, ry, rz - 0.3,
                    rx + 0.3, ry + 1.8, rz + 0.3,
                    1.0F, 0.5F, 0.1F, alpha);
        }
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.offset(-20, -20, -20), pos.offset(20, 20, 20));
    }
}
