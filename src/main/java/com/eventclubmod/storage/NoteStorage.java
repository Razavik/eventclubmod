package com.eventclubmod.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoteStorage {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type TYPE = new TypeToken<Set<String>>() {}.getType();

	private final Set<UUID> given = new HashSet<>();

	private Path resolvePath(MinecraftServer server) {
		Path base = server.getRunDirectory();
		return base.resolve("config").resolve("eventclubmod").resolve("given_notes.json");
	}

	public void load(MinecraftServer server) {
		Path path = resolvePath(server);
		given.clear();
		if (Files.exists(path)) {
			try (Reader r = Files.newBufferedReader(path)) {
				Set<String> data = GSON.fromJson(r, TYPE);
				if (data != null) {
					for (String s : data) {
						try { given.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
					}
				}
			} catch (IOException ignored) {
			}
		}
	}

	public void save(MinecraftServer server) {
		Path path = resolvePath(server);
		try {
			Files.createDirectories(path.getParent());
			Set<String> out = new HashSet<>();
			for (UUID id : given) out.add(id.toString());
			try (Writer w = Files.newBufferedWriter(path)) {
				GSON.toJson(out, TYPE, w);
			}
		} catch (IOException ignored) {
		}
	}

	public boolean has(UUID id) {
		return given.contains(id);
	}

	public void add(UUID id) {
		given.add(id);
	}

	public Set<UUID> view() {
		return Collections.unmodifiableSet(given);
	}
}
