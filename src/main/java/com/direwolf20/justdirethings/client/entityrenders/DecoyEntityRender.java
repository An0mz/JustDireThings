package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.common.entities.DecoyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

public class DecoyEntityRender extends LivingEntityRenderer<DecoyEntity, PlayerModel<DecoyEntity>> {

	public static final UUID DEFAULT_UUID = UUID.fromString("0192723f-b3dc-495a-959f-52c53fa63bff");

	public DecoyEntityRender(EntityRendererProvider.Context context) {
		super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
		this.addLayer(new HumanoidArmorLayer<DecoyEntity, PlayerModel<DecoyEntity>, HumanoidArmorModel<DecoyEntity>>(
				this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
		this.addLayer(new ArrowLayer<>(context, this));
		this.addLayer(new CustomHeadLayer<DecoyEntity, PlayerModel<DecoyEntity>>(this, context.getModelSet(),
				context.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(DecoyEntity entity) {
		Optional<UUID> ownerUUID = entity.getOwnerUUID();
		UUID uuid = ownerUUID.orElse(DEFAULT_UUID);
		if (Minecraft.getInstance().getConnection() != null) {
			PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
			if (info != null)
				return info.getSkinLocation();
		}
		return DefaultPlayerSkin.getDefaultSkin(uuid);
	}
}
