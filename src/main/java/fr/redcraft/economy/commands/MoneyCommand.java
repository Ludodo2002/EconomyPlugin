package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    public Main plugin;

    public MoneyCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("money").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        player.sendMessage(Main.PREFIX + "Votre argent : " + plugin.getAPI().getAccount(player.getName(),player.getUniqueId().toString()).getMoney() + " ₡");
        return false;
    }
}
