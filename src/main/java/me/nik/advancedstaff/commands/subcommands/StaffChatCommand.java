package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffChatCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public StaffChatCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "staffchat";
    }

    @Override
    protected String getDescription() {
        return "Enable/Disable The Staff Chat";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff staffchat";
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
        return Config.Setting.STAFFCHAT_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        this.plugin.getStaffChatManager().toggleStaffChat(player);
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}