package fr.redcraft.economy;

import fr.redcraft.economy.commands.*;
import fr.redcraft.economy.database.Database;
import fr.redcraft.economy.database.data.MySQLDB;
import fr.redcraft.economy.gui.GuiHandler;
import fr.redcraft.economy.helper.API;
import fr.redcraft.economy.listener.PlayerListener;
import fr.redcraft.economy.shop.ShopHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Plugin instance;
    private CommandManager commandManager;
    public static String PREFIX = "§8[§6₡§8]§r ";
    private Database database;
    private API api;
    public static API apis;
    private static GuiHandler guiHandler;
    private static ShopHandler shop;

    @Override
    public void onEnable() {
        instance = this;
        this.database = new MySQLDB(this);
        this.api = new API(this);
        apis = new API(this);
        try {
            commandManager = new CommandManager(this);
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Error whilst initializing command manager", e);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this),this);
        for(Player player : Bukkit.getOnlinePlayers()){
            getAPI().createAccount(player.getName(), player.getUniqueId().toString());
        }
        new MoneyCommand(this);
        new BaltopCommand(this);
        new PayCommand(this);
        new TradeCommand(this);
        new ShopCommand(this);
        new ServerBankCommand(this);
        guiHandler = new GuiHandler();
        shop = ShopHandler.loadShopHandler();
    }

    @Override
    public void onDisable() {

    }

    public static Plugin getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public API getAPI() {
        return api;
    }

    public static API getApis() {
        return apis;
    }

    public static GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public static ShopHandler getShop() {
        return shop;
    }
}
