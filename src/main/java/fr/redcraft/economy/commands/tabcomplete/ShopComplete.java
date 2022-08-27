package fr.redcraft.economy.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class ShopComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("economy.staff")) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                commands.add("help");
                commands.add("add");
                commands.add("update");
                return commands;
            } else if (args.length == 2) {
                List<String> arguments = new ArrayList<>();
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("update")) {
                    arguments.add("<Prix-Vente>");
                }
                return arguments;
            } else if (args.length == 3) {
                List<String> arguments = new ArrayList<>();
                if (args[1].equalsIgnoreCase("<Prix-Vente>")) {
                    arguments.add("<Prix-Achat>");
                }
                return arguments;
            }
            return null;
        }
        return null;
    }
}