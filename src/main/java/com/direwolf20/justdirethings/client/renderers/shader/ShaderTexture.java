package com.direwolf20.justdirethings.client.renderers.shader;

import net.minecraft.resources.ResourceLocation;

// Small value object for shader texture bindings.
public record ShaderTexture(ResourceLocation location, boolean blur, boolean mipmap) {
    public ShaderTexture(ResourceLocation location, boolean blur) {
        this(location, blur, false);
    }

    public ShaderTexture(ResourceLocation location) {
        this(location, false, false);
    }
}

