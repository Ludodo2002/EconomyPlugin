package fr.redcraft.economy.commands.coin;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.CoinCommand;
import fr.redcraft.economy.commands.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class RemoveCommand extends CoinCommand {

    public RemoveCommand(Main plugin, CommandManager commandManager){
        super(plugin,commandManager);
    }
    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("remove")
                        .permission("coin.command.remove")
                        .argument(PlayerArgument.of("player"))
                        .argument(IntegerArgument.of("Integer"))
                        .handler(this::execute)
        );
    }

    private void execute(CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final Optional<Player> optionalPlayer = context.getOptional("player");
        int coin = context.get("Integer");

        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            Main.getVaultManager().getEconomy().withdrawPlayer(player,Double.valueOf(String.valueOf(coin)));
            sender.sendMessage(Main.PREFIX + " Vous venez d'enlever " + coin + " ₡ à " + player.getName());
        }
    }
}

