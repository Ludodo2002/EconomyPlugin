package fr.redcraft.economy.helper;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.database.Account;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class API {
    private final Main plugin;

    public API(Main plugin) {
        this.plugin = plugin;
    }

    public List<Account> getTopAccounts() {
        return plugin.getDatabase().getTopAccounts(10);
    }
    public List<Account> getTopAccounts(int size) {
        return plugin.getDatabase().getTopAccounts(size);
    }

    public List<Account> getAccounts() {
        return plugin.getDatabase().getAccounts();
    }

    public double getDefaultHoldings() {
        return 500;
    }

    public double getMaxHoldings() {
        return plugin.getConfig().getDouble("maxholdings");
    }

    public boolean isCurrencyNegative() {
        return plugin.getConfig().getBoolean("currency.negative");
    }

    public boolean getCacheAccounts() {
        return plugin.getConfig().getBoolean("cacheaccounts");
    }

    public Account createAccount(String name, String uuid) {
        return plugin.getDatabase().createAccount(name, uuid);
    }

    public void removeAccount(String name, String uuid) {
        plugin.getDatabase().removeAccount(name, uuid);
    }

    public Account getAccount(String name, String uuid) {
        return plugin.getDatabase().getAccount(name, uuid);
    }

    public boolean accountExists(String name, String uuid) {
        return plugin.getDatabase().accountExists(name, uuid);
    }

    private String formatValue(double value) {
        boolean isWholeNumber = value == Math.round(value);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        String pattern = isWholeNumber ? "###,###.###" : "###,##0.00";
        DecimalFormat df = new DecimalFormat(pattern, formatSymbols);
        return df.format(value);
    }


    public double getMoneyRounded(double amount) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        String formattedAmount = twoDForm.format(amount);
        formattedAmount = formattedAmount.replace(",", ".");
        return Double.valueOf(formattedAmount);
    }

    public void clean() {
        plugin.getDatabase().clean();
    }
}