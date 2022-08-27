package fr.redcraft.economy.commands.coin;

import cloud.commandframework.Description;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.CoinCommand;
import fr.redcraft.economy.commands.CommandManager;
import fr.redcraft.economy.database.Account;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class TopCommand extends CoinCommand {

    public TopCommand(Main plugin, CommandManager commandManager){
        super(plugin,commandManager);
    }
    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("top", Description.of("test"))
                        .permission("coin.command.top")
                        .handler(this::execute)
        );
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        List<Account> topAccounts = plugin.getAPI().getTopAccounts();
        if (topAccounts.size() < 0) {
            sender.sendMessage(Main.PREFIX + "§cErreur il n'existe aucun compte.");
            return;
        }
        sender.sendMessage("§fTop §6§l10 : ");
        for (int i = 0; i < topAccounts.size(); i++) {
            Account account = topAccounts.get(i);
            sender.sendMessage((i + 1) + ". " + account.getName() + " - " + account.getMoney());
        }
    }
}
