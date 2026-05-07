package com.direwolf20.justdirethings.common.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class JustDireFluidType extends FluidType {
    private static final ResourceLocation STILL_TEXTURE = new ResourceLocation("minecraft", "block/water_still");
    private static final ResourceLocation FLOW_TEXTURE = new ResourceLocation("minecraft", "block/water_flow");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("minecraft", "block/water_overlay");

    private final int tintColor;

    public JustDireFluidType(int tintColor, Properties properties) {
        super(properties);
        this.tintColor = tintColor;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() { return STILL_TEXTURE; }

            @Override
            public ResourceLocation getFlowingTexture() { return FLOW_TEXTURE; }

            @Override
            public ResourceLocation getOverlayTexture() { return OVERLAY_TEXTURE; }

            @Override
            public int getTintColor() { return tintColor; }
        });
    }
}
