package fr.redcraft.economy.commands;

import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import fr.redcraft.economy.Main;
import fr.redcraft.economy.commands.coin.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.UnaryOperator;

public final class CommandManager extends PaperCommandManager<CommandSender> {

    private final BukkitAudiences bukkitAudiences;

    public CommandManager(final @NonNull Main plugin) throws Exception {
        super(plugin, CommandExecutionCoordinator.simpleCoordinator(), UnaryOperator.identity(), UnaryOperator.identity());

        this.bukkitAudiences = BukkitAudiences.create(plugin);

            this.registerBrigadier();

            final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
        }

        registerExceptions();

            this.registerAsynchronousCompletions();

        ImmutableList.of(
                new ResetCommand(plugin,this),
                new RemoveCommand(plugin,this),
                new AddCommand(plugin,this),
                new InfoCommand(plugin,this),
                new HelpCommand(plugin, this)
        ).forEach(CoinCommand::register);

    }

    public void registerExceptions() {
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .apply(this, bukkitAudiences::sender);

    }

    public void registerSubcommand(UnaryOperator<Command.Builder<CommandSender>> builderModifier) {
        this.command(builderModifier.apply(this.rootBuilder()));
    }

    private Command.@NonNull Builder<CommandSender> rootBuilder() {
        return this.commandBuilder("coin", "coin")
                .meta(CommandMeta.DESCRIPTION, "Coin command. '/Coin help'");
    }

    public BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

}