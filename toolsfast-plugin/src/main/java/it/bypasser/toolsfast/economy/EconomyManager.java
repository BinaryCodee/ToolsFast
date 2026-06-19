package it.bypasser.toolsfast.economy;

import it.bypasser.toolsfast.ToolsFast;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyManager {

    private final ToolsFast plugin;
    private Economy economy;

    public EconomyManager(ToolsFast plugin) {
        this.plugin = plugin;
        if (plugin.hooks() != null && plugin.hooks().vault()) {
            try {
                RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp != null) economy = rsp.getProvider();
            } catch (Throwable t) {
                plugin.getLogger().warning("Vault economy hook failed: " + t.getMessage());
            }
        }
    }

    public boolean available() { return economy != null; }

    public double balance(OfflinePlayer p) {
        if (economy == null) return 0.0;
        try { return economy.getBalance(p); } catch (Throwable t) { return 0.0; }
    }

    public void deposit(org.bukkit.entity.Player p, double amount) {
        if (economy == null || amount <= 0) return;
        try { economy.depositPlayer(p, amount); } catch (Throwable ignored) {}
    }

    public void withdraw(org.bukkit.entity.Player p, double amount) {
        if (economy == null || amount <= 0) return;
        try { economy.withdrawPlayer(p, amount); } catch (Throwable ignored) {}
    }
}
