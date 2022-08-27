package fr.redcraft.economy.helper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;


    public ItemBuilder(Material material)
    {
        this(material, 1);
    }

    public ItemBuilder(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount)
    {
        itemStack = new ItemStack(material, amount);
    }


    public ItemBuilder(String name,Material material,int amount){
        itemStack = new ItemStack(material,amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public ItemBuilder(Material material, int amount, short data)
    {
        itemStack = new ItemStack(material, amount, data);
    }

    public ItemBuilder(Material material, int amount, short data, String owner)
    {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(owner);
        skull.setItemMeta(skullMeta);
        itemStack = skull;
    }

    @Override
    public ItemBuilder clone()
    {
        return new ItemBuilder(itemStack);
    }


    public ItemBuilder setName(String name)
    {
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(name);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line)
    {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }


    public ItemBuilder addLoreLine(String line, int pos)
    {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable)
    {
        ItemMeta im = itemStack.getItemMeta();
        im.setUnbreakable(unbreakable);
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE});
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES});
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_DESTROYS});
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment,int level){
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(enchantment,level,true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setDurability(short durability){
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setCustomModelData(int modelData){
        ItemMeta im = itemStack.getItemMeta();
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color){
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(color);
        itemStack.setItemMeta(leatherArmorMeta);
        return this;
    }

    public ItemBuilder setSkullTextures(String textures) {
        SkullMeta headMeta = (SkullMeta)this.itemStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", textures));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
            this.itemStack.setItemMeta(headMeta);
        } catch (NoSuchFieldException|IllegalAccessException ignored) {}
        return this;
    }

    public ItemStack build()
    {
        return itemStack;
    }
}

