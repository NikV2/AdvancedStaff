package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.gui.menus.MainGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public MenuCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "menu";
    }

    @Override
    protected String getDescription() {
        return "Open the AdvancedStaff Menu";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff menu";
    }

    @Override
    protected String getPermission() {
        return Permissions.MENU.getPermission();
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
        new MainGUI(new PlayerMenu((Player) sender), this.plugin).open();
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}