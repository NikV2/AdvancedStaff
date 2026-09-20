package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.gui.menus.PlayerGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InspectCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public InspectCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "inspect";
    }

    @Override
    protected String getDescription() {
        return "Open the Inspection Menu";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff inspect";
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
        return null;
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {
        new PlayerGUI(new PlayerMenu((Player) sender), this.plugin).open();
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}