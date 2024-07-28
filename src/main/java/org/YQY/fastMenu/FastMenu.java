package org.YQY.fastMenu;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getPluginManager;

public final class FastMenu extends JavaPlugin {
    HashMap<String, Inventory> menuList = new HashMap<String, Inventory>();
    HashMap<String, FileConfiguration> menuConfigs = new HashMap<>();
    HashMap<Player, String> currentOpenedMenuName = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("FastMenu is starting up..");

        saveDefaultConfig();
        // Create Default Config Folder.
        File menuFolder = new File(getDataFolder(), "Menu");
        if (!menuFolder.exists()) {
            menuFolder.mkdirs();
            saveResource("main_menu.yml", false);
        }

        MenuManager menuManager = new MenuManager(this);
        menuManager.createAllMenu(menuList, menuConfigs);

        MenuListener menuListener = new MenuListener(this, menuManager);

        // Register MenuListener
        getPluginManager().registerEvents(menuListener, this);

        getLogger().info("FastMenu has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("FastMenu is shutting down...");
    }

    public Inventory getMenu(String id) {
        return menuList.get(id);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fm")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("fastmenu.reload")) {
                    reloadConfig();
                    sender.sendMessage("FastMenu configuration reloaded.");
                    return true;
                } else {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
            }
        }
        return false;
    }
}