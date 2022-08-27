package fr.redcraft.economy.commands.coin;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.CoinCommand;
import fr.redcraft.economy.commands.CommandManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.stream.Collectors;

public class HelpCommand extends CoinCommand {

    private MinecraftHelp<CommandSender> minecraftHelp;

    public HelpCommand(@NonNull Main plugin, @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
        this.minecraftHelp = new MinecraftHelp<>(
                "/coin help",
                commandManager.getBukkitAudiences()::sender,
                commandManager
        );
        this.minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(
                TextColor.color(0x8DFF00),
                NamedTextColor.WHITE,
                TextColor.color(0xC028FF),
                NamedTextColor.GRAY,
                NamedTextColor.DARK_GRAY
        ));
        this.minecraftHelp.setMessage(MinecraftHelp.MESSAGE_HELP_TITLE, "Coin Help");
    }

    @Override
    public void register() {
        //used to suggest commands as arguments
        CommandHelpHandler<CommandSender> commandHelpHandler = this.commandManager.createCommandHelpHandler();
        CommandArgument<CommandSender, String> helpQueryArgument = StringArgument.<CommandSender>newBuilder("query")
                .greedy()
                .asOptional()
                .withSuggestionsProvider((context, input) -> {
                    CommandHelpHandler.IndexHelpTopic<CommandSender> indexHelpTopic = (CommandHelpHandler.IndexHelpTopic<CommandSender>) commandHelpHandler.queryHelp(context.getSender(), "");
                    return indexHelpTopic.getEntries()
                            .stream()
                            .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                            .collect(Collectors.toList());
                })
                .build();

        this.commandManager.registerSubcommand(builder ->
                builder.literal("help")
                        .argument(helpQueryArgument)
                        .permission("coin.command.help")
                        .handler(this::executeHelp));
    }

    private void executeHelp(@NonNull CommandContext<CommandSender> context) {
        this.minecraftHelp.queryCommands(
                context.<String>getOptional("query").orElse(""),
                context.getSender()
        );
    }
}
