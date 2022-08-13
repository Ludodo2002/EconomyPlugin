package fr.redcraft.economy.listener;

import fr.redcraft.economy.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!Main.getVaultManager().getEconomy().hasAccount(event.getPlayer()))
            Main.getVaultManager().getEconomy().createPlayerAccount(event.getPlayer());
    }
}
