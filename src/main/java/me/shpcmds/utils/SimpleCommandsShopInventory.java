package me.shpcmds.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.shpcmds.brain.Spigot;

public class SimpleCommandsShopInventory implements Listener {

    public SimpleCommandsShopInventory() {

    }

    public void initialize() {
        Spigot.getInstance().getServer().getPluginManager().registerEvents(this, Spigot.getInstance());
    }

    @EventHandler
    public void thatsNotSuposedToBeHere(PlayerCommandPreprocessEvent event) {
    	if (event.getMessage().equals("//set")) {
    		event.setCancelled(true);
    		event.getPlayer().chat("//set "+event.getPlayer().getInventory().getItemInMainHand().getType().name());
    	}
    	if (event.getMessage().equals("//replace")) {
    		event.setCancelled(true);
    		event.getPlayer().chat("//replace "+event.getPlayer().getInventory().getItemInMainHand().getType().name());
    	}
    }
    
    @EventHandler
    public void shopClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().equals(SimpleCommandsShopMessages.getMessage("title")))
            return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
            return;
        ItemStack purchased = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta pMeta = purchased.getItemMeta();
        ItemStack item = e.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();
        pMeta.setDisplayName(name);
        Double price = Double.valueOf(Spigot.getInstance().getConfig().getDouble("commands." + name + ".price"));
        if (!meta.getLore().get(0).equalsIgnoreCase(SimpleCommandsShopMessages.getMessage("purchased")) && !meta.getDisplayName().equalsIgnoreCase(SimpleCommandsShopMessages.getMessage("unavailable"))) {
            if (Spigot.getInstance().getConfig().get("commands." + name + ".permissions") == null) {
                e.getWhoClicked().sendMessage(ChatColor.RED + SimpleCommandsShopMessages.getMessage("invalid-permission"));
                e.setCancelled(true);
                return;
            }
            if (Spigot.getInstance().getEcon().getBalance(p) >= Spigot.getInstance().getConfig().getDouble("commands." + String.valueOf(name) + ".price")) {
                Spigot.getInstance().getEcon().withdrawPlayer(p, price.doubleValue());
                if (Spigot.getInstance().getConfig().isList("commands." + String.valueOf(name) + ".permissions")) {
                    for (String perm : Spigot.getInstance().getConfig().getStringList("commands." + String.valueOf(name) + ".permissions")) {
                        Spigot.getInstance().getPerms().playerAdd(p, perm);
                    }
                } else {
                    Spigot.getInstance().getPerms().playerAdd(p, Spigot.getInstance().getConfig().getString("commands." + String.valueOf(name) + ".permissions"));
                }
                p.closeInventory();
                int size = formatSize(Spigot.getInstance().getConfig().getConfigurationSection("commands").getKeys(false).size() - 1);
                Inventory inv = Bukkit.createInventory(null, size, SimpleCommandsShopMessages.getMessage("title"));
                inv = craftInv(inv, p);
                p.openInventory(inv);
                p.sendMessage(ChatColor.GREEN + SimpleCommandsShopMessages.getMessage("command-purchased").replace("%COMMAND%", String.valueOf(name)));
            } else {
                p.sendMessage(ChatColor.RED + SimpleCommandsShopMessages.getMessage("insufficient-money"));
                return;
            }
            e.setCancelled(true);
            return;
        }
        if (meta.getDisplayName().equalsIgnoreCase(SimpleCommandsShopMessages.getMessage("unavailable"))) {
            e.getWhoClicked().sendMessage(ChatColor.RED + SimpleCommandsShopMessages.getMessage("no-permission"));
            e.setCancelled(true);
            return;
        }
        if (meta.getLore().get(0).equalsIgnoreCase(SimpleCommandsShopMessages.getMessage("purchased"))) {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + SimpleCommandsShopMessages.getMessage("already-own"));
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
    }

    public static int formatSize(int size) {
        if (size <= 9)
            return 9;
        if (size <= 27)
            return 27;
        return 54;
    }

    public static Inventory craftInv(Inventory inv, Player p) {
        Inventory newInv = inv;
        ConfigurationSection commandsConfig = Spigot.getInstance().getConfig().getConfigurationSection("commands");
        if (commandsConfig != null) {
            for (String command: commandsConfig.getKeys(false)) {
                ItemStack com = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemMeta comMeta = com.getItemMeta();
                ArrayList < String > lores = new ArrayList < > ();
                if (!p.hasPermission("scs.purchase." + command) && !p.hasPermission("scs.purchase.*")) {
                    com = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
                    comMeta.setDisplayName(SimpleCommandsShopMessages.getMessage("unavailable"));
                    lores.add(0, SimpleCommandsShopMessages.getMessage("no-permission"));
                } else if (Spigot.getInstance().getConfig().get("commands." + command + ".permissions") != null && p.hasPermission(Spigot.getInstance().getConfig().isList("commands." + command + ".permissions") ? Spigot.getInstance().getConfig().getStringList("commands." + command + ".permissions").get(0) : Spigot.getInstance().getConfig().getString("commands." + command + ".permissions"))) {
                    com = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    comMeta.setDisplayName(command);
                    lores.add(0, SimpleCommandsShopMessages.getMessage("purchased"));
                } else {
                    String price = Spigot.getInstance().getConfig().getString("commands." + command + ".price", SimpleCommandsShopMessages.getMessage("invalid-price"));
                    if (price.equals(SimpleCommandsShopMessages.getMessage("invalid-price"))) {
                        com = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
                    }
                    comMeta.setDisplayName(command);
                    lores.add(0, Spigot.getInstance().getEcon().currencyNamePlural() + price);
                }
                comMeta.setLore(lores);
                com.setItemMeta(comMeta);
                newInv.addItem(com);
            }
        }
        return newInv;
    }

}