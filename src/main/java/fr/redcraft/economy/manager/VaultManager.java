package fr.redcraft.economy.manager;

import fr.redcraft.economy.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VaultManager {

    private final Economy economy;
    public static HashMap<UUID,Integer> coins = new HashMap<>();
    public static File file = new File(Main.getInstance().getDataFolder(),"coins.yml");
    public static YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    public VaultManager(Main plugin) {
        plugin.getServer().getServicesManager().register(Economy.class, new VaultAPI(), plugin, ServicePriority.Highest);
        economy = new VaultAPI();
        loadAll();
    }

    public static void saveAll(){
        for (Map.Entry<UUID, Integer> coins : coins.entrySet()){
            config.set(coins.getKey().toString(),coins.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadAll(){
        for(String string : config.getKeys(false)){
            new VaultAPI().createPlayerAccount(string);
            new VaultAPI().depositPlayer(string,config.getInt(string));
        }
    }

    public Economy getEconomy() {
        return economy;
    }
}
