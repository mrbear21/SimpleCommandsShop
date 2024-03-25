package me.shpcmds.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.shpcmds.brain.Spigot;
import me.shpcmds.utils.SimpleCommandsShopInventory;
import me.shpcmds.utils.SimpleCommandsShopMessages;

public class SimpleCommandsShopCommand implements CommandExecutor {

    public SimpleCommandsShopCommand() {

    }

    public void initialize() {
        Spigot.getInstance().getCommand("scs").setExecutor(this);
        Spigot.getInstance().getCommand("cmdshop").setExecutor(this);
        //if (Spigot.getInstance().getCommand("cmds") == null) {
            Spigot.getInstance().getCommand("cmds").setExecutor(this);
        //}
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Spigot plugin = Spigot.getInstance();
        Player player = (Player) sender;

        if (args.length > 0) {
            switch (args[0]) {
                case "":
                case "reload":
                    if (sender.hasPermission("scs.reload")) {
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getConfig().getString("messages.reloaded", "ShopCommands reloaded."));
                        return true;
                    }
            }
        }

        if (sender.hasPermission("scs.gui")) {
            int size = SimpleCommandsShopInventory.formatSize(plugin.getConfig().getConfigurationSection("commands").getKeys(false).size() - 1);
            Inventory inv = Bukkit.createInventory(null, size, SimpleCommandsShopMessages.getMessage("title"));
            inv = SimpleCommandsShopInventory.craftInv(inv, (Player) sender);
            player.openInventory(inv);

            return true;
        }
        sender.sendMessage(plugin.getConfig().getString("messages.no-permission", "You don't have permission to use that."));
        return true;
    }

}