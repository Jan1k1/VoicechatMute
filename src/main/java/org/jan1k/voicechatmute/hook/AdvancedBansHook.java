package org.jan1k.voicechatmute.hook;

import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.cache.MuteCache;

import java.util.UUID;

public class AdvancedBansHook implements PunishmentHook, Listener {

    private final VoicechatMute plugin;
    private final MuteCache cache;

    public AdvancedBansHook(VoicechatMute plugin) {
        this.plugin = plugin;
        this.cache = this.plugin.getMuteCache();
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean isMuted(UUID uuid) {
        String uuidStr = uuid.toString().replace("-", "");
        if (PunishmentManager.get().hasPunishment(uuidStr, PunishmentType.MUTE)) {
            return true;
        }
        if (PunishmentManager.get().hasPunishment(uuidStr, PunishmentType.TEMP_MUTE)) {
            return true;
        }
        String uuidDashed = uuid.toString();
        if (PunishmentManager.get().hasPunishment(uuidDashed, PunishmentType.MUTE)) {
            return true;
        }
        return PunishmentManager.get().hasPunishment(uuidDashed, PunishmentType.TEMP_MUTE);
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        PunishmentType type = event.getPunishment().getType();
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            String uuidStr = event.getPunishment().getUuid();
            if (uuidStr != null) {
                if (uuidStr.length() == 32) {
                    uuidStr = uuidStr.substring(0, 8) + "-" + uuidStr.substring(8, 12) + "-" + uuidStr.substring(12, 16) + "-" + uuidStr.substring(16, 20) + "-" + uuidStr.substring(20, 32);
                }
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    this.cache.setMuted(uuid, true);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    @EventHandler
    public void onRevoke(RevokePunishmentEvent event) {
        PunishmentType type = event.getPunishment().getType();
        if (type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE) {
            String uuidStr = event.getPunishment().getUuid();
            if (uuidStr != null) {
                if (uuidStr.length() == 32) {
                    uuidStr = uuidStr.substring(0, 8) + "-" + uuidStr.substring(8, 12) + "-" + uuidStr.substring(12, 16) + "-" + uuidStr.substring(16, 20) + "-" + uuidStr.substring(20, 32);
                }
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    this.cache.setMuted(uuid, false);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }
}
