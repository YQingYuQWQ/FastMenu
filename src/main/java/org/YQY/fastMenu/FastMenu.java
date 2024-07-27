package org.YQY.fastMenu;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class FastMenu extends JavaPlugin {
    private Inventory menu;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("FastMenu is starting up...");

        // Save the default config if it doesn't exist
        saveDefaultConfig();

        // Create instances of MenuManager and MenuListener
        MenuManager menuManager = new MenuManager(this);
        menu = menuManager.createMenu();
        MenuListener menuListener = new MenuListener(this, menuManager);

        // Register MenuListener
        Bukkit.getPluginManager().registerEvents(menuListener, this);

        getLogger().info("FastMenu has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("FastMenu is shutting down...");
    }

    public Inventory getMenu() {
        return menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fm")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("fastmenu.reload")) {
                    reloadConfig();
                    menu = new MenuManager(this).createMenu();
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