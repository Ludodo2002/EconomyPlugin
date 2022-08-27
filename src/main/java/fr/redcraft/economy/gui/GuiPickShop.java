package fr.redcraft.economy.gui;


import fr.redcraft.economy.Main;
import fr.redcraft.economy.helper.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiPickShop implements IGui {

    public void onOpen(Inventory inventory, Player player) {
        ItemStack buy = new ItemBuilder("Achat", Material.DIAMOND,1).build();
        ItemStack sell = new ItemBuilder("Vente", Material.EMERALD,1).build();
        ItemStack exit = new ItemBuilder("Quitter", Material.ARROW,1).build();

        inventory.setItem(2, buy);
        inventory.setItem(3, sell);
        inventory.setItem(5, exit);
    }

    public void onClose(Inventory inventory, Player player) {
    }

    public void onClick(Inventory inventory, Player player, int slot, ClickType clickType, ItemStack stack) {
        if (slot == 2) {
            open(player, true);
            return;
        }
        if (slot == 3) {
            open(player, false);
            return;
        }
        if (slot == 5) {
            player.closeInventory();
        }
    }

    private void open(Player player, boolean buying) {
        Main.getGuiHandler().openGui(player, new GuiShop(Main.getShop(), buying));
    }

    public String getName() {
        return "Shop Operation";
    }

    public int getRows() {
        return 1;
    }

}