// MenuManager.java
package org.YQY.fastMenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuManager {
    private FastMenu plugin;

    public MenuManager(FastMenu plugin) {
        this.plugin = plugin;
    }

    public Inventory createMenu() {
        FileConfiguration config = plugin.getConfig();
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
        return menu;
    }

    public String getCommandByItemName(String itemName) {
        FileConfiguration config = plugin.getConfig();
        for (String key : config.getConfigurationSection("menu.items").getKeys(false)) {
            String name = config.getString("menu.items." + key + ".name");
            String command = config.getString("menu.items." + key + ".command");
            if (name.equals(itemName)) {
                return command;
            }
        }
        return null;
    }
}