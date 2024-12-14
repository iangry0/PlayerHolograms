package me.iangry.playerholograms.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerHologramsPlugin implements CommandExecutor {

    private final me.iangry.playerholograms.HologramGUI plugin;

    public PlayerHologramsPlugin(me.iangry.playerholograms.HologramGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Handle reload command
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("playerholograms.reload")) {
                // Reload and save the config
                plugin.reloadConfig();
                plugin.saveConfig();


                // Feedback to the player
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&aPlayerHolograms&2]"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReload Complete"));
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
            }
            return true;
        }

        // If no arguments, show basic information
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&aPlayerHolograms&2]"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDeveloper: &7iAngry"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVersion: &7" + plugin.getDescription().getVersion()));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCommands: &7/HologramGUI"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCommands: &7/PlayerHologramsPlugin Reload"));
            return true;
        }

        // If an unknown command is used
        sender.sendMessage(ChatColor.RED + "Unknown command. Use '/HologramGUI'");
        return false;
    }
}
