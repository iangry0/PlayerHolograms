package me.iangry.playerholograms.commands;

import me.iangry.playerholograms.gui.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HologramCommand implements CommandExecutor {

    private final me.iangry.playerholograms.HologramGUI plugin;

    public HologramCommand(me.iangry.playerholograms.HologramGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only for players.");
            return true;
        }

        Player player = (Player) sender;
        if (player.hasPermission("playerholograms.use")) {
            player.sendMessage(ChatColor.GREEN + "Opening Player Holograms GUI...");
            GUIManager.openMainMenu(player);
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to create holograms.");
            return true;
        }
    }
}