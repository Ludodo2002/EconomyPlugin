package fr.redcraft.economy.listener;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.database.Account;
import fr.redcraft.economy.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    public Main plugin;

    public PlayerListener(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getAPI().createAccount(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Database database = plugin.getDatabase();
        Player player = event.getPlayer();
        Account account = database.getCachedAccount(player.getName(), player.getUniqueId().toString());
        if (account != null) {
            account.save(account.getMoney());
            database.removeCachedAccount(account);
        }
    }
}
