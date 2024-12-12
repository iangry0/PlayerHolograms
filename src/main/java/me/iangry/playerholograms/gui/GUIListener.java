package me.iangry.playerholograms.gui;

import me.iangry.playerholograms.hologram.HologramManager;
import me.iangry.playerholograms.utils.AnvilManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    private final HologramManager hologramManager;
    private final AnvilManager anvilManager;

    public GUIListener(HologramManager hologramManager, AnvilManager anvilManager) {
        this.hologramManager = hologramManager;
        this.anvilManager = anvilManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;

        ItemStack clickedItem = event.getCurrentItem();

        if (event.getView().getTitle().equals(ChatColor.GREEN + "Hologram Manager")) {
            event.setCancelled(true);
           // player.sendMessage(ChatColor.GREEN + "Clicked on: " + clickedItem.getType());

            if (clickedItem.getType() == Material.NAME_TAG) {
                player.sendMessage(ChatColor.GREEN + "Opening anvil input for hologram creation...");
                player.closeInventory();
                anvilManager.openAnvilInput(player, newLines -> {
                    hologramManager.createHologram(player, newLines);
                });
            } else if (clickedItem.getType() == Material.PAPER) {
                player.sendMessage(ChatColor.GREEN + "Listing holograms...");
                new HologramListGUI(hologramManager, anvilManager).open(player);
            } else if (clickedItem.getType() == Material.BARRIER) {
                player.sendMessage(ChatColor.RED + "Closed the Hologram Manager.");
                player.closeInventory();
            }
        }
    }
}