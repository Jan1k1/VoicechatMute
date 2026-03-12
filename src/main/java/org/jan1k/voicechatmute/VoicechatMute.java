package org.jan1k.voicechatmute;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jan1k.voicechatmute.cache.MuteCache;
import org.jan1k.voicechatmute.command.VoicechatMuteCommand;
import org.jan1k.voicechatmute.config.ConfigManager;
import org.jan1k.voicechatmute.hook.AdvancedBansHook;
import org.jan1k.voicechatmute.hook.LiteBansHook;

import org.jan1k.voicechatmute.hook.PunishmentHook;
import org.jan1k.voicechatmute.hook.SimpleVoiceChatHook;
import org.jan1k.voicechatmute.hook.VoiceHook;
import org.jan1k.voicechatmute.listener.PlayerJoinListener;
import org.jan1k.voicechatmute.util.NotificationManager;

import java.util.ArrayList;
import java.util.List;

public class VoicechatMute extends JavaPlugin {

    private static VoicechatMute instance;
    private ConfigManager configManager;
    private MuteCache muteCache;
    private NotificationManager notificationManager;

    private final List<PunishmentHook> punishmentHooks = new ArrayList<>();
    private final List<VoiceHook> voiceHooks = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.muteCache = new MuteCache();
        this.notificationManager = new NotificationManager(this);

        new Metrics(this, 30073);

        registerHooks();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, this.punishmentHooks), this);

        VoicechatMuteCommand cmd = new VoicechatMuteCommand(this);
        getCommand("voicechatmute").setExecutor(cmd);
        getCommand("voicechatmute").setTabCompleter(cmd);

        startCacheCleanupTask();
    }

    private void startCacheCleanupTask() {
        getServer().getAsyncScheduler().runAtFixedRate(this, task -> {
            getServer().getOnlinePlayers().forEach(player -> {
                java.util.UUID uuid = player.getUniqueId();
                String reason = null;
                for (PunishmentHook hook : this.punishmentHooks) {
                    reason = hook.getMuteReason(uuid);
                    if (reason != null) break;
                }
                this.muteCache.setMuted(uuid, reason);
            });
        }, 5L, 10L, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        this.voiceHooks.forEach(VoiceHook::unregister);
        this.punishmentHooks.forEach(PunishmentHook::unregister);
        if (this.muteCache != null) {
            this.muteCache.clear();
        }
    }

    private void registerHooks() {
        if (this.configManager.isHookEnabled("litebans") && getServer().getPluginManager().isPluginEnabled("LiteBans")) {
            registerPunishmentHook(new LiteBansHook(this), "LiteBans");
        }
        if (this.configManager.isHookEnabled("advancedban") && getServer().getPluginManager().isPluginEnabled("AdvancedBan")) {
            registerPunishmentHook(new AdvancedBansHook(this), "AdvancedBan");
        }
        if (this.configManager.isHookEnabled("simplevoicechat") && getServer().getPluginManager().isPluginEnabled("voicechat")) {
            registerVoiceHook(new SimpleVoiceChatHook(this, this.notificationManager), "Simple Voice Chat");
        }
    }

    private void registerPunishmentHook(PunishmentHook hook, String name) {
        hook.register();
        this.punishmentHooks.add(hook);
        getLogger().info("Hooked into " + name + ".");
    }

    private void registerVoiceHook(VoiceHook hook, String name) {
        hook.register();
        this.voiceHooks.add(hook);
        getLogger().info("Hooked into " + name + ".");
    }

    public static VoicechatMute getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public MuteCache getMuteCache() {
        return this.muteCache;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }
}
