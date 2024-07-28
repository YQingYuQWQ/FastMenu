package org.YQY.fastMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {
    private FastMenu plugin;
    private MenuManager menuManager;

    public MenuListener(FastMenu plugin, MenuManager menuManager) {
        this.plugin = plugin;
        this.menuManager = menuManager;
    }
    // 处理玩家右键点击时打开菜单的事件
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getMaterial() == Material.CLOCK && event.getAction() == Action.RIGHT_CLICK_AIR) {
            event.getPlayer().openInventory(plugin.getMenu("main_menu"));  // 打开已经创建的菜单
            plugin.currentOpenedMenuName.put(event.getPlayer(), "main_menu");
        }
    }

    // 处理玩家点击菜单项的事件
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 判断是否点击的是菜单
        if (event.getClickedInventory() == null || !plugin.menuList.containsValue(event.getClickedInventory())) {
            return;
        }

        event.setCancelled(true);  // 取消事件，防止玩家将物品拖出菜单

        ItemStack clickedItem = event.getCurrentItem();  // 5获取点击的物品
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();
        menuManager.DoCommandByItemName((Player)event.getWhoClicked(), itemName);
    }
}