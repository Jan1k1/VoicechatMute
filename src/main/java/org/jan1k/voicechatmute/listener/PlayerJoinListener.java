package org.jan1k.voicechatmute.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.hook.PunishmentHook;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final VoicechatMute plugin;
    private final List<PunishmentHook> punishmentHooks;

    public PlayerJoinListener(VoicechatMute plugin, List<PunishmentHook> punishmentHooks) {
        this.plugin = plugin;
        this.punishmentHooks = punishmentHooks;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.plugin.getServer().getAsyncScheduler().runNow(this.plugin, task -> {
            boolean muted = this.punishmentHooks.stream().anyMatch(hook -> hook.isMuted(uuid));
            this.plugin.getMuteCache().setMuted(uuid, muted);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getMuteCache().remove(event.getPlayer().getUniqueId());
    }
}
