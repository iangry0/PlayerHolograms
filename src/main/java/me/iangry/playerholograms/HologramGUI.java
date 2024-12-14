package me.iangry.playerholograms;

import me.iangry.playerholograms.commands.HologramCommand;
import me.iangry.playerholograms.commands.PlayerHologramsPlugin;
import me.iangry.playerholograms.gui.GUIListener;
import me.iangry.playerholograms.gui.HologramEditLineGUI;
import me.iangry.playerholograms.gui.HologramListGUI;
import me.iangry.playerholograms.hologram.HologramManager;
import me.iangry.playerholograms.utils.AnvilManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramGUI extends JavaPlugin {

    private static HologramGUI instance;
    private HologramManager hologramManager;
    private AnvilManager anvilManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Create config file if not exists

        hologramManager = new HologramManager(getConfig());
        anvilManager = new AnvilManager();

        getCommand("hologramgui").setExecutor(new HologramCommand(this));
        getCommand("playerhologramsplugin").setExecutor(new PlayerHologramsPlugin(this));


        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIListener(hologramManager, anvilManager), this);
        pm.registerEvents(new HologramListGUI(hologramManager, anvilManager), this);
        pm.registerEvents(new HologramEditLineGUI(hologramManager, anvilManager), this);



        getLogger().info("PlayerHolograms Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        hologramManager.saveHolograms(); // Ensure holograms are saved on disable
        getLogger().info("PlayerHolograms Plugin Disabled!");
    }

    public static HologramGUI getInstance() {
        return instance;
    }
}
