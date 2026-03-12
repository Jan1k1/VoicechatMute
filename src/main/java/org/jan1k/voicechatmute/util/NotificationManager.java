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
    private final Map<UUID, Long> lastActionBarWarning = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastChatWarning = new ConcurrentHashMap<>();

    public NotificationManager(VoicechatMute plugin) {
        this.plugin = plugin;
    }

    public void warnPlayer(UUID uuid, String reason) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        boolean sendActionBar = (now - this.lastActionBarWarning.getOrDefault(uuid, 0L)) >= 3000L;
        boolean sendChat = (now - this.lastChatWarning.getOrDefault(uuid, 0L)) >= 60000L;

        if (!sendActionBar && !sendChat) {
            return;
        }

        if (sendActionBar) this.lastActionBarWarning.put(uuid, now);
        if (sendChat) this.lastChatWarning.put(uuid, now);

        Runnable task = () -> {
            ConfigManager config = this.plugin.getConfigManager();
            String actionbarTemplate = config.getMessage("muted-actionbar");
            if (sendActionBar && actionbarTemplate != null && !actionbarTemplate.isEmpty()) {
                String msg = actionbarTemplate.replace("%reason%", reason != null ? reason : "Muted");
                player.sendActionBar(parse(msg));
            }

            String chatTemplate = config.getMessage("muted-chat");
            if (sendChat && chatTemplate != null && !chatTemplate.isEmpty()) {
                String msg = chatTemplate.replace("%reason%", reason != null ? reason : "Muted");
                player.sendMessage(parse(msg));
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
