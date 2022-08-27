package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.tabcomplete.ServerBankComplete;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ServerBankCommand implements CommandExecutor {
    public Main plugin;

    public ServerBankCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("serverbank").setExecutor(this);
        plugin.getCommand("serverbank").setTabCompleter(new ServerBankComplete());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("serverbank"))
            if(sender.isOp() || sender.hasPermission("economy.bank")){
                if(args.length == 0 || args.length == 1){
                    sender.sendMessage(Main.PREFIX + "§6La bank contient §7: §f" + Main.getInstance().getConfig().getInt("bank"));
                    return true;
                }
                if(args.length == 2){
                    int nbr = 0;
                    try {
                        nbr = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e){
                        sender.sendMessage(Main.PREFIX + "§cErreur le nombre est invalide");
                        return false;
                    }
                    if(args[0].equalsIgnoreCase("add")){
                        int current = Main.getInstance().getConfig().getInt("bank");
                        int finalcoin = current + nbr;
                        sender.sendMessage(Main.PREFIX + "§6Vous venez d'ajouter §f" + finalcoin + "§6 à la bank");
                        Main.getInstance().getConfig().set("bank",finalcoin);
                        Main.getInstance().saveConfig();
                    }
                    else if(args[0].equalsIgnoreCase("remove")){
                        int current = Main.getInstance().getConfig().getInt("bank");
                        int finalcoin = current - nbr;
                        sender.sendMessage(Main.PREFIX + "§6Vous venez d'enlever §f" + finalcoin + "§6 à la bank");
                        Main.getInstance().getConfig().set("bank",finalcoin);
                        Main.getInstance().saveConfig();
                    }
                }
            }else {
                sender.sendMessage("§cAn internal error occurred, please contact an admin or moderator");
                return false;
            }
        return false;
    }
}
