package com.eventclubmod.registry;

import com.eventclubmod.EventClubMod;
import com.eventclubmod.entity.ShadowNpcEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public final class ModEntities {
	private static final Identifier SHADOW_NPC_ID = Identifier.of(EventClubMod.MOD_ID, "shadow_npc");
	private static final RegistryKey<EntityType<?>> SHADOW_NPC_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, SHADOW_NPC_ID);

	public static final EntityType<ShadowNpcEntity> SHADOW_NPC = Registry.register(
			Registries.ENTITY_TYPE,
			SHADOW_NPC_KEY,
			FabricEntityTypeBuilder
					.create(SpawnGroup.MISC, ShadowNpcEntity::new)
					.dimensions(EntityDimensions.fixed(0.6f, 1.8f))
					.build(SHADOW_NPC_KEY)
	);

	public static void initAttributes() {
		FabricDefaultAttributeRegistry.register(SHADOW_NPC, ShadowNpcEntity.createAttributes());
	}
}
