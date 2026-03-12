package org.jan1k.voicechatmute.hook;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.Bukkit;
import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.cache.MuteCache;
import org.jan1k.voicechatmute.util.NotificationManager;

import java.util.UUID;

public class SimpleVoiceChatHook implements VoiceHook, VoicechatPlugin {

    private final VoicechatMute plugin;
    private final MuteCache cache;
    private final NotificationManager notifications;
    private BukkitVoicechatService service;

    public SimpleVoiceChatHook(VoicechatMute plugin, NotificationManager notifications) {
        this.plugin = plugin;
        this.cache = this.plugin.getMuteCache();
        this.notifications = notifications;
    }

    @Override
    public void register() {
        this.service = Bukkit.getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (this.service != null) {
            this.service.registerPlugin(this);
        }
    }

    @Override
    public void unregister() {
        // No unregister method in BukkitVoicechatService 2.5.0
    }

    @Override
    public String getPluginId() {
        return "voicechatmute";
    }

    @Override
    public void initialize(VoicechatApi api) {
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        if (event.getSenderConnection() == null) {
            return;
        }
        UUID playerUuid = event.getSenderConnection().getPlayer().getUuid();
        if (this.cache.isMuted(playerUuid)) {
            event.cancel();
            this.notifications.warnPlayer(playerUuid);
        }
    }
}
