package com.direwolf20.justdirethings.client.renderers.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Avoids texture slot collisions with lightmap/overlay by skipping to slot 3
 * after slot 0.
 */
public class FixedMultiTextureStateShard extends RenderStateShard.EmptyTextureStateShard {
	private final Optional<ResourceLocation> cutoutTexture;

	public FixedMultiTextureStateShard(List<ShaderTexture> textures) {
		super(() -> {
			int slot = 0;
			for (ShaderTexture texture : textures) {
				TextureManager textureManager = Minecraft.getInstance().getTextureManager();
				textureManager.getTexture(texture.location()).setFilter(texture.blur(), texture.mipmap());
				RenderSystem.setShaderTexture(slot, texture.location());
				slot = (slot == 0) ? 3 : slot + 1;
			}
		}, () -> {
		});
		this.cutoutTexture = textures.stream().findFirst().map(ShaderTexture::location);
	}

	@Override
	protected @NotNull Optional<ResourceLocation> cutoutTexture() {
		return this.cutoutTexture;
	}
}
