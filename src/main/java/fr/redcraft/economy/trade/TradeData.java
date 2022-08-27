package fr.redcraft.economy.trade;

import fr.redcraft.economy.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TradeData implements Listener {

    public static HashMap<Player, TradeData> pendingRequests = new HashMap<>();
    ItemStack[][] items;
    private Inventory[] inventories;
    private Player[] players;
    private boolean tradeClosed;
    private boolean[] accepted;
    Timer requestTimeout;
    Timer finishTrade;
    Timer sync;
    CopyOnWriteArrayList<Integer> events;

    public TradeData(Player sender, Player receiver) {
        if (pendingRequests.containsKey(receiver))
            ((TradeData)pendingRequests.get(receiver)).requestTimeout.cancel();
        pendingRequests.put(receiver, this);
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        this.accepted = new boolean[2];
        this.players = new Player[2];
        this.inventories = new Inventory[2];
        this.players[0] = sender;
        this.players[1] = receiver;
        this.requestTimeout = new Timer();
        this.requestTimeout.schedule(new TimeOutTask(this), 10000L);
        this.events = new CopyOnWriteArrayList<>();
        this.sync = new Timer();
        this.sync.schedule(new UpdateSync(this.events), 0L, 250L);
    }

    @EventHandler
    public void InventoryDrag(InventoryDragEvent event) {
        if (!this.tradeClosed) {
            int index = -1;
            if (this.inventories[0] != null && event.getInventory().equals(this.inventories[0]))
                index = 0;
            if (this.inventories[1] != null && event.getInventory().equals(this.inventories[1]))
                index = 1;
            for (Iterator<Integer> iterator = event.getRawSlots().iterator(); iterator.hasNext(); ) {
                int slot = ((Integer)iterator.next()).intValue();
                int sRow = slot / 9;
                int sColumn = slot % 9;
                if ((sColumn == 0 && (sRow == 0 || sRow == 8)) || sColumn > 3) {
                    event.setCancelled(false);
                    return;
                }
            }
            if (index != -1)
                this.events.add(Integer.valueOf(index));
        }

    }

    @EventHandler
    public void InventoryEdit(InventoryClickEvent event) {
        if (!this.tradeClosed && Arrays.<Player>asList(this.players).contains(event.getWhoClicked())) {
            if (event.getInventory() == null)
                return;
            int index = -1;
            if (this.inventories[0] != null && event.getInventory().equals(this.inventories[0]))
                index = 0;
            if (this.inventories[1] != null && event.getInventory().equals(this.inventories[1]))
                index = 1;
            int sRow = event.getRawSlot() / 9;
            int sColumn = event.getRawSlot() % 9;
            if ((index != -1 || this.players[0].getOpenInventory().getTopInventory().equals(this.inventories[0]) || this.players[0]
                    .getOpenInventory().getTopInventory().equals(this.inventories[0])) &&
                    event.getClick() != ClickType.RIGHT && event.getClick() != ClickType.LEFT) {
                event.setCancelled(true);
                return;
            }
            if (index != -1) {
                if ((sColumn == 0 && (sRow == 0 || sRow == 8)) || sColumn > 3) {
                    event.setCancelled(true);
                    if (sColumn == 0)
                        if (!this.accepted[index] && canFit((Inventory)this.players[index].getInventory(), getOffer(1 - index))) {
                            acceptTrade(index);
                        } else {
                            declineTrade();
                        }
                    return;
                }
                this.events.add(Integer.valueOf(index));
            }
        }
    }

    public boolean canFit(Inventory inv, ItemStack[] offer) {
        inv = cloneInventory(inv);
        for (ItemStack item : offer) {
            if (inv.addItem(new ItemStack[] { item }).size() > 0) {
                inv.clear();
                return false;
            }
        }
        inv.clear();
        return true;
    }

    public Inventory cloneInventory(Inventory inv) {
        Inventory cloned = Bukkit.createInventory(null,54);
        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                cloned.setItem(i, items[i].clone());
            } else {
                cloned.addItem(new ItemStack[] { new ItemStack(Material.AIR) });
            }
        }
        return cloned;
    }

    public void declineTrade() {
        this.tradeClosed = false;
        this.accepted[0] = false;
        this.accepted[1] = false;
        addTradingDesign(0);
        addTradingDesign(1);
        if (this.finishTrade != null)
            this.finishTrade.cancel();
    }

    public void acceptTrade(int index) {
        @SuppressWarnings("deprecation")
        ItemStack decline = new ItemStack(Material.LIME_WOOL, 1, (short) 13);

        ItemMeta meta = decline.getItemMeta();
        meta.setDisplayName("Accepter.");
        meta.setLore(Collections.singletonList("Click pour refuser."));
        decline.setItemMeta(meta);
        this.inventories[index].setItem(0, decline);
        meta = meta.clone();
        meta.setLore(Collections.singletonList("attendre..."));
        decline = decline.clone();
        decline.setItemMeta(meta);
        this.inventories[1 - index].setItem(8, decline);
        this.accepted[index] = true;
        if (this.accepted[0] && this.accepted[1]) {
            this.finishTrade = new Timer();
            this.finishTrade.schedule(new FinishTrade(this), 0L, 1000L);
        }
    }
    private class FinishTrade extends TimerTask {
        TradeData inventory;
        int count;
        public FinishTrade(TradeData TradeData) {

            this.inventory = TradeData;
            Bukkit.getScheduler ().runTaskLater (Main.getInstance(), () ->  closeInventory(players[0],players[1]), 130);

        }

        public void run() {
            if (this.count == 6) {
                TradeData.applyTradeTaxes(TradeData.this.players[0], TradeData.this.players[1]);
                TradeData.this.closeTrade(1, 0);

                TradeData.this.players[0].getOpenInventory().close();
                TradeData.this.players[1].getOpenInventory().close();

                TradeData.pendingRequests.remove(TradeData.this.players[1]);
                cancel();
                return;
            }
            int slot = 9 * (5 - this.count) + 4;
            ItemMeta glassMeta = this.inventory.inventories[0].getItem(slot).getItemMeta();
            @SuppressWarnings("deprecation")
            ItemStack glass = new ItemStack(Material.LIME_STAINED_GLASS, 1, (short)5);
            glass.setItemMeta(glassMeta);
            this.inventory.inventories[0].setItem(slot, glass);
            this.inventory.inventories[1].setItem(slot, glass);
            this.count++;

        }
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {

        if (!this.tradeClosed) {
            int index = -1;
            if (this.inventories[0] != null && event.getInventory().equals(this.inventories[0]))
                index = 0;
            if (this.inventories[1] != null && event.getInventory().equals(this.inventories[1]))
                index = 1;
            if (index != -1 &&
                    closeTrade(0, 1)) {
                if (this.finishTrade != null) this.finishTrade.cancel();
                this.players[1 - index].closeInventory();
                pendingRequests.remove(this.players[1]);
                this.players[0].sendMessage(Main.PREFIX + "Trade Annulé.");
                this.players[1].sendMessage(Main.PREFIX + "Trade Annulé.");
            }
        }
    }
    public boolean closeTrade(int loots1, int loots2) {

        if (!this.tradeClosed) {
            for (ItemStack item : getOffer(0)) {
                this.players[loots1].getInventory().addItem(new ItemStack[] { item });
            }
            this.players[loots1].updateInventory();
            for (ItemStack item : getOffer(1)) {
                this.players[loots2].getInventory().addItem(new ItemStack[] { item });
            }
            this.players[loots2].updateInventory();
            this.tradeClosed = true;
            this.sync.cancel();
            this.inventories[0].clear();
            this.inventories[1].clear();

            return true;
        }
        return false;
    }

    public static TradeData acceptTradeRequest(Player player) {

        if (pendingRequests.containsKey(player)) {
            TradeData tempInventory = pendingRequests.get(player);
            pendingRequests.remove(player);
            tempInventory.requestTimeout.cancel();
            tempInventory.players[0].sendMessage(Main.PREFIX + tempInventory.players[1].getName() + " a accepté votre demande d'échange.");
            return tempInventory;
        }
        return null;
    }

    public void initiateTrade() {
        String fNameOne = String.format("%-16s", new Object[] { this.players[0].getName() });
        String fNameTwo = String.format("%-16s", new Object[] { this.players[1].getName() });
        this.inventories[0] = Bukkit.getServer().createInventory(null, 54, fNameOne + fNameTwo);
        addTradingDesign(0);
        this.players[0].closeInventory();
        this.players[0].openInventory(this.inventories[0]);
        this.players[0].updateInventory();
        this.inventories[1] = Bukkit.getServer().createInventory(null, 54, fNameTwo + fNameOne);
        addTradingDesign(1);
        this.players[1].closeInventory();
        this.players[1].openInventory(this.inventories[1]);
        this.players[1].updateInventory();
    }

    public void addTradingDesign(int index) {
        ItemStack accept = new ItemStack(Material.WHITE_WOOL, 1, (short)14);
        ItemMeta meta = accept.getItemMeta();
        meta.setDisplayName("Refuser");
        meta.setLore(Collections.singletonList(Main.PREFIX + "Cliquez pour accepter. Ne fonctionnera pas sans espace dans l'inventaire ."));
        accept.setItemMeta(meta);
        this.inventories[index].setItem(0, accept);
        meta = meta.clone();
        meta.setLore(Collections.singletonList("Attente..."));
        accept = accept.clone();
        accept.setItemMeta(meta);
        this.inventories[index].setItem(8, accept);
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS, 1);
        meta = glass.getItemMeta();
        meta.setDisplayName("");
        meta.setLore(Arrays.asList(new String[] { ("<- Ton coté"), ("Sont coté ->") }));
        glass.setItemMeta(meta);
        for (int i = 0; i < 6; i++)
            this.inventories[index].setItem(4 + 9 * i, glass);
    }

    public ItemStack[] getOffer(int p) {
        ItemStack[] contents = this.inventories[p].getContents();
        ArrayList<ItemStack> offer = new ArrayList<>();
        for (int i = 1; i < 54; i++) {
            int column = i % 9;
            if (column < 4 && contents[i] != null)
                offer.add(contents[i]);
        }
        return offer.<ItemStack>toArray(new ItemStack[0]);
    }

    private class UpdateSync extends TimerTask {
        CopyOnWriteArrayList<Integer> events;

        public UpdateSync(CopyOnWriteArrayList<Integer> events) {
            this.events = events;
        }

        public void run() {
            while (!this.events.isEmpty()) {
                int index = ((Integer)this.events.remove(0)).intValue();
                for (int i = 1; i < 54; i++) {
                    int row = i / 9;
                    int column = i % 9;
                    if (column < 4) {
                        ItemStack item = new ItemStack(Material.AIR);
                        if (TradeData.this.inventories[index].getItem(i) != null)
                            item = TradeData.this.inventories[index].getItem(i).clone();
                        TradeData.this.inventories[1 - index].setItem(row * 9 - column + 8, item);
                        TradeData.this.declineTrade();
                    }
                }
            }
            if (TradeData.this.accepted[0] && TradeData.this.accepted[1]) {
                if (!TradeData.this.canFit((Inventory)TradeData.this.players[1].getInventory(), TradeData.this.getOffer(0)))
                    TradeData.this.declineTrade();
                if (!TradeData.this.canFit((Inventory)TradeData.this.players[0].getInventory(), TradeData.this.getOffer(1)))
                    TradeData.this.declineTrade();
            }
        }
    }

    private class TimeOutTask extends TimerTask {
        TradeData inventory;

        public TimeOutTask(TradeData inventory) {
            this.inventory = inventory;
        }

        public void run() {
            TradeData.pendingRequests.remove(this.inventory.players[1]);
            this.inventory.players[0].sendMessage(Main.PREFIX + "La demande de trade " + TradeData.this.players[1].getName() + " a expirée.");
            this.inventory.players[1].sendMessage(Main.PREFIX + "la demande de trade " + TradeData.this.players[0].getName() + " a expirée.");
        }
    }


    public static void applyTradeTaxes(Player player, Player receiver) {

        Location locCaller = player.getLocation();
        Location locReceiver = receiver.getLocation();

        if (player instanceof Player && receiver instanceof Player) {

            int distance = (int) locCaller.distance(locReceiver);
            System.out.println(distance);
            if (distance <100) {applyTaxe(2, player, receiver);}
            else if(distance >100	  && distance <200) {applyTaxe(10, player, receiver); }
            else if(distance >200	  && distance <300) {applyTaxe(20, player, receiver);}
            else if(distance >300	  && distance <400) {applyTaxe(30, player, receiver);}
            else if(distance >400	  && distance <500) {applyTaxe(40, player, receiver);}
            else if(distance >500	  && distance <600) {applyTaxe(50, player, receiver);}
            else if(distance >600	  && distance <700) {applyTaxe(60, player, receiver);}
            else if(distance >700	  && distance <800) {applyTaxe(70, player, receiver);}
            else if(distance >800 	  && distance <900) {applyTaxe(80, player, receiver);}
            else if(distance >900 	  && distance <1000) {applyTaxe(90, player, receiver);}
            else if(distance >1000    && distance <5000) {applyTaxe(100, player, receiver);}
            else if(distance >5000    && distance <10000) {applyTaxe(500, player, receiver);}
            else if(distance >10000   && distance <30000) {applyTaxe(1000, player, receiver);}
            else if(distance >30000   && distance <70000) {applyTaxe(2000, player, receiver);}
            else if(distance >70000   && distance <100000) {applyTaxe(6000, player, receiver);}
            else if(distance >100000 ) {applyTaxe(60000, player, receiver);}
        }
    }


    private static void applyTaxe(double percent, Player player, Player receiver ){
        Bukkit.broadcastMessage("percent -> " + percent);
        Bukkit.broadcastMessage("player -> " + player.getName());
        Bukkit.broadcastMessage("receiver -> " + receiver.getName());
    }

    private static void closeInventory(Player player, Player target) {
        player.closeInventory();
        target.closeInventory();
    }
}
