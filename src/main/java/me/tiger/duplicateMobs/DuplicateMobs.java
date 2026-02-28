package me.tiger.duplicateMobs;

import org.bukkit.plugin.java.JavaPlugin;

public class DuplicateMobs extends JavaPlugin {

    public static final int DUPLICATE_CAP = 50; // your cap

    @Override
    public void onEnable() {
        DuplicateManager manager = new DuplicateManager(this);

        getServer().getPluginManager().registerEvents(new DuplicateListener(manager), this);

        var cmd = getCommand("duplicatemobs");
        if (cmd != null) {
            cmd.setExecutor(new DuplicateMobsCommand(manager));
        }

        getLogger().info("DuplicateMobs enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("DuplicateMobs disabled.");
    }
}