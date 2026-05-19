package com.direwolf20.justdirethings.client.blockentityrenders;

import com.direwolf20.justdirethings.client.blockentityrenders.baseber.AreaAffectingBER;
import com.direwolf20.justdirethings.client.renderers.DireVertexConsumer;
import com.direwolf20.justdirethings.client.renderers.OurRenderTypes;
import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class ParadoxMachineBER extends AreaAffectingBER {

	public ParadoxMachineBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(BlockEntity blockentity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightsIn, int combinedOverlayIn) {
		super.render(blockentity, partialTicks, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn);
		if (!(blockentity instanceof ParadoxMachineBE paradoxMachineBE))
			return;

		if (paradoxMachineBE.isRunning) {
			float alpha = Mth.clamp(
					0.05f + (paradoxMachineBE.timeRunning / (float) paradoxMachineBE.getRunTime()) * 0.95f, 0.05f,
					1.0f);
			int intAlpha = (int) (alpha * 255);
			renderBlocks(paradoxMachineBE, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn, alpha,
					paradoxMachineBE.restoringBlocks);
			renderEntities(paradoxMachineBE, matrixStackIn, bufferIn, partialTicks, combinedLightsIn, intAlpha);
		} else {
			if (!paradoxMachineBE.renderParadox)
				return;
			int targetType = paradoxMachineBE.targetType;
			if (targetType == 0 || targetType == 1)
				renderBlocks(paradoxMachineBE, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn, 0.5f,
						paradoxMachineBE.getBlocksFromNBT());
			if (targetType == 0 || targetType == 2)
				renderEntities(paradoxMachineBE, matrixStackIn, bufferIn, partialTicks, combinedLightsIn, 175);
		}
	}

	private void renderBlocks(ParadoxMachineBE be, PoseStack poseStack, MultiBufferSource bufferIn,
			int combinedLightsIn, int combinedOverlayIn, float alpha, Map<BlockPos, BlockState> blocksToRestore) {
		Level level = be.getLevel();
		if (level == null || blocksToRestore.isEmpty())
			return;

		BlockColors blockColors = Minecraft.getInstance().getBlockColors();
		ModelBlockRenderer modelBlockRenderer = new ModelBlockRenderer(blockColors);
		BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

		for (Map.Entry<BlockPos, BlockState> entry : blocksToRestore.entrySet()) {
			BlockPos blockPos = entry.getKey();
			BlockState renderState = entry.getValue();

			if (!level.getBlockState(blockPos).canBeReplaced())
				continue;

			BakedModel model = blockRenderer.getBlockModel(renderState);
			float[] shades = new float[Direction.values().length * 2];
			BitSet shapeFlags = new BitSet(3);
			RandomSource random = RandomSource.create();
			BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
			ModelBlockRenderer.AmbientOcclusionFace aoFace = new ModelBlockRenderer.AmbientOcclusionFace();

			poseStack.pushPose();
			poseStack.translate(blockPos.getX() - be.getBlockPos().getX(), blockPos.getY() - be.getBlockPos().getY(),
					blockPos.getZ() - be.getBlockPos().getZ());

			VertexConsumer raw = renderState.isSolidRender(level, blockPos)
					? bufferIn.getBuffer(OurRenderTypes.RenderBlockFade)
					: bufferIn.getBuffer(OurRenderTypes.RenderBlockFadeNoCull);
			DireVertexConsumer vc = new DireVertexConsumer(raw, alpha);

			for (Direction dir : Direction.values()) {
				random.setSeed(renderState.getSeed(blockPos));
				List<BakedQuad> quads = model.getQuads(renderState, dir, random, ModelData.EMPTY, null);
				if (quads.isEmpty())
					continue;

				mutablePos.setWithOffset(blockPos, dir);
				BlockPos neighbor = blockPos.relative(dir);
				boolean skip = blocksToRestore.containsKey(neighbor)
						&& blocksToRestore.get(neighbor).isSolidRender(level, neighbor);
				if (!skip) {
					modelBlockRenderer.renderModelFaceAO(level, renderState, blockPos, poseStack, vc, quads, shades,
							shapeFlags, aoFace, combinedOverlayIn);
				}
			}

			// Unculled quads (no face direction)
			random.setSeed(renderState.getSeed(blockPos));
			List<BakedQuad> unculled = model.getQuads(renderState, null, random, ModelData.EMPTY, null);
			if (!unculled.isEmpty()) {
				modelBlockRenderer.renderModelFaceAO(level, renderState, blockPos, poseStack, vc, unculled, shades,
						shapeFlags, aoFace, combinedOverlayIn);
			}

			poseStack.popPose();
		}
	}

	private void renderEntities(ParadoxMachineBE be, PoseStack poseStack, MultiBufferSource bufferIn,
			float partialTicks, int combinedLightsIn, int alpha) {
		Level level = be.getLevel();
		if (level == null)
			return;

		for (Map.Entry<Vec3, LivingEntity> entry : be.getEntitiesFromNBT().entrySet()) {
			Vec3 entityPos = entry.getKey();
			if (be.isRunning && !be.restoringEntites.contains(entityPos))
				continue;
			LivingEntity entity = entry.getValue();
			if (entity == null)
				continue;

			poseStack.pushPose();
			poseStack.translate(entityPos.x - be.getBlockPos().getX(), entityPos.y - be.getBlockPos().getY(),
					entityPos.z - be.getBlockPos().getZ());

			renderTransparentEntity(poseStack, bufferIn, entity, partialTicks, combinedLightsIn, alpha);

			poseStack.popPose();
		}
	}

	@SuppressWarnings("unchecked")
	private void renderTransparentEntity(PoseStack poseStack, MultiBufferSource bufferIn, LivingEntity entity,
			float partialTicks, int combinedLightsIn, int alpha) {
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		EntityRenderer<? super LivingEntity> renderer = dispatcher.getRenderer(entity);
		if (renderer == null)
			return;

		ResourceLocation texture = renderer.getTextureLocation(entity);
		if (texture == null)
			return;

		VertexConsumer vc = bufferIn.getBuffer(RenderType.itemEntityTranslucentCull(texture));
		int overlayCoords = LivingEntityRenderer.getOverlayCoords(entity, 0);
		// Ghost entities aren't in the world, so use full brightness instead of the
		// BER's light value
		combinedLightsIn = 0xF000F0;

		// Entities are recreated from NBT each frame so yBodyRotO is 0 — skip
		// interpolation
		float bodyRot = entity.yBodyRot;
		setupRotations(entity, poseStack, bodyRot);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0F, -1.501F, 0.0F);

		if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
			EntityModel<LivingEntity> model = (EntityModel<LivingEntity>) livingRenderer.getModel();
			model.attackTime = 0f;
			model.riding = false;
			model.young = entity.isBaby();
			model.prepareMobModel(entity, 0f, 0f, partialTicks);
			model.setupAnim(entity, 0f, 0f, 0f, 0f, entity.getXRot());
			model.renderToBuffer(poseStack, vc, combinedLightsIn, overlayCoords, 1.0f, 1.0f, 1.0f, alpha / 255.0f);
		}
	}

	private static void setupRotations(LivingEntity entity, PoseStack poseStack, float bodyRot) {
		if (!entity.hasPose(Pose.SLEEPING)) {
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
		}
	}

	@Override
	public boolean shouldRenderOffScreen(BlockEntity blockEntity) {
		// Must always be true — this is evaluated once at chunk compile time, not per
		// frame.
		// The render() method exits early when there's nothing to show.
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(BlockEntity blockEntity) {
		if (!(blockEntity instanceof ParadoxMachineBE be)) {
			BlockPos pos = blockEntity.getBlockPos();
			return new AABB(pos.offset(-20, -20, -20), pos.offset(20, 20, 20));
		}
		// Cover the full snapshot/restore area so frustum culling still works
		return be.getAABB(be.getBlockPos()).inflate(5);
	}
}
