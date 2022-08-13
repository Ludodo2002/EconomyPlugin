package fr.redcraft.economy.manager;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import java.util.List;
import java.util.UUID;

public class VaultAPI extends AbstractEconomy {


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "OdysseyCoins";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String player) {
        return VaultManager.coins.containsKey(UUID.fromString(player));
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public double getBalance(String player) {
        return VaultManager.coins.get(Bukkit.getOfflinePlayer(player).getUniqueId());
    }
    @Override
    public double getBalance(String playerName, String world) {
        return 0;
    }

    @Override
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, double amount) {
        double money = getBalance(player);
        money -= amount;
        VaultManager.coins.replace(UUID.fromString(player), (int) money);
        return new EconomyResponse(amount, money, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String player, double amount) {
        if (!hasAccount(player)){
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "The Player has no Account!");
        }
        double money = getBalance(player);
        money += amount;
        VaultManager.coins.replace(UUID.fromString(player), (int) money);
        return new EconomyResponse(amount, money, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String player) {
        if(!VaultManager.coins.containsKey(player)){
            VaultManager.coins.put(UUID.fromString(player),500);
        }else {
            return false;
        }
        return true;
    }

    @Override
    public boolean createPlayerAccount(String player, String world) {
        return createPlayerAccount(player);
    }
}
