package com.eventclubmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.eventclubmod.registry.ModEntities;
import com.eventclubmod.client.render.ShadowNpcRenderer;

// Клиентская инициализация: живёт в src/client/java
public class EventClubModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.SHADOW_NPC, ShadowNpcRenderer::new);
    }
}
