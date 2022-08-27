package fr.redcraft.economy.commands.tabcomplete;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class TradeComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("economy.staff")) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                commands.add("request");
                commands.add("accept");
                return commands;
            } else if (args.length == 2) {
                List<String> players = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                return players;
            }
            return null;
        }
        return null;
    }
}