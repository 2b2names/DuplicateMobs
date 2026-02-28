package me.tiger.duplicateMobs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DuplicateManager {

    private final JavaPlugin plugin;

    // Enabled players
    private final Set<UUID> enabledPlayers = ConcurrentHashMap.newKeySet();

    // Track duplicates alive for the cap
    private final Set<UUID> aliveDuplicates = ConcurrentHashMap.newKeySet();

    public DuplicateManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public boolean isEnabled(Player player) {
        return enabledPlayers.contains(player.getUniqueId());
    }

    public void enable(Player player) {
        enabledPlayers.add(player.getUniqueId());
    }

    public void disable(Player player) {
        enabledPlayers.remove(player.getUniqueId());
    }

    public boolean canSpawnMore() {
        return aliveDuplicates.size() < DuplicateMobs.DUPLICATE_CAP;
    }

    public void markDuplicateAlive(Entity e) {
        aliveDuplicates.add(e.getUniqueId());
    }

    public void markDuplicateDead(UUID id) {
        aliveDuplicates.remove(id);
    }
}