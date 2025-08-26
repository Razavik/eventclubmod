package com.eventclubmod.features.shadow;

import com.eventclubmod.EventClubMod;
import com.eventclubmod.config.ModConfig;
import com.eventclubmod.entity.ShadowNpcEntity;
import com.eventclubmod.registry.ModEntities;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ShadowNpcManager {
    // Жёстко заданные координаты и мир для единственного «Неизвестного»
    // Примечание: при необходимости поменяйте значения ниже.
    private static final String FIXED_WORLD_ID = "minecraft:overworld"; // мир
    private static final double FIXED_X = 0.5;
    private static final double FIXED_Y = 128.0;
    private static final double FIXED_Z = 0.5;

    // Переключение фичи в рантайме + сохранение конфига
    public void toggleShadow(boolean state, ServerCommandSource source, ModConfig config) {
        MinecraftServer server = source.getServer();
        if (config == null) {
            // Защитная проверка: если вдруг null, не падаем
            source.sendError(Text.literal("Config is null, cannot toggle ShadowNPC"));
            return;
        }
        config.enableShadowNpc = state;
        config.save(server);

        if (state) {
            spawnShadows(server, config);
            source.sendFeedback(() -> Text.literal("ShadowNPC включён (спавн в фиксированной точке)"), true);
        } else {
            despawnShadows(server);
            source.sendFeedback(() -> Text.literal("ShadowNPC выключен (тень удалена)"), true);
        }
    }

    // Спавн единственного Shadow NPC в жёстко заданной точке
    public void spawnShadows(MinecraftServer server, ModConfig config) {
        if (config == null || !config.enableShadowNpc) return;

        // Если хотя бы одна Тень уже существует — ничего не делаем (гарантия единственности)
        if (hasAnyShadow(server)) return;

        try {
            Identifier worldId = Identifier.tryParse(FIXED_WORLD_ID);
            if (worldId == null) return;
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, worldId);
            ServerWorld world = server.getWorld(worldKey);
            if (world == null) return;

            ShadowNpcEntity e = new ShadowNpcEntity(ModEntities.SHADOW_NPC, world);
            e.refreshPositionAndAngles(FIXED_X, FIXED_Y, FIXED_Z, 0f, 0f);
            e.setNoGravity(true);
            e.noClip = true;
            e.setPersistent();
            world.spawnEntity(e);
        } catch (Exception ex) {
            EventClubMod.LOGGER.warn("Не удалось заспавнить Shadow NPC: {}", ex.toString());
        }
    }

    // Удалить все Shadow NPC из всех миров
    public void despawnShadows(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            var list = world.getEntitiesByClass(ShadowNpcEntity.class,
                    new Box(-3.0E7, -3.0E7, -3.0E7, 3.0E7, 3.0E7, 3.0E7), e -> true);
            for (ShadowNpcEntity e : list) {
                e.discard();
            }
        }
    }

    // Есть ли хотя бы одна «Тень» в любом мире
    private boolean hasAnyShadow(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            var any = world.getEntitiesByClass(ShadowNpcEntity.class,
                    new Box(-3.0E7, -3.0E7, -3.0E7, 3.0E7, 3.0E7, 3.0E7), e -> true);
            if (!any.isEmpty()) return true;
        }
        return false;
    }
}
