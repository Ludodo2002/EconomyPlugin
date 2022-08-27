package fr.redcraft.economy.database;


import fr.redcraft.economy.Main;
import fr.redcraft.economy.helper.API;

public class Account {
    private Main plugin;
    private String name;
    private String uuid;
    private API api;
    private Database database;
    private Double money;

    public Account(Main plugin, String name, String uuid, Database database) {
        this.plugin = plugin;
        this.name = name;
        this.uuid = uuid;
        this.api = plugin.getAPI();
        this.database = database;
        this.money = null;
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean buy(double cost) {
        if (getMoney() >= cost) {
            withdraw(cost);
            return true;
        }
        return false;
    }

    public Double getMoney() {
        if (money != null) {
            return money;
        }
        Double money = database.loadAccountMoney(name, uuid);
        if (database.cacheAccounts()) {
            this.money = money;
        }
        return money;
    }

    public void setMoney(double money) {
        Double currentMoney = getMoney();
        if (currentMoney != null && currentMoney == money) {
            return;
        }
        if (money < 0 && !api.isCurrencyNegative()) {
            money = 0;
        }
        currentMoney = api.getMoneyRounded(money);
        if (api.getMaxHoldings() > 0 && currentMoney > api.getMaxHoldings()) {
            currentMoney = api.getMoneyRounded(api.getMaxHoldings());
        }
        if (!database.cacheAccounts() || plugin.getServer().getPlayerExact(getName()) == null) {
            save(currentMoney);
        } else {
            this.money = currentMoney;
        }
    }

    public void withdraw(double amount) {
        setMoney(getMoney() - amount);
    }

    public void deposit(double amount) {
        setMoney(getMoney() + amount);
    }

    public boolean canReceive(double amount) {
        return api.getMaxHoldings() == -1 || amount + getMoney() < api.getMaxHoldings();

    }
    public boolean has(double amount) {
        return getMoney() >= amount;
    }

    public void save(double money) {
        database.saveAccount(name, uuid, money);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }
        Account account = (Account) object;
        return account.getName().equals(getName());
    }
}