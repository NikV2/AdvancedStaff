package me.nik.advancedstaff.gui.menus;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.gui.PaginatedMenu;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.managers.ReportManager;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.MiscUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportsGUI extends PaginatedMenu {

    public ReportsGUI(PlayerMenu playerMenu, AdvancedStaff plugin) {
        super(playerMenu, plugin);
    }

    @Override
    protected String getMenuName() {
        return ChatUtils.format("&9Active Reports");
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

        if (type != Material.PAPER) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore() || meta.getLore().isEmpty()) return;

        String idLine = ChatUtils.stripColorCodes(meta.getLore().get(meta.getLore().size() - 1));
        String digitsOnly = idLine.replaceAll("[^0-9]", "");

        if (digitsOnly.isEmpty()) return;

        ReportManager.Report report = this.plugin.getReportManager().getReport(Integer.parseInt(digitsOnly));

        if (report != null) {
            p.closeInventory();
            new ReportDetailGUI(this.playerMenu, this.plugin, report).open();
        }
    }

    @Override
    protected void setMenuItems() {

        addMenuBorder();

        List<ReportManager.Report> activeReports = this.plugin.getReportManager().getActiveReports();

        if (activeReports.isEmpty()) return;

        SimpleDateFormat format = new SimpleDateFormat("MMM d, HH:mm");

        List<ItemStack> items = new ArrayList<>();

        for (ReportManager.Report report : activeReports) {

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatUtils.format("&9Reporter: &b" + report.getReporterName()));
            lore.add(ChatUtils.format("&9Reason: &b" + report.getReason()));
            lore.add(ChatUtils.format("&9Filed: &b" + format.format(new Date(report.getTimestamp()))));
            lore.add("");
            lore.add(ChatUtils.format("&b&lClick to view"));
            lore.add("");
            lore.add(ChatUtils.format("&8ID: " + report.getId()));

            items.add(MiscUtils.makeItem(Material.PAPER, "&e" + report.getTargetName(), lore));
        }

        for (int i = 0; i < super.maxItemsPerPage; i++) {
            index = super.maxItemsPerPage * page + i;
            if (index >= items.size()) break;
            if (items.get(index) != null) {
                inventory.addItem(items.get(index));
            }
        }
    }
}