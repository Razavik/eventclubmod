package com.eventclubmod.features;

import com.eventclubmod.storage.NoteStorage;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WelcomeNoteFeature {
	private static final Logger LOGGER = LoggerFactory.getLogger("eventclubmod:welcome_note");
	private final NoteStorage storage;

	public WelcomeNoteFeature(NoteStorage storage) {
		this.storage = storage;
	}

	public void processOnline(MinecraftServer server) {
		server.getPlayerManager().getPlayerList().forEach(p -> giveIfNeeded(server, p));
	}

	public void giveIfNeeded(MinecraftServer server, ServerPlayerEntity player) {
		if (player == null || player.getUuid() == null)
			return;
		if (storage.has(player.getUuid()))
			return;

		ItemStack note = new ItemStack(Items.PAPER);
		// Тёмно-серый курсив
		note.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Неизвестный ожидает вас в растущую луну.")
				.styled(s -> s.withItalic(true).withColor(0x555555)));

		boolean ok = player.getInventory().insertStack(note.copy());
		if (!ok) {
			player.giveItemStack(note);
		}

		// Звук при выдаче записки: глухой атмосферный звук (можно заменить по желанию)
		var w = player.getWorld();
		if (!w.isClient && w instanceof net.minecraft.server.world.ServerWorld sw) {
			sw.playSound(
					null,
					player.getX(), player.getY(), player.getZ(),
					SoundEvents.AMBIENT_CAVE,
					SoundCategory.AMBIENT,
					5.0f,
					5.0f);
		}

		player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0));
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 255));

		storage.add(player.getUuid());
		storage.save(server);
		LOGGER.debug("Выдана приветственная записка игроку {}", player.getName().getString());
	}
}
