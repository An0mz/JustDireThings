package com.direwolf20.justdirethings.client.blockentityrenders;

import com.direwolf20.justdirethings.client.blockentityrenders.baseber.AreaAffectingBER;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ExperienceHolderBER extends AreaAffectingBER {
	private static final ItemStack XP_BOTTLE = new ItemStack(Items.EXPERIENCE_BOTTLE);

	public ExperienceHolderBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(BlockEntity blockentity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightsIn, int combinedOverlayIn) {
		super.render(blockentity, partialTicks, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn);

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		float angle = ((System.currentTimeMillis() / 15) % 360);

		matrixStackIn.pushPose();
		matrixStackIn.translate(0.5, 0.8, 0.5);
		matrixStackIn.mulPose(Axis.YP.rotationDegrees(angle));
		matrixStackIn.scale(0.2f, 0.2f, 0.2f);
		itemRenderer.renderStatic(XP_BOTTLE, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, combinedOverlayIn,
				matrixStackIn, bufferIn, Minecraft.getInstance().level, 0);
		matrixStackIn.popPose();
	}
}
