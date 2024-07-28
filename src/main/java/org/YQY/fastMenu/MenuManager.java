// MenuManager.java
package org.YQY.fastMenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class MenuManager {
    private FastMenu plugin;

    public MenuManager(FastMenu plugin) {
        this.plugin = plugin;
    }

    public void createAllMenu(HashMap<String, Inventory> list, HashMap<String, FileConfiguration> configMap){
        FileConfiguration config = plugin.getConfig();
        File folder = plugin.getDataFolder();
        File menuFolder = new File(folder, "Menu");
        File[] configFiles = menuFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (configFiles != null){
            for (File file : configFiles){
                String configName = file.getName();
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                AbstractMap.SimpleEntry<String, Inventory> TempInventory = createMenu(configuration);
                list.put(TempInventory.getKey(), TempInventory.getValue());
                configMap.put(TempInventory.getKey(), configuration);
            }
        }
    }

    public AbstractMap.SimpleEntry<String, Inventory> createMenu(FileConfiguration config) {
        int size = config.getInt("menu.size");
        String title = config.getString("menu.title");// 菜单标题
        title = ChatColor.translateAlternateColorCodes('&', title);
        Inventory menu = Bukkit.createInventory(null, size, title);// 创建菜单

        // 遍历菜单项
        for (String key : Objects.requireNonNull(config.getConfigurationSection("menu.items")).getKeys(false)) {
            String name = config.getString("menu.items." + key + ".name");// 菜单项名称
            name = ChatColor.translateAlternateColorCodes('&', name);
            Material material = Material.getMaterial(config.getString("menu.items." + key + ".material"));// 菜单项材质
            int slot = config.getInt("menu.items." + key + ".slot");// 菜单项槽位
            int amount = config.getInt("menu.items." + key + ".amount");// 菜单项数量

            ItemStack item = new ItemStack(material, amount);// 创建菜单项
            ItemMeta meta = item.getItemMeta();

            // 获取物品的附魔列表
            if (config.isConfigurationSection("menu.items." + key + ".enchants")) {
                for (String enchantName : config.getConfigurationSection("menu.items." + key + ".enchants").getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByName(enchantName);
                    int level = config.getInt("menu.items." + key + ".enchants." + enchantName);
                    if (enchantment != null) {
                        meta.addEnchant(enchantment, level, true);
                    } else {
                        plugin.getLogger().warning("Unknown enchantment: " + enchantName);
                    }
                }
            }

            String lore = config.getString("menu.items." + key + ".lore");// 菜单项描述
            lore = ChatColor.translateAlternateColorCodes('&', lore);

            List<String> loreList = new ArrayList<>();
            loreList.add(lore);
            meta.setLore(loreList);

            meta.setDisplayName(name);
            item.setItemMeta(meta);
            menu.setItem(slot,item);
        }

        plugin.getLogger().info("Menu created.");
        return new AbstractMap.SimpleEntry<String, Inventory>(config.getString("menu.id"), menu);
    }


    public void DoCommandByItemName(Player currentPlayer, String itemName) {
        String currentInventoryName = plugin.currentOpenedMenuName.get(currentPlayer);
        FileConfiguration currentInventoryConfig = plugin.menuConfigs.get(currentInventoryName);
        for (String key : currentInventoryConfig.getConfigurationSection("menu.items").getKeys(false)) {
            String name = currentInventoryConfig.getString("menu.items." + key + ".name");
            if (name.equals(itemName)) {
                String command = currentInventoryConfig.getString("menu.items." + key + ".command");
                if (command != null) {
                    currentPlayer.performCommand(command);
                    return;
                }
                String jump = currentInventoryConfig.getString("menu.items." + key + ".jump");
                if (jump != null) {
                    currentPlayer.closeInventory();
                    if (currentPlayer.openInventory(plugin.menuList.get(jump)) != null){
                        plugin.currentOpenedMenuName.put(currentPlayer, jump);
                    }
                    return;
                }
            }
        }
    }
}