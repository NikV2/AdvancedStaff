package me.nik.advancedstaff.gui.menus;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.gui.PaginatedMenu;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerGUI extends PaginatedMenu {

    public PlayerGUI(PlayerMenu playerMenu, AdvancedStaff plugin) {
        super(playerMenu, plugin);
    }

    @Override
    protected String getMenuName() {
        return ChatUtils.format("&9Online Players");
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
                break;
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
        }

        if (type.name().contains("HEAD")) {

            p.closeInventory();

            String playerName = ChatUtils.stripColorCodes(item.getItemMeta().getDisplayName());

            Player target = Bukkit.getPlayer(playerName);

            if (target != null) {

                this.plugin.getVanishManager().vanish(p);

                //Do it 5 ticks later to avoid instant visibility issues
                TaskUtils.taskLater(() -> {
                    p.teleport(target.getLocation());
                    target.addPassenger(p);
                }, 5L);
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

                String playerName = player.getName();

                String ip = "Unknown";
                if (player.getAddress() != null && player.getAddress().getAddress() != null) {
                    ip = player.getAddress().getAddress().getHostAddress();
                }

                String uuid = player.getUniqueId().toString();

                String world = player.getWorld().getName();

                String location = String.format(
                        "%d, %d, %d",
                        player.getLocation().getBlockX(),
                        player.getLocation().getBlockY(),
                        player.getLocation().getBlockZ()
                );

                String gamemode = player.getGameMode().name();

                String health = String.format(
                        "%.1f",
                        player.getHealth()
                );

                int ping = 0;

                try {
                    ping = player.getPing();
                } catch (NoSuchMethodError ignored) {
                }

                List<String> lore = new ArrayList<>();

                lore.add(ChatUtils.format("&8&m--------------------"));
                lore.add(ChatUtils.format("&9&lPlayer Information"));
                lore.add("");
                lore.add(ChatUtils.format("&9IP: &b" + ip));
                lore.add(ChatUtils.format("&9UUID: &b" + uuid));
                lore.add(ChatUtils.format("&9Ping: &b" + ping + "ms"));
                lore.add("");
                lore.add(ChatUtils.format("&9World: &b" + world));
                lore.add(ChatUtils.format("&9Location: &b" + location));
                lore.add(ChatUtils.format("&9Gamemode: &b" + gamemode));
                lore.add(ChatUtils.format("&9Health: &b" + health + "&c❤"));
                lore.add(ChatUtils.format("&9OP: &b" + (player.isOp() ? "Yes" : "No")));
                lore.add("");
                lore.add(ChatUtils.format("&8&m--------------------"));
                lore.add("");
                lore.add(ChatUtils.format("&b&lClick to inspect"));
                lore.add("");

                items.add(makeHead(
                        playerName,
                        ChatUtils.format("&b" + playerName),
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