package me.iangry.playerholograms.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class WorldGuardChecker {

    public static boolean isRestricted(org.bukkit.Location location, List<String> restrictedRegions) {
        if (location == null || restrictedRegions == null || restrictedRegions.isEmpty()) {
            return false;
        }

        // Get WorldGuard's RegionContainer
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            return false;
        }

        RegionManager regionManager = container.get(BukkitAdapter.adapt(bukkitWorld));
        if (regionManager == null) {
            return false;
        }

        // Get the applicable regions for the location
        ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for (ProtectedRegion region : applicableRegions) {
            if (restrictedRegions.contains(region.getId())) {
                return true;
            }
        }

        return false;
    }
}
