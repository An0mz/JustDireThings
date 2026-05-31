package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.common.entities.JustDireArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class JustDireArrowRenderer extends ArrowRenderer<JustDireArrow> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/projectiles/arrow.png");

	public JustDireArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(JustDireArrow entity) {
		return TEXTURE;
	}
}
