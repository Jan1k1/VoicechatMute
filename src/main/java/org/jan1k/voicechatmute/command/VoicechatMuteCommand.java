package org.jan1k.voicechatmute.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jan1k.voicechatmute.VoicechatMute;

import java.util.List;

public class VoicechatMuteCommand implements CommandExecutor, TabCompleter {

    private final VoicechatMute plugin;

    public VoicechatMuteCommand(VoicechatMute plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voicechatmute.admin")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No permission.</red>"));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            this.plugin.getConfigManager().loadConfig();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>VoicechatMute config reloaded.</green>"));
            return true;
        }
        sender.sendMessage(Component.text("Usage: /voicechatmute reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return List.of();
    }
}
