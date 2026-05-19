package com.direwolf20.justdirethings.client.events;

import com.direwolf20.justdirethings.client.renderactions.MiscRenders;
import com.direwolf20.justdirethings.client.renderactions.ThingFinder;
import com.direwolf20.justdirethings.client.renderers.OurRenderTypes;
import com.direwolf20.justdirethings.client.renderers.RenderHelpers;
import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;

public class RenderLevelLast {
    @SubscribeEvent
    static void renderWorldLastEvent(RenderLevelStageEvent evt) {
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        ItemStack heldItemMain = player.getMainHandItem();
        ItemStack heldItemOff = player.getOffhandItem();

        if (heldItemMain.getItem() instanceof ToggleableTool toggleableTool) {
            ThingFinder.render(evt, player, heldItemMain);
            if (toggleableTool.canUseAbilityAndDurability(heldItemMain, Ability.VOIDSHIFT) && ToggleableTool.getSetting(heldItemMain, Ability.VOIDSHIFT.getName() + "_render"))
                MiscRenders.renderTransparentPlayer(evt, player, heldItemMain);
        }
        if (heldItemOff.getItem() instanceof ToggleableTool toggleableTool) {
            ThingFinder.render(evt, player, heldItemOff);
            if (toggleableTool.canUseAbilityAndDurability(heldItemOff, Ability.VOIDSHIFT) && ToggleableTool.getSetting(heldItemOff, Ability.VOIDSHIFT.getName() + "_render"))
                MiscRenders.renderTransparentPlayer(evt, player, heldItemOff);
        }

        renderAreaPreviews(evt, player);
    }

    private static void renderAreaPreviews(RenderLevelStageEvent evt, Player player) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        PoseStack matrix = evt.getPoseStack();

        int chunkX = player.chunkPosition().x;
        int chunkZ = player.chunkPosition().z;
        int chunkRadius = 6;
        boolean rendered = false;

        for (int cx = chunkX - chunkRadius; cx <= chunkX + chunkRadius; cx++) {
            for (int cz = chunkZ - chunkRadius; cz <= chunkZ + chunkRadius; cz++) {
                ChunkAccess chunkAccess = level.getChunkSource().getChunkNow(cx, cz);
                if (!(chunkAccess instanceof LevelChunk chunk)) continue;
                for (BlockEntity be : new ArrayList<>(chunk.getBlockEntities().values())) {
                    if (!(be instanceof AreaAffectingBE areaAffectingBE)) continue;
                    if (!areaAffectingBE.getAreaAffectingData().renderArea) continue;

                    BlockPos bePos = be.getBlockPos();
                    matrix.pushPose();
                    matrix.translate(
                        bePos.getX() - cameraPos.x(),
                        bePos.getY() - cameraPos.y(),
                        bePos.getZ() - cameraPos.z()
                    );
                    Matrix4f matrix4f = matrix.last().pose();
                    AABB aabb = areaAffectingBE.getAABB(BlockPos.ZERO);
                    RenderHelpers.renderLines(matrix, aabb, Color.GREEN, bufferSource);
                    RenderHelpers.renderBoxSolid(matrix4f, bufferSource, aabb, 1, 0, 0, 0.125f);
                    if (areaAffectingBE.getAreaAffectingData().xRadius > 0 ||
                        areaAffectingBE.getAreaAffectingData().yRadius > 0 ||
                        areaAffectingBE.getAreaAffectingData().zRadius > 0) {
                        AABB offsetAABB = areaAffectingBE.getAABBOffsetOnly(BlockPos.ZERO);
                        RenderHelpers.renderLines(matrix, offsetAABB, Color.WHITE, bufferSource);
                        RenderHelpers.renderBoxSolid(matrix4f, bufferSource, offsetAABB, 0, 0, 1, 0.125f);
                    }
                    matrix.popPose();
                    rendered = true;
                }
            }
        }

        if (rendered) {
            bufferSource.endBatch(OurRenderTypes.lines());
            bufferSource.endBatch(OurRenderTypes.SolidBoxArea);
        }
    }
}
