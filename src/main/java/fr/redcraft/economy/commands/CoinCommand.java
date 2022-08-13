package fr.redcraft.economy.commands;

import fr.redcraft.economy.Main;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class CoinCommand {
    protected final Main plugin;
    protected final CommandManager commandManager;

    protected CoinCommand(final @NonNull Main plugin, final @NonNull CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public abstract void register();
}