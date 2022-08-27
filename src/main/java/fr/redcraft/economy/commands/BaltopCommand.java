package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.database.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BaltopCommand implements CommandExecutor {
    public Main plugin;

    public BaltopCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("baltop").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Account> topAccounts = plugin.getAPI().getTopAccounts();
        if (topAccounts.size() < 0) {
            sender.sendMessage(Main.PREFIX + "§cErreur il n'existe aucun compte.");
            return false;
        }
        sender.sendMessage("§8§m                             §8<§bTop-10§8>§8§m                             ");
        for (int i = 0; i < topAccounts.size(); i++) {
            Account account = topAccounts.get(i);
            sender.sendMessage((i + 1) + ". " + account.getName() + " - " + account.getMoney());
        }
        return false;
    }
}
