package fr.redcraft.economy.shop;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import fr.redcraft.economy.Main;
import fr.redcraft.economy.gui.GuiPickShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopHandler {

    private final List<ShopItem> shopItems = new LinkedList<ShopItem>();

    private ShopHandler() {
    }

    public ShopItem[] getItems(boolean buying) {
        List<ShopItem> items = new LinkedList<>();
        for (ShopItem item : shopItems) {
            if ((buying && item.getIsBuyingEnabled()) || (!buying && item.getIsSellingEnabled())) {
                items.add(item);
            }
        }
        return items.toArray(new ShopItem[0]);
    }

    public ShopItem[] getItemsOnPage(boolean buying, int page, int perPage) {
        ShopItem[] items = getItems(buying);
        if (page * perPage >= items.length) {
            return null;
        }
        ShopItem[] outItems = new ShopItem[Math.min(items.length - (page * perPage), perPage)];
        for (int i = page * perPage; i < ((page + 1) * perPage) && (i < items.length); i ++) {
            outItems[i - (page * perPage)] = items[i].clone();
        }
        return outItems;
    }

    public boolean getHasNextPage(boolean buying, int page, int perPage) {
        return ((page + 1) * perPage) < getItems(buying).length;
    }

    public boolean getHasPreviousPage(int page) {
        return page > 0;
    }

    public int getPages(boolean buying, int perPage) {
        return (int) Math.ceil(getItems(buying).length / (float) perPage);
    }

    public ShopItem getItem(Material item, short data) {
        for (ShopItem shopItem : shopItems) {
            if (shopItem.getMaterial().equals(item) && shopItem.getData() == data) {
                return shopItem;
            }
        }
        return null;
    }

    public boolean updateItem(ItemStack stack, double buyPrice, double sellPrice) {
        return updateItem(stack.getType(), stack.getDurability(), buyPrice, sellPrice);
    }

    public boolean updateItem(Material item, short data, double buyPrice, double sellPrice) {
        ShopItem sitem = getItem(item, data);
        if (sitem == null) {
            return false;
        }
        sitem.update(buyPrice, sellPrice);
        sortAndSave();
        return true;
    }

    public boolean addItem(ItemStack stack, double buyPrice, double sellPrice) {
        return addItem(stack.getType(), stack.getDurability(), buyPrice, sellPrice);
    }

    public boolean addItem(Material item, short data, double buyPrice, double sellPrice) {
        if (getItem(item, data) != null) {
            return false;
        }
        shopItems.add(new ShopItem(item, data, buyPrice, sellPrice));
        sortAndSave();
        return true;
    }

    public boolean removeItem(ItemStack stack) {
        return removeItem(stack.getType(), stack.getDurability());
    }

    public boolean removeItem(Material item, short data) {
        for (ShopItem shopItem : shopItems) {
            if (shopItem.getMaterial().equals(item) && shopItem.getData() == data) {
                shopItems.remove(shopItem);
                sortAndSave();
                return true;
            }
        }
        return false;
    }

    public void openShop(Player player) {
       Main.getGuiHandler().openGui(player, new GuiPickShop());
    }

    public BuyResult tryBuy(Player player, ShopItem item, int count) {
        if (!item.getIsBuyingEnabled()) {
            return BuyResult.CANNOT_BUY;
        }
        ItemStack stack = item.createStack(count);
        if (stack.getAmount() > stack.getMaxStackSize()) {
            stack.setAmount(stack.getMaxStackSize());
        }
        if (!Main.getApis().getAccount(player.getName(),player.getUniqueId().toString()).buy(stack.getAmount() * item.getBuyPrice())) {
            return BuyResult.NOT_ENOUGH_MONEY;
        }
        HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(stack.clone());
        if (remaining.size() > 0) {
            Main.getApis().getAccount(player.getName(),player.getUniqueId().toString()).deposit(remaining.entrySet().iterator().next().getValue().getAmount() * item.getBuyPrice());
            return BuyResult.INVENTORY_FULL;
        }
        return BuyResult.SUCCESS;
    }

    public SellResult trySell(Player player, ShopItem item, int count) {
        if (!item.getIsSellingEnabled()) {
            return SellResult.CANNOT_SELL;
        }
        if (count < 1) {
            return SellResult.NOT_ENOUGH_ITEMS;
        }
        ItemStack stack = item.createStack(1);
        if (!player.getInventory().containsAtLeast(stack, stack.getAmount())) {
            return SellResult.NOT_ENOUGH_ITEMS;
        }

        int tmp = count;
        do {
            ItemStack stmp = stack.clone();
            stmp.setAmount(Math.min(stack.getMaxStackSize(), tmp));
            tmp -= stack.getMaxStackSize();
            player.getInventory().removeItem(stmp);
        } while (tmp > 0);
        Main.getApis().getAccount(player.getName(),player.getUniqueId().toString()).deposit(count * item.getSellPrice());
        return SellResult.SUCCESS;
    }

    public void sortAndSave() {
        sort();
        ShopIO.writeFile(ShopIO.getDataFile(), ShopIO.getGson().toJson(this));
    }

    public void sort() {
            shopItems.sort(ShopItem::compareTo);
    }

    public static ShopHandler loadShopHandler() {
        String file = ShopIO.readFile(ShopIO.getDataFile());
        if (file != null) {
            try {
                ShopHandler handler = ShopIO.getGson().fromJson(file, ShopHandler.class);
                if (handler == null) {
                    throw new Exception("The shop JSON could not be parsed");
                }
                handler.sort();
                return handler;
            } catch (Exception e) {
                Main.getInstance().getLogger().log(Level.WARNING,"Failed to load shop items: " + e.getMessage());
                e.printStackTrace();
            }
        }
        ShopHandler nbh = new ShopHandler();
        return nbh;
    }
}
