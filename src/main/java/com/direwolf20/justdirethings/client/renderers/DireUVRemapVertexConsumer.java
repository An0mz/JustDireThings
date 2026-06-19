package com.direwolf20.justdirethings.client.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;

/**
 * Remaps UV coordinates from a source sprite's atlas region to a target
 * sprite's atlas region. Used to render the goo spread pattern geometry with
 * the target block's texture instead of the pattern block's texture, enabling a
 * shader-compatible visual match.
 */
public class DireUVRemapVertexConsumer extends VertexConsumerWrapper {
	private final TextureAtlasSprite sourceSprite;
	private final TextureAtlasSprite targetSprite;

	public DireUVRemapVertexConsumer(VertexConsumer parent, TextureAtlasSprite sourceSprite,
			TextureAtlasSprite targetSprite) {
		super(parent);
		this.sourceSprite = sourceSprite;
		this.targetSprite = targetSprite;
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		float sourceWidth = sourceSprite.getU1() - sourceSprite.getU0();
		float sourceHeight = sourceSprite.getV1() - sourceSprite.getV0();
		float normalU = sourceWidth == 0f ? 0f : (u - sourceSprite.getU0()) / sourceWidth;
		float normalV = sourceHeight == 0f ? 0f : (v - sourceSprite.getV0()) / sourceHeight;
		float newU = targetSprite.getU0() + normalU * (targetSprite.getU1() - targetSprite.getU0());
		float newV = targetSprite.getV0() + normalV * (targetSprite.getV1() - targetSprite.getV0());
		parent.uv(newU, newV);
		return this;
	}
}
