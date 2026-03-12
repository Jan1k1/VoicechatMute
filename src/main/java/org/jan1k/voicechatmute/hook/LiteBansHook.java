package org.jan1k.voicechatmute.hook;

import litebans.api.Database;
import litebans.api.Entry;
import litebans.api.Events;
import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.cache.MuteCache;

import java.util.UUID;

public class LiteBansHook implements PunishmentHook {


    private final VoicechatMute plugin;
    private final MuteCache cache;

    public LiteBansHook(VoicechatMute plugin) {
        this.plugin = plugin;
        this.cache = plugin.getMuteCache();
    }

    @Override
    public void register() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                if ("mute".equals(entry.getType()) && entry.getUuid() != null) {
                    try {
                        String reason = entry.getReason();
                        if (reason == null) reason = "Muted";
                        cache.setMuted(UUID.fromString(entry.getUuid()), reason);
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            @Override
            public void entryRemoved(Entry entry) {
                if ("mute".equals(entry.getType()) && entry.getUuid() != null) {
                    try {
                        cache.setMuted(UUID.fromString(entry.getUuid()), null);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        });
    }

    @Override
    public void unregister() {
    }

    @Override
    public String getMuteReason(UUID uuid) {
        if (Database.get().isPlayerMuted(uuid, null)) {
            return "Muted";
        }
        return null;
    }
}
