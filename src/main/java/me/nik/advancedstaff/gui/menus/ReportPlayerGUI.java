package me.nik.advancedstaff.gui.menus;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.gui.PaginatedMenu;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ReportPlayerGUI extends PaginatedMenu {

    public ReportPlayerGUI(PlayerMenu playerMenu, AdvancedStaff plugin) {
        super(playerMenu, plugin);
    }

    @Override
    protected String getMenuName() {
        return ChatUtils.format("&9Report a Player");
    }

    @Override
    protected int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();
        if (!item.getItemMeta().hasDisplayName()) return;

        Material type = item.getType();

        switch (type) {
            case BARRIER:
                p.closeInventory();
                return;
            case BOOK:
                switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                    case "Previous Page":
                        if (page != 0) {
                            page = page - 1;
                            super.open();
                        }
                        break;
                    case "Next Page":
                        page = page + 1;
                        super.open();
                        break;
                }
                return;
        }

        if (type.name().contains("HEAD")) {
            String playerName = ChatUtils.stripColorCodes(item.getItemMeta().getDisplayName());

            Player target = Bukkit.getPlayer(playerName);

            if (target != null) {
                p.closeInventory();
                new ReportReasonGUI(this.playerMenu, this.plugin, target).open();
            }
        }
    }

    @Override
    protected void setMenuItems() {

        addMenuBorder();

        inventory.setMaxStackSize(1);

        List<ItemStack> items = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player != null && !player.getUniqueId().equals(playerMenu.getOwner().getUniqueId())) {

                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatUtils.format("&b&lClick to report"));
                lore.add("");

                items.add(makeHead(
                        player.getName(),
                        ChatUtils.format("&b" + player.getName()),
                        lore
                ));
            }
        });

        if (items.isEmpty()) return;

        for (int i = 0; i < super.maxItemsPerPage; i++) {
            index = super.maxItemsPerPage * page + i;
            if (index >= items.size()) break;
            if (items.get(index) != null) {
                inventory.addItem(items.get(index));
            }
        }
    }
}