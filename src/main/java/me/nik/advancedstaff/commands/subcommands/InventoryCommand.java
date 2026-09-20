package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InventoryCommand extends SubCommand {
    private final AdvancedStaff plugin;

    public InventoryCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "inventory";
    }

    @Override
    protected String getDescription() {
        return "View a player's inventory";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff inventory <player>";
    }

    @Override
    protected String getPermission() {
        return Permissions.ADMIN.getPermission();
    }

    @Override
    protected int maxArguments() {
        return 2;
    }

    @Override
    protected boolean canConsoleExecute() {
        return false;
    }

    @Override
    protected List<String> getAliases() {
        return Config.Setting.INVENTORY_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) return;

        this.plugin.getInventoryManager().openPlayerInventory((Player) sender, target);
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}