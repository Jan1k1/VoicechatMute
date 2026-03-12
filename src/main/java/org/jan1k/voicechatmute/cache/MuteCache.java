package org.jan1k.voicechatmute.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MuteCache {

    private final Map<UUID, String> mutedPlayers = new ConcurrentHashMap<>();

    public void setMuted(UUID uuid, String reason) {
        if (reason == null) {
            this.mutedPlayers.remove(uuid);
        } else {
            this.mutedPlayers.put(uuid, reason);
        }
    }

    public String getMuteReason(UUID uuid) {
        return this.mutedPlayers.get(uuid);
    }

    public boolean isMuted(UUID uuid) {
        return this.mutedPlayers.containsKey(uuid);
    }

    public java.util.Set<UUID> getMutedPlayers() {
        return this.mutedPlayers.keySet();
    }

    public void remove(UUID uuid) {
        this.mutedPlayers.remove(uuid);
    }

    public void clear() {
        this.mutedPlayers.clear();
    }
}
