package me.nik.advancedstaff.gui.menus;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.gui.Menu;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.managers.ReportManager;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportDetailGUI extends Menu {

    private final ReportManager.Report report;

    public ReportDetailGUI(PlayerMenu playerMenu, AdvancedStaff plugin, ReportManager.Report report) {
        super(playerMenu, plugin);
        this.report = report;
    }

    @Override
    protected String getMenuName() {
        return ChatUtils.format("&9Report #" + report.getId());
    }

    @Override
    protected int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        switch (item.getType()) {
            case BARRIER:
                p.closeInventory();
                new ReportsGUI(this.playerMenu, this.plugin).open();
                break;

            case LIME_WOOL:
                this.plugin.getReportManager().resolve(report, p);
                p.sendMessage(MsgType.REPORT_RESOLVED.getMessage().replace("%id%", String.valueOf(report.getId())));
                p.closeInventory();
                break;

            case RED_WOOL:
                this.plugin.getReportManager().reject(report, p);
                p.sendMessage(MsgType.REPORT_REJECTED.getMessage().replace("%id%", String.valueOf(report.getId())));
                p.closeInventory();
                break;
        }
    }

    @Override
    protected void setMenuItems() {

        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy HH:mm");

        List<String> infoLore = new ArrayList<>();
        infoLore.add("");
        infoLore.add(ChatUtils.format("&9Reporter: &b" + report.getReporterName()));
        infoLore.add(ChatUtils.format("&9Target: &b" + report.getTargetName()));
        infoLore.add(ChatUtils.format("&9Reason: &b" + report.getReason()));
        infoLore.add(ChatUtils.format("&9Filed: &b" + format.format(new Date(report.getTimestamp()))));
        infoLore.add("");

        inventory.setItem(13, MiscUtils.makeItem(Material.PAPER, "&eReport #" + report.getId(), infoLore));

        inventory.setItem(11, MiscUtils.makeItem(Material.LIME_WOOL, "&aResolve Report", null));
        inventory.setItem(15, MiscUtils.makeItem(Material.RED_WOOL, "&cReject Report", null));

        inventory.setItem(22, MiscUtils.makeItem(Material.BARRIER, "&7Back", null));
    }
}