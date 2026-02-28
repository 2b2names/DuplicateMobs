package me.tiger.duplicateMobs;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Cooldown {

    private final long cooldownMillis;
    private final ConcurrentHashMap<UUID, Long> lastUse = new ConcurrentHashMap<>();

    public Cooldown(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    public boolean ready(UUID id) {
        long now = System.currentTimeMillis();

        Long last = lastUse.get(id);
        if (last == null || (now - last) >= cooldownMillis) {
            lastUse.put(id, now);
            return true;
        }

        return false;
    }

    public void remove(UUID id) {
        lastUse.remove(id);
    }
}