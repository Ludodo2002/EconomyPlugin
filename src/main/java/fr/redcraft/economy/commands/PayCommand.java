package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.helper.Helper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    public Main plugin;

    public PayCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("pay").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        Integer number = Integer.valueOf(args[1]);

        if(!(args.length < 2) || !(args.length > 2)){
            if(target != null){
                if(number > 0){
                    if(Helper.isInt(args[1])){
                        double coin = plugin.getAPI().getAccount(player.getName(),player.getUniqueId().toString()).getMoney();
                        if(number < coin){
                            plugin.getAPI().getAccount(target.getName(),target.getUniqueId().toString()).deposit(number);
                            plugin.getDatabase().getAccount(player.getName(),player.getUniqueId().toString()).withdraw(number);
                            sender.sendMessage(Main.PREFIX + "Vous venez de donner " + number + "₡ à " + target.getName());
                            target.sendMessage(Main.PREFIX + "Vous venez de recevoir " + number + "₡ de " + player.getName());
                        }else {
                            sender.sendMessage(Main.PREFIX + "§cErreur vous n'avez pas assez d'argent");
                            return false;
                        }
                    }else {
                        sender.sendMessage(Main.PREFIX + "§cErreur l'argument 1 doit être un entier");
                        return false;
                    }
                }else {
                    sender.sendMessage(Main.PREFIX + "§cErreur le nombre doit être plus grand que 0");
                    return false;
                }
            }else {
                sender.sendMessage(Main.PREFIX + "§cErreur le joueur n'existe pas");
                return false;
            }
        }else {
            sender.sendMessage(Main.PREFIX + "§c/pay {player} {number}");
            return false;
        }
        return false;
    }
}

