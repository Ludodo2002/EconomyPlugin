package fr.redcraft.economy.commands.coin;

import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.CoinCommand;
import fr.redcraft.economy.commands.CommandManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AddCommand extends CoinCommand {

    public AddCommand(Main plugin, CommandManager commandManager){
        super(plugin,commandManager);
    }
    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("add")
                        .permission("coin.command.add")
                        .argument(PlayerArgument.optional("player"))
                        .argument(DoubleArgument.optional("Integer"))
                        .handler(this::execute)
        );
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Optional<Player> optionalPlayer = context.getOptional("player");
        double coin = context.get("Integer");

        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            plugin.getAPI().getAccount(player.getName(),player.getUniqueId().toString()).deposit(coin);
            sender.sendMessage(Main.PREFIX + " Vous venez d'ajouter " + coin + " ₡ à " + player.getName());
        }
    }
}
