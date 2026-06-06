package com.direwolf20.justdirethings.client.entitymodels;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.entities.PortalProjectile;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class PortalProjectileModel extends HierarchicalModel<PortalProjectile> {

	public static final ModelLayerLocation Portal_Projectile_Layer = new ModelLayerLocation(
			new ResourceLocation(JustDireThings.MODID, "portal_projectile"), "body");

	private final ModelPart root;
	private final ModelPart main;

	public PortalProjectileModel(ModelPart pRoot) {
		this.root = pRoot;
		this.main = pRoot.getChild("main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("main",
				CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -4f, -1f, 8f, 8f, 2f).texOffs(0, 10)
						.addBox(-1f, -4f, -4f, 2f, 8f, 8f).texOffs(20, 0).addBox(-4f, -1f, -4f, 8f, 2f, 8f),
				PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(PortalProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		this.main.yRot = netHeadYaw * (float) (Math.PI / 180.0);
		this.main.xRot = headPitch * (float) (Math.PI / 180.0);
	}
}
