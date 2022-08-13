package fr.redcraft.economy;

import fr.redcraft.economy.commands.CommandManager;
import fr.redcraft.economy.manager.VaultManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Plugin instance;
    private static VaultManager vaultManager;
    private CommandManager commandManager;
    public static String PREFIX = "§8[§6₡§8]§r ";

    @Override
    public void onEnable() {
        instance = this;
        vaultManager = new VaultManager(this);

        try {
            commandManager = new CommandManager(this);
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Error whilst initializing command manager", e);
            return;
        }
    }

    @Override
    public void onDisable() {
        VaultManager.saveAll();
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static VaultManager getVaultManager() {
        return vaultManager;
    }
}
