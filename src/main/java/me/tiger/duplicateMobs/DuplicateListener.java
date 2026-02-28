package me.tiger.duplicateMobs;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.ThreadLocalRandom;

public class DuplicateListener implements Listener {

    private static final String META_DUPLICATE = "duplicatemobs_duplicate";
    private final DuplicateManager manager;

    public DuplicateListener(DuplicateManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;
        if (!manager.isEnabled(player)) return;

        LivingEntity source = resolveMobSource(event.getDamager());
        if (source == null) return;

        // Exclusions
        if (source instanceof EnderDragon) return; // your rule
        if (source instanceof Wither) return;      // also excluded
        if (!(source instanceof Monster)) return;  // only hostile mobs

        // Cap: max 50 duplicates alive (spawned by plugin)
        if (!manager.canSpawnMore()) return;

        World world = source.getWorld();

        // A + C mix: sometimes adjacent, sometimes small circle
        double r = ThreadLocalRandom.current().nextBoolean()
                ? 0.7 + ThreadLocalRandom.current().nextDouble(0.4)   // A: very close
                : 1.2 + ThreadLocalRandom.current().nextDouble(1.2);   // C: around

        double angle = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);

        var spawnLoc = source.getLocation().clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);

        Entity spawned = world.spawnEntity(spawnLoc, source.getType());
        if (!(spawned instanceof LivingEntity dup)) return;

        // Track duplicate for cap removal
        dup.setMetadata(META_DUPLICATE, new FixedMetadataValue(manager.plugin(), true));
        manager.markDuplicateAlive(dup);

        // Copy ONLY weapon (main hand)
        copyWeaponOnly(source, dup);

        // No-voice feedback
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.3f);
        player.sendTitle(ChatColor.RED + "MULTIPLIED!", ChatColor.WHITE + prettyTypeName(source), 0, 12, 6);
    }

    @EventHandler
    public void onDuplicateDeath(EntityDeathEvent event) {
        LivingEntity e = event.getEntity();
        if (!e.hasMetadata(META_DUPLICATE)) return;
        manager.markDuplicateDead(e.getUniqueId());
    }

    private LivingEntity resolveMobSource(Entity damager) {
        if (damager instanceof LivingEntity le) return le;

        if (damager instanceof Projectile projectile) {
            Object shooter = projectile.getShooter();
            if (shooter instanceof LivingEntity le) return le;
        }

        return null;
    }

    private void copyWeaponOnly(LivingEntity from, LivingEntity to) {
        EntityEquipment fromEq = from.getEquipment();
        EntityEquipment toEq = to.getEquipment();
        if (fromEq == null || toEq == null) return;

        ItemStack weapon = fromEq.getItemInMainHand();
        if (weapon != null && !weapon.getType().isAir()) {
            toEq.setItemInMainHand(weapon.clone());
        }
    }

    private String prettyTypeName(LivingEntity entity) {
        String s = entity.getType().name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}