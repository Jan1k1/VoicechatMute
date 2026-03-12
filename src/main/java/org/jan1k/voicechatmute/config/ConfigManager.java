package org.jan1k.voicechatmute.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.jan1k.voicechatmute.VoicechatMute;

public class ConfigManager {

    private final VoicechatMute plugin;
    private FileConfiguration config;

    public ConfigManager(VoicechatMute plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
    }

    public boolean isHookEnabled(String hookName) {
        return this.config.getBoolean("settings.hooks." + hookName.toLowerCase(), false);
    }

    public String getMessage(String path) {
        return this.config.getString("messages." + path, "");
    }
}
