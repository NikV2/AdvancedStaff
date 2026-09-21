package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.gui.menus.ReportsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReportsCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public ReportsCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "reports";
    }

    @Override
    protected String getDescription() {
        return "View and manage active player reports";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff reports";
    }

    @Override
    protected String getPermission() {
        return Permissions.ADMIN.getPermission();
    }

    @Override
    protected int maxArguments() {
        return 1;
    }

    @Override
    protected boolean canConsoleExecute() {
        return false;
    }

    @Override
    protected List<String> getAliases() {
        return Config.Setting.REPORTS_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {
        new ReportsGUI(new PlayerMenu((Player) sender), this.plugin).open();
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}