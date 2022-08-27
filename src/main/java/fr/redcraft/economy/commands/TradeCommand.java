package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.tabcomplete.TradeComplete;
import fr.redcraft.economy.helper.Helper;
import fr.redcraft.economy.trade.TradeData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {
    public Main plugin;

    public TradeCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("trade").setExecutor(this);
        plugin.getCommand("trade").setTabCompleter(new TradeComplete());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("trade"))

            if (args.length == 0) {
                sender.sendMessage("Help command trade");
            }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("request")) {
                Player target = Bukkit.getPlayer(args[1]);
                Player player = (Player) sender;
                if(args.length < 2 || args.length > 2) {
                    sender.sendMessage(Main.PREFIX + "/trade request {joueur}");
                    return false;
                }
                if(target != null){
                    int defaultTaxeCaller = (int) (Helper.getPercent(Helper.getDistance(player, target)));
                    int defaultTaxeReciever = (int) (Helper.getPercent(Helper.getDistance(target, player)));
                    player.sendMessage("§aVous venez de faire une demande de trade à §f§l" + target.getName());
                    player.sendMessage("§7/§c!§7\\ §cUne taxe de §8" + defaultTaxeCaller + "₡ §cVous sera appliquer");
                    target.sendMessage("§aVous venez de recevoir une demande de trade de §f§l" + player.getName());
                    target.sendMessage("§7/§c!§7\\ §cUne taxe de §8" + defaultTaxeReciever + "₡ §cVous sera appliquer");
                    target.sendMessage(Main.PREFIX + "Pour continuer faites /trade accept [pseudo]");
                    new TradeData(player,target);
                }else {
                    sender.sendMessage(Main.PREFIX + "§cErreur le joueur n'existe pas");
                    return false;
                }
            }
            else if(args[0].equalsIgnoreCase("accept")){
                Player target = Bukkit.getPlayer(args[1]);
                Player player = (Player) sender;
                if(args.length < 2 || args.length > 2) {
                    sender.sendMessage(Main.PREFIX + "/trade accept {joueur}");
                    return false;
                }
                if(target != null){
                    TradeData trade = TradeData.acceptTradeRequest(player);
                    if(trade != null){
                        trade.initiateTrade();
                    }else {
                        sender.sendMessage(Main.PREFIX + "§cErreur il n'existe aucune demande de trade");
                    }
                }else {
                    sender.sendMessage(Main.PREFIX + "§cErreur le joueur n'existe pas");
                    return false;
                }
            }
        }
        return false;
    }
}
