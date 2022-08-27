package fr.redcraft.economy.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class ServerBankComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("economy.staff")) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                commands.add("add");
                commands.add("remove");
                return commands;
            }
            return null;
        }
        return null;
    }
}