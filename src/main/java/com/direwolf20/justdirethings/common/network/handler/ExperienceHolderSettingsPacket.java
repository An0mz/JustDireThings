package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.direwolf20.justdirethings.common.containers.ExperienceHolderContainer;
import com.direwolf20.justdirethings.common.network.data.ExperienceHolderSettingsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExperienceHolderSettingsPacket {
	public static void handle(final ExperienceHolderSettingsPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			AbstractContainerMenu container = sender.containerMenu;
			if (container instanceof ExperienceHolderContainer experienceHolderContainer
					&& experienceHolderContainer.baseMachineBE instanceof ExperienceHolderBE be) {
				be.changeSettings(sender, payload.targetExp(), payload.ownerOnly(), payload.collectExp(),
						payload.showParticles());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
