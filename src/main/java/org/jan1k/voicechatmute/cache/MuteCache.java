package org.jan1k.voicechatmute.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MuteCache {

    private final Map<UUID, Boolean> mutedPlayers = new ConcurrentHashMap<>();

    public void setMuted(UUID uuid, boolean muted) {
        this.mutedPlayers.put(uuid, muted);
    }

    public boolean isMuted(UUID uuid) {
        return this.mutedPlayers.getOrDefault(uuid, false);
    }

    public void remove(UUID uuid) {
        this.mutedPlayers.remove(uuid);
    }

    public void clear() {
        this.mutedPlayers.clear();
    }
}
