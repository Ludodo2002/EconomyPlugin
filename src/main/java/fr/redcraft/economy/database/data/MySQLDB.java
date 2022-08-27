package fr.redcraft.economy.database.data;

import fr.redcraft.economy.Main;
import org.bukkit.configuration.ConfigurationSection;
import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLDB extends SQLDatabase {
    public String HOST = "sql3.minestrator.com";
    public int PORT = 3306;
    public String DATABASE = "minesr_wShBcMAa";
    public String USER = "minesr_wShBcMAa";
    public String PASSWORD = "JPDbtjjVsteN9SMA";
    public MySQLDB(Main plugin) {
        super(plugin, true);
    }

    protected Connection getNewConnection() {
        ConfigurationSection config = getConfigSection();
        setAccountTable("economy");
        setAccountsColumnUser("name");
        setAccountsColumnMoney("money");
        setAccountsColumnUUID("uuid");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
            return DriverManager.getConnection(url, USER, PASSWORD);
        } catch (Exception e) {
            return null;
        }
    }

    private ConfigurationSection getSection(ConfigurationSection parent, String childName) {
        ConfigurationSection child = parent.getConfigurationSection(childName);
        if (child == null) {
            child = parent.createSection(childName);
        }
        return child;
    }

    public void getConfigDefaults(ConfigurationSection section) {
        section.addDefault("host", "localhost");
        section.addDefault("port", 3306);
        section.addDefault("user", "root");
        section.addDefault("password", "minecraft");
        section.addDefault("database", "Fe");
        ConfigurationSection tables = getSection(section, "tables");
        tables.addDefault("accounts", "fe_accounts");
        ConfigurationSection columns = getSection(section, "columns");
        ConfigurationSection columnsAccounts = getSection(columns, "accounts");
        columnsAccounts.addDefault("username", "name");
        columnsAccounts.addDefault("money", "money");
        columnsAccounts.addDefault("uuid", "uuid");
    }

    public String getName() {
        return "MySQL";
    }
}