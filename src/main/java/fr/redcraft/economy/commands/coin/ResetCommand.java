package fr.redcraft.economy.commands.coin;

import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.CoinCommand;
import fr.redcraft.economy.commands.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ResetCommand extends CoinCommand {

    public ResetCommand(Main plugin, CommandManager commandManager){
        super(plugin,commandManager);
    }
    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("reset")
                        .permission("coin.command.reset")
                        .argument(PlayerArgument.optional("player"))
                        .handler(this::execute)
        );
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Optional<Player> optionalPlayer = context.getOptional("player");

        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            Main.getApis().getAccount(player.getName(),player.getUniqueId().toString()).setMoney(0.0);
            sender.sendMessage(Main.PREFIX + "Vous venez de reset l'argent de " + player.getName());
        }
    }
}

