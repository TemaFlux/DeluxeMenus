package com.extendedclip.deluxemenus.requirement.custom;

import com.extendedclip.deluxemenus.menu.MenuHolder;
import com.extendedclip.deluxemenus.requirement.Requirement;
import me.temaflux.core.CommonPlugin;
import me.temaflux.core.commons.api.EconomyLog;
import org.bukkit.entity.Player;

public class CoreBuyRequirement
extends Requirement {
    private final String from;
    private final EconomyLog.Economy economy;
    private final int amount;
    private final String reason;

    public CoreBuyRequirement(String from, String economy, int amount, String reason) {
        this(from, economy == null || economy.isBlank() ? null : EconomyLog.Economy.valueOf(economy.toUpperCase().trim()), amount, reason);
    }

    public CoreBuyRequirement(String from, EconomyLog.Economy economy, int amount, String reason) {
        this.from = from == null || from.isBlank() ? "DeluxeMenu" : from;
        this.economy = economy == null ? EconomyLog.Economy.MONEY : economy;
        this.amount = amount;
        this.reason = reason == null || reason.isBlank() ? "Unknown" : reason;
    }

    @Override
    public boolean evaluate(MenuHolder holder) {
        Player human = holder.getViewer();

        try {
            CommonPlugin plugin = CommonPlugin.getInstance();
            if (plugin == null) return false;

            me.temaflux.core.commons.api.entity.Player player = human == null ? null : plugin.getPlayer(human.getUniqueId());
            if (player == null || amount > (economy == EconomyLog.Economy.SMITE ? player.getSmite() : player.getMoney())) return false;

            return plugin.money(
                from,
                player.getUniqueId(),
                economy,
                -amount,
                reason
            ).join();
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
