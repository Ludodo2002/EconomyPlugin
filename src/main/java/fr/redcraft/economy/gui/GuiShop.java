package fr.redcraft.economy.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.helper.ItemBuilder;
import fr.redcraft.economy.shop.BuyResult;
import fr.redcraft.economy.shop.SellResult;
import fr.redcraft.economy.shop.ShopHandler;
import fr.redcraft.economy.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiShop implements IGui {

    public static final int ITEMS_PER_PAGE = 5 * 9;

    private int page = 0;
    private final ShopHandler shopHandler;
    private final boolean buying;
    private Inventory inventory;
    private Player player;
    private ShopItem[] items;

    public GuiShop(ShopHandler shopHandler, boolean buying) {
        this.shopHandler = shopHandler;
        this.buying = buying;
    }

    public void onOpen(Inventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
        refresh();
    }

    public void onClose(Inventory inventory, Player player) {
    }

    public void onClick(Inventory inventory, Player player, int slot, ClickType clickType, ItemStack stack) {
        if (!(clickType.isLeftClick() || clickType.isShiftClick() || clickType.isRightClick())) {
            return;
        }
        if (items == null) {
            player.sendMessage(Main.PREFIX + "§cAn internal error occurred, please contact an admin or moderator");
            return;
        }
        if (slot < items.length) {
            ShopItem item = items[slot];
            if (buying) {
                if (!(clickType.isLeftClick() || clickType.isShiftClick())) {
                    return;
                }
                BuyResult result = shopHandler.tryBuy(player, item, (clickType.isShiftClick()) ? item.getMaxStack() : 1);
                switch (result) {
                    case CANNOT_BUY:
                        player.sendMessage(Main.PREFIX + "Désolé cette item n'est pas disponnible");
                        break;
                    case NOT_ENOUGH_MONEY:
                        player.sendMessage(Main.PREFIX + "Vous n'avez pas assez d'argent");
                        break;
                    case INVENTORY_FULL:
                        player.sendMessage(Main.PREFIX + "Votre inventaire est plein");
                        break;
                    default:
                        break;
                }
                return;
            }
            SellResult result = shopHandler.trySell(player, item, ((clickType.isRightClick()) ? getCountOfItemOnPlayer(player, stack) : ((clickType.isShiftClick()) ? item.getMaxStack() : 1)));
            switch (result) {
                case CANNOT_SELL:
                    player.sendMessage(Main.PREFIX + "Désolé cette item n'est pas disponnible");
                    break;
                case NOT_ENOUGH_ITEMS:
                    player.sendMessage(Main.PREFIX + "Vous n'avez pas assez d'item");
                    break;
                default:
                    break;
            }
            return;
        }

        // Previous Page
        if (slot == 45) {
            page--;
            refresh();
        }
        // Back
        if (slot == 49) {
            shopHandler.openShop(player);
        }
        // Next Page
        if (slot == 53) {
            page++;
            refresh();
        }
    }

    private int getCountOfItemOnPlayer(Player player, ItemStack stack) {
        int count = 0;
        for (ItemStack stack1 : player.getInventory().getContents()) {
            if (stack1 != null && stack1.getType().equals(stack.getType()) && stack1.getDurability() == stack.getDurability()) {
                count += stack1.getAmount();
            }
        }
        return count;
    }

    public String getName() {
        return "Shop - " + ((buying) ? "Achat" : "Vente");
    }

    public int getRows() {
        return 6;
    }

    public void refresh() {
        inventory.clear();
        items = shopHandler.getItemsOnPage(buying, page, ITEMS_PER_PAGE);
        if (items == null) {
            Main.getInstance().getLogger().log(Level.WARNING, "Shop out of bounds for player: " + player.getName() + " on page " + page + " when there can only be " + shopHandler.getPages(buying, ITEMS_PER_PAGE));
            player.sendMessage(Main.PREFIX + "§cAn internal error occurred, please contact an admin or moderator");
            player.closeInventory();
            return;
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                Main.getInstance().getLogger().log(Level.WARNING, "Item was null at index " + i + " which is item " + (i + page * ITEMS_PER_PAGE) + " in the shop");
                continue;
            }
            ItemStack stack = items[i].createStack(1);
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (buying) {
                lore.add("§7Prix : §f" + items[i].getBuyPrice() + "§6₡");
                lore.add("§8§m               ");
                lore.add("§7§oLeft Click pour en acheter 1");
                lore.add("§7§oShift + Left Click pour tout acheter");
            } else {
                lore.add("§7Prix : §f" + items[i].getSellPrice() + "§6₡");
                lore.add("§8§m               ");
                lore.add("§7§oLeft Click pour en vendre 1");
                lore.add("§7§oShift + Left Click pour tout vendre");
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
            inventory.setItem(i, stack);
        }
        // Non-shop items

        if (shopHandler.getHasPreviousPage(page)) {
            ItemStack prev = new ItemBuilder("Précédent", Material.PLAYER_HEAD,1).setSkullTextures("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5NzExMjRiZTg5YWM3ZGM5YzkyOWZlOWI2ZWZhN2EwN2NlMzdjZTFkYTJkZjY5MWJmODY2MzQ2NzQ3N2M3In19fQ==").build();
            inventory.setItem(45, prev);
        }
        ItemStack back = new ItemBuilder("§cRetour", Material.SPECTRAL_ARROW, 1).build();
        inventory.setItem(49, back);
        if (shopHandler.getHasNextPage(buying, page, ITEMS_PER_PAGE)) {
            ItemStack next = new ItemBuilder("Suivant", Material.PLAYER_HEAD,1).setSkullTextures("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY3MWM0YzA0MzM3YzM4YTVjN2YzMWE1Yzc1MWY5OTFlOTZjMDNkZjczMGNkYmVlOTkzMjA2NTVjMTlkIn19fQ==").build();
            inventory.setItem(53, next);
        }
    }
}
