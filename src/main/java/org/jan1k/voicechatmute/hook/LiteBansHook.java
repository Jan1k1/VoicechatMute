package org.jan1k.voicechatmute.hook;

import litebans.api.Database;
import litebans.api.Events;
import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.cache.MuteCache;

import java.util.UUID;

public class LiteBansHook implements PunishmentHook {

    private final VoicechatMute plugin;
    private final MuteCache cache;

    public LiteBansHook(VoicechatMute plugin) {
        this.plugin = plugin;
        this.cache = this.plugin.getMuteCache();
    }

    @Override
    public void register() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Events.Entry entry) {
                if ("mute".equals(entry.getType()) && entry.getUuid() != null) {
                    try {
                        UUID uuid = UUID.fromString(entry.getUuid());
                        cache.setMuted(uuid, true);
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            @Override
            public void entryRemoved(Events.Entry entry) {
                if ("mute".equals(entry.getType()) && entry.getUuid() != null) {
                    try {
                        UUID uuid = UUID.fromString(entry.getUuid());
                        cache.setMuted(uuid, false);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        });
    }

    @Override
    public void unregister() {
    }

    @Override
    public boolean isMuted(UUID uuid) {
        return Database.get().isPlayerMuted(uuid, null);
    }
}
