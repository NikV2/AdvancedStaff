package me.nik.advancedstaff.gui.menus;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.gui.Menu;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ReportReasonGUI extends Menu {

    private final Player target;
    private final List<String> reasons;

    public ReportReasonGUI(PlayerMenu playerMenu, AdvancedStaff plugin, Player target) {
        super(playerMenu, plugin);
        this.target = target;
        this.reasons = Config.Setting.REPORT_REASONS.getStringList();
    }

    @Override
    protected String getMenuName() {
        return ChatUtils.format("&9Report: " + target.getName());
    }

    @Override
    protected int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        if (item.getType() == Material.BARRIER) {
            p.closeInventory();
            return;
        }

        if (item.getType() == Material.WRITABLE_BOOK) {
            p.closeInventory();
            this.plugin.getReportManager().awaitCustomReason(p, target);
            p.sendMessage(MsgType.REPORT_CUSTOM_PROMPT.getMessage());
            return;
        }

        String reason = ChatUtils.stripColorCodes(item.getItemMeta().getDisplayName());

        p.closeInventory();

        this.plugin.getReportManager().handleSubmit(p, target, reason);
    }

    @Override
    protected void setMenuItems() {

        int slot = 0;

        for (String reason : reasons) {
            if (slot >= 18) break;

            inventory.setItem(slot, MiscUtils.makeItem(Material.PAPER, "&e" + reason, null));
            slot++;
        }

        if (Config.Setting.REPORT_ALLOW_CUSTOM_REASON.getBoolean()) {

            List<String> customLore = new ArrayList<>();
            customLore.add("");
            customLore.add(ChatUtils.format("&7Type your reason in chat"));
            customLore.add(ChatUtils.format("&7after clicking, or type"));
            customLore.add(ChatUtils.format("&7&ocancel&7 to back out"));
            customLore.add("");

            inventory.setItem(22, MiscUtils.makeItem(Material.WRITABLE_BOOK, "&bCustom Reason", customLore));
        }

        inventory.setItem(26, MiscUtils.makeItem(Material.BARRIER, "&cCancel", null));
    }
}
