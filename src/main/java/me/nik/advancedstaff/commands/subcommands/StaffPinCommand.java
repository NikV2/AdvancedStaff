package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StaffPinCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public StaffPinCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "staffpin";
    }

    @Override
    protected String getDescription() {
        return "Register or remove your staff PIN";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff staffpin <remove>";
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
        return Config.Setting.STAFFPIN_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgType.CONSOLE_COMMANDS.getMessage());
            return;
        }

        Player player = (Player) sender;

        if (args.length > 1 && args[1].equalsIgnoreCase("remove")) {
            if (!plugin.getStaffPinManager().removePin(player)) {
                player.sendMessage(MsgType.STAFFPIN_NOT_REGISTERED.getMessage());
                return;
            }

            player.sendMessage(MsgType.STAFFPIN_REMOVED.getMessage());
            return;
        }

        if (plugin.getStaffPinManager().hasPin(player)) {
            player.sendMessage(MsgType.STAFFPIN_ALREADY_REGISTERED.getMessage());
            return;
        }

        plugin.getStaffPinManager().beginRegistration(player);
        player.sendMessage(MsgType.STAFFPIN_REGISTRATION_PROMPT.getMessage());
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.singletonList("remove");
    }
}