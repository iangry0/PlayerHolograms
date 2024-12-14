package me.iangry.playerholograms.utils;

import me.iangry.playerholograms.HologramGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.function.Consumer;

public class AnvilManager {

    public void openAnvilInput(Player player, Consumer<String[]> callback) {
        new AnvilGUI.Builder()
                .onClose(p -> {
                    // Notify the player if they closed the inventory without confirming input
                    p.getPlayer().closeInventory();
                    //p.getPlayer().sendMessage(ChatColor.RED + "Hologram creation cancelled");
                })
                .onClick((slot, stateSnapshot) -> {
                    if (slot == AnvilGUI.Slot.OUTPUT) {
                        String text = stateSnapshot.getText();
                        player.sendMessage(ChatColor.GREEN + "Input received: " + ChatColor.translateAlternateColorCodes('&', text));

                        // Split the text into multiple lines if necessary
                        String[] lines = text.split("\n");

                        // Execute the callback with the input lines
                        callback.accept(lines);

                        // Close the AnvilGUI
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                    }

                    return Collections.emptyList();
                })
                .text("Enter hologram text") // Sets the default text
                .itemLeft(new ItemStack(Material.PAPER)) // Sets the item in the left slot
                .title("Hologram Input") // Sets the title
                .plugin(HologramGUI.getInstance()) // Sets the plugin instance
                .open(player); // Opens the GUI for the player
    }
}
