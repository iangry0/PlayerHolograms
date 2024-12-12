package me.iangry.playerholograms.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIManager {

    public static void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Hologram Manager");

        ItemStack createHologram = new ItemStack(Material.NAME_TAG);
        ItemMeta createMeta = createHologram.getItemMeta();
        createMeta.setDisplayName(ChatColor.YELLOW + "Create Hologram");
        createMeta.setLore(List.of(ChatColor.GREEN + "Uses your current location"));
        createHologram.setItemMeta(createMeta);

        ItemStack listeditHologram = new ItemStack(Material.PAPER);
        ItemMeta listeditMeta = listeditHologram.getItemMeta();
        listeditMeta.setDisplayName(ChatColor.BLUE + "List & Edit Holograms");
        listeditHologram.setItemMeta(listeditMeta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "Exit");
        exit.setItemMeta(exitMeta);

        gui.setItem(11, createHologram);
        gui.setItem(13, listeditHologram);
        gui.setItem(15, exit);

        player.openInventory(gui);
    }
}
