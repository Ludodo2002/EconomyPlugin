package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.tabcomplete.ShopComplete;
import fr.redcraft.economy.helper.Helper;
import fr.redcraft.economy.trade.TradeData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommand implements CommandExecutor {
    public Main plugin;

    public ShopCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("shop").setExecutor(this);
        plugin.getCommand("shop").setTabCompleter(new ShopComplete());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("shop"))
            if (args.length == 0) {
                Main.getShop().openShop((Player) sender);
                return false;
            }
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage("§6Shop commands : ");
                sender.sendMessage("§b");
                sender.sendMessage(">> /shop add <Prix-Achat> <Prix-Vente>");
                sender.sendMessage(">> /shop update <Prix-Achat> <Prix-Vente>");
                return true;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add")) {
                double buy = -1.0d;
                double sell = -1.0d;
                try {
                    if (args.length > 1) {
                        buy = Double.parseDouble(args[1]);
                    }
                    if (args.length > 2) {
                        sell = Double.parseDouble(args[2]);
                    }
                } catch (Exception e) {
                    sender.sendMessage(Main.PREFIX + "§cErreur le prix est invalide");
                    return false;
                }
                if (buy < 0.0f && sell < 0.0f) {
                    sender.sendMessage(Main.PREFIX + "§cAn internal error occurred, please contact an admin or moderator");
                    return false;
                }
                Player ply = (Player) sender;
                ItemStack inHand = ply.getInventory().getItemInMainHand();
                if (inHand == null || inHand.getType().equals(Material.AIR) || inHand.getAmount() < 1) {
                    sender.sendMessage(Main.PREFIX + "§cErreur vous devez avoir un item dans la main");
                    return false;
                }
                if (Main.getShop().addItem(inHand, buy, sell)) {
                    sender.sendMessage(Main.PREFIX + "Vous venez d'ajouter un nouvel items au shop.");
                }
            } else if (args[0].equalsIgnoreCase("update")) {
                double buy = -1.0d;
                double sell = -1.0d;
                try {
                    if (args.length > 1) {
                        buy = Double.parseDouble(args[1]);
                    }
                    if (args.length > 2) {
                        sell = Double.parseDouble(args[2]);
                    }
                } catch (Exception e) {
                    sender.sendMessage(Main.PREFIX + "§cErreur le prix est invalide");
                    return false;
                }
                if (buy < 0.0f && sell < 0.0f) {
                    sender.sendMessage(Main.PREFIX + "§cAn internal error occurred, please contact an admin or moderator");
                    return false;
                }
                Player ply = (Player) sender;
                ItemStack inHand = ply.getInventory().getItemInMainHand();
                if (inHand == null || inHand.getType().equals(Material.AIR) || inHand.getAmount() < 1) {
                    sender.sendMessage(Main.PREFIX + "§cErreur vous devez avoir un item dans la main");
                    return false;
                }
                if (Main.getShop().updateItem(inHand, buy, sell)) {
                    sender.sendMessage(Main.PREFIX + "Vous venez de changer le prix de l'item");

                }
            }
            return false;
        }
        return false;
    }
}

