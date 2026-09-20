package me.nik.advancedstaff.gui.menus;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.gui.Menu;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MainGUI extends Menu {
    public MainGUI(PlayerMenu playerMenu, AdvancedStaff plugin) {
        super(playerMenu, plugin);
    }

    @Override
    protected String getMenuName() {
        return ChatUtils.format("&9AdvancedStaff Menu");
    }

    @Override
    protected int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        final Player p = (Player) e.getWhoClicked();

        switch (e.getSlot()) {
            case 31:
                p.closeInventory();
                break;
            case 11:
                p.closeInventory();
                new PlayerGUI(this.playerMenu, this.plugin).open();
                break;
            case 13:
                p.closeInventory();
                this.plugin.getConfiguration().setup();
                this.plugin.getLang().reload();
                p.sendMessage(MsgType.RELOADED.getMessage());
                break;
            case 15:
                p.closeInventory();

                //StaffMode
                break;
        }
    }

    @Override
    protected void setMenuItems() {

        ItemStack close = MiscUtils.makeItem(Material.BARRIER, "&cExit", null);
        inventory.setItem(31, close);

        ItemStack players = MiscUtils.makeItem(Material.PLAYER_HEAD, "&6Players", null);
        inventory.setItem(11, players);

        ItemStack reload = MiscUtils.makeItem(Material.REDSTONE_BLOCK, "&6Reload", null);
        inventory.setItem(13, reload);

        ItemStack staffMode = MiscUtils.makeItem(Material.ENDER_EYE, "&6Staff Mode", null);
        inventory.setItem(15, staffMode);
    }
}