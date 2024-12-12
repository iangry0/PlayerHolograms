package me.iangry.playerholograms.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.utils.WorldGuardChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramManager {

    private final Map<UUID, List<String>> playerHolograms = new HashMap<>();
    private final FileConfiguration config;

    public HologramManager(FileConfiguration config) {
        this.config = config;
        loadHolograms();
    }

    public void createHologram(Player player, String[] lines) {
        int maxHolograms = getMaxHolograms(player);
        List<String> holograms = playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList<>());

        if (holograms.size() >= maxHolograms && maxHolograms != -1) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of holograms you can create.");
            return;
        }

        // Check banned words
        List<String> bannedWords = config.getStringList("bannedWords");
        for (String line : lines) {
            for (String bannedWord : bannedWords) {
                if (line.contains(bannedWord)) {
                    player.sendMessage(ChatColor.RED + "This hologram contains a banned word.");
                    return;
                }
            }
        }

        Location location = player.getLocation().add(0, 2, 0);

        // Check if the location is restricted
        if (isRestrictedLocation(location)) {
            player.sendMessage(ChatColor.RED + "You cannot create a hologram here.");
            return;
        }

        String hologramName = "holo_" + player.getUniqueId() + "_" + UUID.randomUUID();
        DHAPI.createHologram(hologramName, location, Arrays.asList(lines));

        holograms.add(hologramName);
        playerHolograms.put(player.getUniqueId(), holograms);
        saveHologramDetails(hologramName, location, lines);
        saveHolograms();
        player.sendMessage(ChatColor.GREEN + "Hologram created successfully!");
    }

    public void deleteHologram(Player player, String hologramName) {
        List<String> holograms = playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList<>());

        if (!holograms.contains(hologramName)) {
            player.sendMessage(ChatColor.RED + "You do not own this hologram or it does not exist.");
            return;
        }

        DHAPI.removeHologram(hologramName);
        holograms.remove(hologramName);
        config.set("hologramDetails." + hologramName, null);
        saveHolograms();
        player.sendMessage(ChatColor.GREEN + "Hologram deleted successfully!");
    }

    public void updateHologramText(Player player, String hologramName, String[] lines) {
        List<String> holograms = playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList<>());

        if (!holograms.contains(hologramName)) {
            player.sendMessage(ChatColor.RED + "You do not own this hologram or it does not exist.");
            return;
        }

        // Check banned words
        List<String> bannedWords = config.getStringList("bannedWords");
        for (String line : lines) {
            for (String bannedWord : bannedWords) {
                if (line.contains(bannedWord)) {
                    player.sendMessage(ChatColor.RED + "This hologram contains a banned word.");
                    return;
                }
            }
        }

        Location location = deserializeLocation(config.getString("hologramDetails." + hologramName + ".location"));
        DHAPI.removeHologram(hologramName);
        DHAPI.createHologram(hologramName, location, Arrays.asList(lines));

        saveHologramDetails(hologramName, location, lines);
        saveHolograms();
        player.sendMessage(ChatColor.GREEN + "Hologram updated successfully!");
    }

    public List<String> listHolograms(Player player) {
        List<String> holograms = playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList<>());

        if (holograms.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You have no holograms.");
            return holograms;
        }

        player.sendMessage(ChatColor.GREEN + "Your holograms:");
        for (String hologram : holograms) {
            List<String> lines = getHologramLines(hologram); // Fetch hologram text lines
            String joinedLines = String.join("\n", lines); // Join lines into a single string

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b - " + joinedLines + "&b"));
        }
        return holograms;
    }

    private boolean isRestrictedLocation(Location location) {
        List<String> restrictedRegions = config.getStringList("restrictedRegions");
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null
                && WorldGuardChecker.isRestricted(location, restrictedRegions);
    }

    public void saveHolograms() {
        for (UUID uuid : playerHolograms.keySet()) {
            config.set("holograms." + uuid.toString(), playerHolograms.get(uuid));
        }
        HologramGUI.getInstance().saveConfig();
    }

    private void loadHolograms() {
        if (config.contains("holograms")) {
            for (String uuidString : config.getConfigurationSection("holograms").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                List<String> holograms = config.getStringList("holograms." + uuidString);
                playerHolograms.put(uuid, holograms);

                for (String hologramName : holograms) {
                    String locationString = config.getString("hologramDetails." + hologramName + ".location");
                    Location location = deserializeLocation(locationString);
                    List<String> lines = config.getStringList("hologramDetails." + hologramName + ".lines");
                    DHAPI.createHologram(hologramName, location, lines);
                }
            }
        }
    }

    private void saveHologramDetails(String hologramName, Location location, String[] lines) {
        config.set("hologramDetails." + hologramName + ".location", serializeLocation(location));
        config.set("hologramDetails." + hologramName + ".lines", Arrays.asList(lines));
    }

    private Location deserializeLocation(String locationString) {
        String[] parts = locationString.split(",");
        World world = Bukkit.getWorld(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public List<String> getHologramLines(String hologramName) {
        return config.getStringList("hologramDetails." + hologramName + ".lines");
    }

    public int getMaxHolograms(Player player) {
        for (int i = 100; i > 0; i--) { // Check from 100 down to 1
            if (player.hasPermission("playerholograms.use." + i)) {
                return i;
            }
        }
        if (player.hasPermission("playerholograms.use.*")) {
            return -1; // Infinite holograms
        }
        return 0; // Default to 0 if no permissions are found
    }
}