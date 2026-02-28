package me.tiger.duplicateMobs;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class DuplicateMobsCommand implements CommandExecutor {

    private final DuplicateManager manager;

    public DuplicateMobsCommand(DuplicateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /duplicatemobs enable | disable");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "enable" -> {
                manager.enable(player);
                player.sendMessage(ChatColor.GREEN + "DuplicateMobs ENABLED");
                player.sendTitle(ChatColor.RED + "DuplicateMobs", ChatColor.WHITE + "ENABLED", 10, 35, 10);
            }
            case "disable" -> {
                manager.disable(player);
                player.sendMessage(ChatColor.RED + "DuplicateMobs DISABLED");
                player.sendTitle(ChatColor.GRAY + "DuplicateMobs", ChatColor.WHITE + "DISABLED", 10, 35, 10);
            }
            default -> player.sendMessage(ChatColor.YELLOW + "Usage: /duplicatemobs enable | disable");
        }

        return true;
    }
}