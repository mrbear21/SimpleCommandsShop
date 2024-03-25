package me.shpcmds.brain;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.shpcmds.commands.SimpleCommandsShopCommand;
import me.shpcmds.utils.SimpleCommandsShopInventory;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Spigot extends JavaPlugin {
    static File config;

    private static Economy econ;

    private static Permission perms;

    private static Spigot INSTANCE;

    public void onEnable() {
        INSTANCE = this;

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", new Object[] {
                getDescription().getName()
            }));
            getServer().getPluginManager().disablePlugin((Plugin) this);
            return;
        }
        setupPermissions();
        createConfig();

        new SimpleCommandsShopCommand().initialize();
        new SimpleCommandsShopInventory().initialize();
        getLogger().info("SimpleShopCommands is ready to be helpful for the all BreadMakers!");
    }

    public void onDisable() {
        getLogger().info("SimpleShopCommands has stopped it's service!");
    }

    public static Spigot getInstance() {
        return INSTANCE;
    }

    public void createConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            getLogger().info("config.yml not found, creating....");
            saveDefaultConfig();
        } else {
            getLogger().info("config.yml found, loadinig.....");
        }
        
        getConfig().addDefault("messages.title", "Command Shop");
        getConfig().addDefault("messages.invalid-permission", "There is no permission set for this command!");
        getConfig().addDefault("messages.purchased", "Purchased");
        getConfig().addDefault("messages.unavailable", "Unavailable");
        getConfig().addDefault("messages.command-purchased", "Command Purchased!");
        getConfig().addDefault("messages.insufficient-money",  "You don't have enough Money!");
        getConfig().addDefault("messages.no-permission", "You Don't have permission!");
        getConfig().addDefault("messages.already-own", "You Already have this Command!");
        getConfig().addDefault("messages.invalid-price", "Invalid Price!");
        getConfig().options().copyDefaults(true);
        
        saveConfig();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider < Economy > rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        setEcon((Economy) rsp.getProvider());
        return (getEcon() != null);
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider < Permission > rsp = getServer().getServicesManager().getRegistration(Permission.class);
            setPerms((Permission) rsp.getProvider());
            return (getPerms() != null);
        }
        return false;
    }

    public Economy getEcon() {
        return econ;
    }

    public static void setEcon(Economy econ) {
        Spigot.econ = econ;
    }

    public Permission getPerms() {
        return perms;
    }

    public static void setPerms(Permission perms) {
        Spigot.perms = perms;
    }

}