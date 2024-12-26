package me.temaflux.core;

import me.temaflux.core.commons.api.EconomyLog;
import me.temaflux.core.commons.api.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonPlugin {
    public static CommonPlugin getInstance() {
        throw new RuntimeException();
    }

    public Player getPlayer(UUID uniqueId) {
        throw new RuntimeException();
    }

    public CompletableFuture<Boolean> money(String from, UUID uniqueId, EconomyLog.Economy economy, double amount, String reason) {
        throw new RuntimeException();
    }
}
