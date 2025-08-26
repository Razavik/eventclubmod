package com.eventclubmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	// Этап 1: выдача записки-приветствия
	public boolean enableWelcomeNote = false;

	// Этап 2: Shadow NPC (чёрный силуэт, голова опущена, парит на месте)
	public boolean enableShadowNpc = false;
	public List<ShadowPos> shadowNpcs = new ArrayList<>();

	private static Path resolvePath(MinecraftServer server) {
		return server.getRunDirectory()
				.resolve("config")
				.resolve("eventclubmod")
				.resolve("config.json");
	}

	public static ModConfig load(MinecraftServer server) {
		Path path = resolvePath(server);
		try {
			if (Files.exists(path)) {
				try (Reader r = Files.newBufferedReader(path)) {
					ModConfig cfg = GSON.fromJson(r, ModConfig.class);
					if (cfg != null)
						return cfg;
				}
			}
		} catch (IOException ignored) {
		}
		return new ModConfig();
	}

	public void save(MinecraftServer server) {
		Path path = resolvePath(server);
		try {
			Files.createDirectories(path.getParent());
			try (Writer w = Files.newBufferedWriter(path)) {
				GSON.toJson(this, w);
			}
		} catch (IOException ignored) {
		}
	}

	// Позиция для Shadow NPC
	public static class ShadowPos {
		public String world = "minecraft:overworld";
		public double x = 1370.0;
		public double y = 120.0;
		public double z = 210.0;

		public ShadowPos() {
		}

		public ShadowPos(String world, double x, double y, double z) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
