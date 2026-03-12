package org.jan1k.voicechatmute;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jan1k.voicechatmute.cache.MuteCache;
import org.jan1k.voicechatmute.config.ConfigManager;

public class VoicechatMute extends JavaPlugin {

    private static VoicechatMute instance;
    private ConfigManager configManager;
    private MuteCache muteCache;

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.muteCache = new MuteCache();

        int pluginId = 30073;
        new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        if (this.muteCache != null) {
            this.muteCache.clear();
        }
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
}
