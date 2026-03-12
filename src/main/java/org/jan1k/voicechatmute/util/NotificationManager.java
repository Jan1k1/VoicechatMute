package org.jan1k.voicechatmute.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.config.ConfigManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationManager {

    private final VoicechatMute plugin;
    private final Map<UUID, Long> lastWarning = new ConcurrentHashMap<>();

    public NotificationManager(VoicechatMute plugin) {
        this.plugin = plugin;
    }

    public void warnPlayer(UUID uuid) {
        long now = System.currentTimeMillis();
        long last = this.lastWarning.getOrDefault(uuid, 0L);
        if (now - last < 3000L) {
            return;
        }
        this.lastWarning.put(uuid, now);

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        Runnable task = () -> {
            ConfigManager config = this.plugin.getConfigManager();
            String actionbar = config.getMessage("muted-actionbar");
            if (actionbar != null && !actionbar.isEmpty()) {
                player.sendActionBar(parse(actionbar));
            }

            String chat = config.getMessage("muted-chat");
            if (chat != null && !chat.isEmpty()) {
                player.sendMessage(parse(chat));
            }
        };

        try {
            player.getScheduler().run(this.plugin, scheduledTask -> task.run(), null);
        } catch (NoSuchMethodError e) {
            Bukkit.getScheduler().runTask(this.plugin, task);
        }
    }

    private Component parse(String text) {
        if (text.contains("<") && text.contains(">")) {
            return MiniMessage.miniMessage().deserialize(text);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
