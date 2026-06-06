package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.entitymodels.PortalProjectileModel;
import com.direwolf20.justdirethings.common.entities.PortalProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class PortalProjectileRenderer extends EntityRenderer<PortalProjectile> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation(JustDireThings.MODID,
			"textures/entity/portal_projectile.png");
	protected final PortalProjectileModel model;

	public PortalProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new PortalProjectileModel(context.bakeLayer(PortalProjectileModel.Portal_Projectile_Layer));
	}

	@Override
	public void render(PortalProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
		float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		float f2 = entity.tickCount + partialTicks;

		poseStack.translate(0.0, 0.15, 0.0);
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(f2 * 0.1f) * 180f));
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(f2 * 0.1f) * 180f));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f2 * 0.15f) * 360f));
		poseStack.scale(0.5f, 0.5f, 0.5f);

		// Tint: blue for primary portal, orange for secondary
		int color = entity.isPrimaryType() ? 0x44B5FF : 0xFF9A2E;
		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = (color & 0xFF) / 255f;

		this.model.setupAnim(entity, 0f, 0f, 0f, f, f1);
		VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
		this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1f);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(PortalProjectile entity) {
		return TEXTURE;
	}
}
