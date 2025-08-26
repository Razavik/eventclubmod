package com.eventclubmod;

import net.fabricmc.api.ModInitializer;

// Команды (Fabric API v2)
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import com.eventclubmod.storage.NoteStorage;
import com.eventclubmod.features.WelcomeNoteFeature;
import com.eventclubmod.features.shadow.ShadowNpcManager;
import com.eventclubmod.command.ModCommands;
import com.eventclubmod.config.ModConfig;
import com.eventclubmod.registry.ModEntities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventClubMod implements ModInitializer {
	public static final String MOD_ID = "eventclubmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private final NoteStorage noteStorage = new NoteStorage();
	private WelcomeNoteFeature welcomeNoteFeature;
	private ModConfig config;
	private final ShadowNpcManager shadowNpcManager = new ShadowNpcManager();

	@Override
	public void onInitialize() {
		LOGGER.info("EventClub Mod загружен");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.register(dispatcher, this, shadowNpcManager);
		});

		// Регистрация атрибутов кастомных сущностей
		ModEntities.initAttributes();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> onServerStarted(server));
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> onServerStopping(server));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (config != null && config.enableWelcomeNote && welcomeNoteFeature != null) {
				ServerPlayerEntity player = handler.player;
				welcomeNoteFeature.giveIfNeeded(server, player);
			}
		});
	}

	private void onServerStarted(MinecraftServer server) {
		this.config = ModConfig.load(server);
		noteStorage.load(server);

		if (config.enableWelcomeNote) {
			this.welcomeNoteFeature = new WelcomeNoteFeature(noteStorage);
			welcomeNoteFeature.processOnline(server);
		} else {
			this.welcomeNoteFeature = null;
		}

		if (config.enableShadowNpc) {
			shadowNpcManager.spawnShadows(server, config);
		}
	}

	private void onServerStopping(MinecraftServer server) {
		noteStorage.save(server);
		if (config != null)
			config.save(server);
		// Удалим наших Shadow NPC при остановке (на всякий случай)
		shadowNpcManager.despawnShadows(server);
	}

	public boolean isAllowedOperator(ServerCommandSource source) {
		String name = source.getName();
		return name.equalsIgnoreCase("Razavik")
				|| name.equalsIgnoreCase("Neaplitos");
	}

	// Включение/выключение фичи Welcome Note во время работы сервера
	public void toggleWelcome(boolean state, ServerCommandSource source) {
		MinecraftServer server = source.getServer();
		if (config == null) {
			config = new ModConfig();
		}
		config.enableWelcomeNote = state;
		config.save(server);

		if (state) {
			if (welcomeNoteFeature == null) {
				welcomeNoteFeature = new WelcomeNoteFeature(noteStorage);
			}
			welcomeNoteFeature.processOnline(server);
			source.sendFeedback(() -> Text.literal("WelcomeNote включён"), true);
		} else {
			welcomeNoteFeature = null;
			source.sendFeedback(() -> Text.literal("WelcomeNote выключен"), true);
		}
	}

	// Доступ к конфигу для команд/менеджеров
	public ModConfig getConfigOrCreate(MinecraftServer server) {
		if (this.config == null) {
			this.config = new ModConfig();
			this.config.save(server);
		}
		return this.config;
	}
}