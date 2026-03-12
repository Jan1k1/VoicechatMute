package org.jan1k.voicechatmute.hook;

import org.jan1k.voicechatmute.VoicechatMute;
import org.jan1k.voicechatmute.util.NotificationManager;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.events.PlayerSpeakEvent;

public class PlasmoVoiceHook implements VoiceHook {

    private final VoicechatMute plugin;
    private final NotificationManager notifications;
    private PlasmoVoiceServer voiceServer;

    public PlasmoVoiceHook(VoicechatMute plugin, NotificationManager notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    @Override
    public void register() {
         this.voiceServer = su.plo.voice.api.server.PlasmoVoiceServer.getPlasmoVoiceServer();
         if (this.voiceServer != null) {
             this.voiceServer.getEventBus().register(this.plugin, this);
         }
    }

    @Override
    public void unregister() {
         if (this.voiceServer != null) {
             this.voiceServer.getEventBus().unregister(this.plugin, this);
         }
    }

    @EventSubscribe
    public void onMicrophone(PlayerSpeakEvent event) {
        if (this.plugin.getMuteCache().isMuted(event.getPlayer().getInstance().getUUID())) {
            event.setCancelled(true);
            this.notifications.warnPlayer(event.getPlayer().getInstance().getUUID());
        }
    }
}
