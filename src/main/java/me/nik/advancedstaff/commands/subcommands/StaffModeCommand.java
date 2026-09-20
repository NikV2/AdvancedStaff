package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffModeCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public StaffModeCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "staffmode";
    }

    @Override
    protected String getDescription() {
        return "Enable/Disable the Staff Mode";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff staffmode";
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
        return Config.Setting.STAFFMODE_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (this.plugin.getStaffModeManager().isInStaffMode(player)) {
            this.plugin.getStaffModeManager().exitStaffMode(player);
            player.sendMessage(MsgType.STAFFMODE_DISABLED.getMessage());
        } else {
            this.plugin.getStaffModeManager().enterStaffMode(player);
            player.sendMessage(MsgType.STAFFMODE_ENABLED.getMessage());
        }
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}