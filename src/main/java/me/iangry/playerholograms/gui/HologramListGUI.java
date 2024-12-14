package me.iangry.playerholograms.gui;

import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.hologram.HologramManager;
import me.iangry.playerholograms.utils.AnvilManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class HologramListGUI implements Listener {

    private final HologramManager hologramManager;
    private final AnvilManager anvilManager;

    public HologramListGUI(HologramManager hologramManager, AnvilManager anvilManager) {
        this.hologramManager = hologramManager;
        this.anvilManager = anvilManager;
    }

    public void open(Player player) {
        List<String> holograms = hologramManager.listHolograms(player);
        int maxHolograms = hologramManager.getMaxHolograms(player);
        String maxHologramsStr = maxHolograms == -1 ? "*" : String.valueOf(maxHolograms);
        String title = "Your Holograms (" + holograms.size() + "/" + maxHologramsStr + ")";

        int rows = (int) Math.ceil(holograms.size() / 9.0);
        rows = Math.max(1, rows); // Ensure at least 1 row
        int size = rows * 9;

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String hologram : holograms) {
            List<String> lines = hologramManager.getHologramLines(hologram); // Fetch hologram text lines
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            String displayName = lines.isEmpty() ? hologram : String.join("\n", lines);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName)); // Translate color codes

            meta.setLore(List.of(
                    "",
                    ChatColor.GREEN + "Left-click to edit",
                    ChatColor.RED + "Right-click to delete"
            ));
            item.setItemMeta(meta);

            // Store hologram name in item metadata
            item = setHologramName(item, hologram);

            inventory.addItem(item);
        }

        player.openInventory(inventory);
    }

    private ItemStack setHologramName(ItemStack item, String hologramName) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(HologramGUI.getInstance(), "hologramName"), PersistentDataType.STRING, hologramName);
        item.setItemMeta(meta);
        return item;
    }

    private String getHologramName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(new NamespacedKey(HologramGUI.getInstance(), "hologramName"), PersistentDataType.STRING);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || !event.getView().getTitle().startsWith("Your Holograms")) {
            return;
        }

        event.setCancelled(true); // Prevent item pickup

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        String hologramName = getHologramName(clickedItem);

        if (hologramName == null) {
            return;
        }

        if (event.isLeftClick()) {
            // Handle left-click (open line selection GUI)
            new HologramEditLineGUI(hologramManager, anvilManager).open(player, hologramName);
        } else if (event.isRightClick()) {
            // Handle right-click (delete)
            hologramManager.deleteHologram(player, hologramName);
            inventory.remove(clickedItem);
        }
    }
}