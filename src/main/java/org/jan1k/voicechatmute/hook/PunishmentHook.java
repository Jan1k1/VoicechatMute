package org.jan1k.voicechatmute.hook;

import java.util.UUID;

public interface PunishmentHook {

    void register();

    void unregister();

    String getMuteReason(UUID uuid);
}
