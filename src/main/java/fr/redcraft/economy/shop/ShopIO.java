package fr.redcraft.economy.shop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.redcraft.economy.Main;

public class ShopIO {

    private static File file;
    private static Gson gson;

    public static File getDataFile() {
        if (file == null) {
            file = new File(Main.getInstance().getDataFolder(), "shop.json");
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }
    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            gson = builder.create();
        }
        return gson;
    }

    public static boolean writeFile(File file, String text) {
        if (file.exists()) {
            file.delete();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(text);
            return true;
        } catch (Exception e) {
            Main.getInstance().getLogger().log(Level.WARNING,"Failed to write to file: " + file.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e1) {
                    Main.getInstance().getLogger().log(Level.WARNING,"Failed to close FileWriter: " + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuilder out = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append('\n');
            }
            return out.toString();
        } catch (Exception e) {
            Main.getInstance().getLogger().log(Level.WARNING,"Failed to read to file: " + file.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e1) {
                    Main.getInstance().getLogger().log(Level.WARNING,"Failed to close FileWriter: " + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}