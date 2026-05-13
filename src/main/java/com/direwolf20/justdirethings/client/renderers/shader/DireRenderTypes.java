package com.direwolf20.justdirethings.client.renderers.shader;

import com.direwolf20.justdirethings.JustDireThings;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class DireRenderTypes extends RenderType {
    private static ShaderInstance portalEntityShader;

    private static final Function<ResourceLocation, RenderType> PORTAL_ENTITY = Util.memoize(texture ->
            create("justdirethings_portal_entity",
                    DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                    CompositeState.builder()
                            .setShaderState(new ShaderStateShard(() -> portalEntityShader != null
                                    ? portalEntityShader
                                    : GameRenderer.getPositionTexShader()))
                            .setTextureState(new FixedMultiTextureStateShard(List.of(new ShaderTexture(texture))))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .createCompositeState(false))
    );

    public static RenderType portalEntity(ResourceLocation texture) {
        return PORTAL_ENTITY.apply(texture);
    }

    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "portal_entity"), DefaultVertexFormat.POSITION_TEX),
                shader -> portalEntityShader = shader
        );
    }

    private DireRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}

