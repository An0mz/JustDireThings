package com.direwolf20.justdirethings.client.blockentityrenders.baseber;

import com.direwolf20.justdirethings.client.renderers.DireModelBlockRenderer;
import com.direwolf20.justdirethings.client.renderers.DireVertexConsumer;
import com.direwolf20.justdirethings.client.renderers.OurRenderTypes;
import com.direwolf20.justdirethings.common.blockentities.basebe.GooBlockBE_Base;
import com.direwolf20.justdirethings.common.blocks.gooblocks.GooBlock_Base;
import com.direwolf20.justdirethings.common.blocks.gooblocks.GooPatternBlock;
import com.direwolf20.justdirethings.datagen.JustDireItemTags;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class GooBlockRender_Base<T extends GooBlockBE_Base> implements BlockEntityRenderer<T> {
	private final static float percentageDivisor = (float) 100 / GooPatternBlock.GOOSTAGE.getPossibleValues().size();
	private ItemStack cachedItemStack = ItemStack.EMPTY;
	private int currentItemIndex = 0;
	private long lastChangeTime = 0;

	public GooBlockRender_Base(BlockEntityRendererProvider.Context p_173636_) {

	}

	@Override
	public void render(T blockentity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightsIn, int combinedOverlayIn) {
		BlockState blockState = blockentity.getBlockState();

		if (!blockState.getValue(GooBlock_Base.ALIVE)) {
			renderFloatingItem(blockentity, matrixStackIn, bufferIn, combinedLightsIn);
		}

		for (Direction direction : Direction.values()) {
			int remainingTicks = blockentity.getRemainingTimeFor(direction);
			if (remainingTicks > 0) {
				int maxTicks = blockentity.getCraftingDuration(direction);
				renderTextures(direction, blockentity.getLevel(), blockentity.getBlockPos(), matrixStackIn, bufferIn,
						combinedOverlayIn, remainingTicks, maxTicks, blockentity.getBlockState(), blockentity);
			}
		}
	}

	private ItemStack getNextItemFromTag(int tier) {
		TagKey<Item> tag = switch (tier) {
			case 1 -> JustDireItemTags.GOO_REVIVE_TIER_1;
			case 2 -> JustDireItemTags.GOO_REVIVE_TIER_2;
			case 3 -> JustDireItemTags.GOO_REVIVE_TIER_3;
			case 4 -> JustDireItemTags.GOO_REVIVE_TIER_4;
			default -> null;
		};
		if (tag == null)
			return ItemStack.EMPTY;

		List<Item> items = ForgeRegistries.ITEMS.tags().getTag(tag).stream().collect(Collectors.toList());
		if (items.isEmpty())
			return ItemStack.EMPTY;

		Item next = items.get(currentItemIndex % items.size());
		currentItemIndex = (currentItemIndex + 1) % items.size();
		return new ItemStack(next);
	}

	private void renderFloatingItem(T blockentity, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightsIn) {
		long currentTime = System.currentTimeMillis();
		long cycleDuration = 3600;
		long elapsedTime = (currentTime - lastChangeTime) % cycleDuration;
		float fadeFactor = (float) (0.5 - 0.5 * Math.cos((2 * Math.PI * elapsedTime) / cycleDuration));

		if (cachedItemStack.isEmpty() || (elapsedTime < 50 && currentTime - lastChangeTime >= cycleDuration)) {
			cachedItemStack = getNextItemFromTag(blockentity.getTier());
			lastChangeTime = currentTime;
		}

		if (cachedItemStack.isEmpty())
			return;

		final float finalFadeFactor = fadeFactor;
		MultiBufferSource transparentBuffer = renderType -> new DireVertexConsumer(
				bufferIn.getBuffer(RenderType.translucent()), finalFadeFactor);

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		BakedModel bakedModel = itemRenderer.getModel(cachedItemStack, blockentity.getLevel(), null, 0);

		for (Direction direction : Direction.values()) {
			matrixStackIn.pushPose();

			boolean isBlockItem = cachedItemStack.getItem() instanceof BlockItem;
			Vec3 itemPos = getOffsetPositionForSide(direction, isBlockItem);
			matrixStackIn.translate(itemPos.x, itemPos.y, itemPos.z);
			applyRotationForSide(matrixStackIn, direction);
			matrixStackIn.scale(0.6f, 0.6f, 0.6f);

			itemRenderer.render(cachedItemStack, ItemDisplayContext.GROUND, false, matrixStackIn, transparentBuffer,
					combinedLightsIn, OverlayTexture.NO_OVERLAY, bakedModel);

			matrixStackIn.popPose();
		}
	}

	private Vec3 getOffsetPositionForSide(Direction direction, boolean isBlockItem) {
		double offset = 0.025;
		double nudge = isBlockItem ? 0.10 : 0.05;
		return switch (direction) {
			case UP -> new Vec3(0.5, 1.0 + offset, 0.5 - nudge);
			case DOWN -> new Vec3(0.5, 0.0 - offset, 0.5 + nudge);
			case NORTH -> new Vec3(0.5, 0.5 - nudge, 0.0 - offset);
			case SOUTH -> new Vec3(0.5, 0.5 - nudge, 1.0 + offset);
			case WEST -> new Vec3(0.0 - offset, 0.5 - nudge, 0.5);
			case EAST -> new Vec3(1.0 + offset, 0.5 - nudge, 0.5);
		};
	}

	private void applyRotationForSide(PoseStack matrixStackIn, Direction direction) {
		switch (direction) {
			case UP -> matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
			case DOWN -> matrixStackIn.mulPose(Axis.XN.rotationDegrees(90));
			case NORTH -> matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
			case SOUTH -> matrixStackIn.mulPose(Axis.YN.rotationDegrees(0));
			case WEST -> matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
			case EAST -> matrixStackIn.mulPose(Axis.YP.rotationDegrees(-90));
		}
	}

	public void renderTextures(Direction direction, Level level, BlockPos pos, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedOverlayIn, int remainingTicks, int maxTicks, BlockState renderState,
			GooBlockBE_Base gooBlockBE_base) {
		float percentComplete = ((1 - (float) remainingTicks / (float) maxTicks) * 100);
		int tensDigit = (int) (percentComplete / percentageDivisor);
		if (tensDigit > 0) { // Render the prior stage with full transparency
			BlockState patternState = Registration.GooPatternBlock.get().defaultBlockState()
					.setValue(GooPatternBlock.GOOSTAGE, tensDigit - 1);
			renderTexturePattern(direction, level, pos, matrixStackIn, bufferIn, combinedOverlayIn, 1f, patternState,
					renderState, gooBlockBE_base);
		}
		BlockState patternState = Registration.GooPatternBlock.get().defaultBlockState()
				.setValue(GooPatternBlock.GOOSTAGE, tensDigit);
		float startOfCurrentStage = tensDigit * percentageDivisor; // This calculates the starting percentage of the
																	// current stage
		float percentagePart = percentComplete - startOfCurrentStage; // This calculates how far into the current stage
																		// we are
		float alpha = percentagePart / percentageDivisor;
		renderTexturePattern(direction, level, pos, matrixStackIn, bufferIn, combinedOverlayIn, alpha, patternState,
				renderState, gooBlockBE_base);
	}

	public void renderTexturePattern(Direction direction, Level level, BlockPos pos, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedOverlayIn, float transparency, BlockState pattern,
			BlockState renderState, GooBlockBE_Base gooBlockBE_base) {
		BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();
		DireModelBlockRenderer modelBlockRenderer = new DireModelBlockRenderer(blockColors, direction);
		BlockPos renderAtPos = pos.relative(direction);

		// These are used for the rendering below
		float[] afloat = new float[Direction.values().length * 2];
		BitSet bitset = new BitSet(3);
		RandomSource randomSource = RandomSource.create();
		BlockPos.MutableBlockPos blockpos$mutableblockpos = renderAtPos.mutable();
		ModelBlockRenderer.AmbientOcclusionFace modelblockrenderer$ambientocclusionface = new ModelBlockRenderer.AmbientOcclusionFace();

		matrixStackIn.pushPose();
		// Offset the render to the direction we're crafting at
		matrixStackIn.translate(direction.getNormal().getX(), direction.getNormal().getY(),
				direction.getNormal().getZ());
		// Slightly larger than a normal block, to prevent z-fighting -
		// Based on tier incase someone puts a craft in between 2 different tiers - If
		// they put between two of the same tiers theres a bit of zfighting but oh well
		float translateF = (float) gooBlockBE_base.getTier() / 2000;
		matrixStackIn.translate(-translateF, -translateF, -translateF);
		float scaleF = (float) gooBlockBE_base.getTier() / 1000;
		matrixStackIn.scale(1 + scaleF, 1 + scaleF, 1 + scaleF);

		// Rotate based on the direction of the block we're drawing. If we don't rotate
		// both blocks together we get z-fighting!
		matrixStackIn.translate(0.5, 0.5, 0.5);
		matrixStackIn.mulPose(direction.getRotation());
		matrixStackIn.translate(-0.5, -0.5, -0.5);

		// Pattern Block - Renders to the depth buffer ONLY, to set the pattern that we
		// draw
		VertexConsumer builder = bufferIn.getBuffer(OurRenderTypes.GooPattern);
		DireVertexConsumer chunksConsumer = new DireVertexConsumer(builder, 1f);
		BakedModel ibakedmodel = blockrendererdispatcher.getBlockModel(pattern);

		randomSource.setSeed(pattern.getSeed(renderAtPos));

		List<BakedQuad> list;
		for (Direction renderSide : Direction.values()) {
			list = ibakedmodel.getQuads(pattern, renderSide, randomSource, ModelData.EMPTY, null);
			if (!list.isEmpty()) {
				blockpos$mutableblockpos.setWithOffset(renderAtPos, renderSide);
				modelBlockRenderer.renderModelFaceAO(level, pattern, renderAtPos, matrixStackIn, chunksConsumer, list,
						afloat, bitset, modelblockrenderer$ambientocclusionface, combinedOverlayIn);
			}
		}

		// Now draw the REAL block overtop, but with BlendFunction set to EQUALS --
		// Meaning it'll only draw where theres already a pixel exiting from the pattern
		// above
		VertexConsumer builder2 = bufferIn.getBuffer(OurRenderTypes.RenderBlockBackface);
		DireVertexConsumer chunksConsumer2 = new DireVertexConsumer(builder2, transparency);
		BakedModel ibakedmodel2 = blockrendererdispatcher.getBlockModel(renderState);

		List<BakedQuad> list2;
		for (Direction renderSide : Direction.values()) {
			Direction newDirection = getDirection(direction, renderSide); // Because we've rotated it, we need to draw
																			// the correct ambient occlusion side
			modelBlockRenderer.setDirection(newDirection); // Overrode BlockModelRenderer to allow this
			list2 = ibakedmodel2.getQuads(renderState, renderSide, randomSource, ModelData.EMPTY, null);
			if (!list2.isEmpty()) {
				blockpos$mutableblockpos.setWithOffset(renderAtPos, renderSide);
				modelBlockRenderer.renderModelFaceAO(level, renderState, renderAtPos, matrixStackIn, chunksConsumer2,
						list2, afloat, bitset, modelblockrenderer$ambientocclusionface, combinedOverlayIn);
			}
		}

		matrixStackIn.popPose();

	}

	public Direction getDirection(Direction facing, Direction renderSide) {
		return switch (renderSide) {
			case UP -> facing;
			case DOWN -> facing.getOpposite();
			case WEST -> facing == Direction.DOWN || facing == Direction.UP ? Direction.WEST : facing.getClockWise();
			case EAST ->
				facing == Direction.DOWN || facing == Direction.UP ? Direction.EAST : facing.getCounterClockWise();
			case NORTH -> switch (facing) {
				case DOWN -> Direction.SOUTH;
				case UP -> Direction.NORTH;
				default -> Direction.UP;
			};
			case SOUTH -> switch (facing) {
				case DOWN -> Direction.NORTH;
				case UP -> Direction.SOUTH;
				default -> Direction.DOWN;
			};
		};
	}

}
