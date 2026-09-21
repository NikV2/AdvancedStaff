package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.gui.PlayerMenu;
import me.nik.advancedstaff.gui.menus.ReportPlayerGUI;
import me.nik.advancedstaff.gui.menus.ReportReasonGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ReportCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public ReportCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "report";
    }

    @Override
    protected String getDescription() {
        return "Report a player to staff";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff report";
    }

    @Override
    protected String getPermission() {
        return Permissions.REPORT.getPermission();
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
        return Config.Setting.REPORT_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (args.length < 2) {
            new ReportPlayerGUI(new PlayerMenu(player), this.plugin).open();
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(MsgType.REPORT_TARGET_OFFLINE.getMessage());
            return;
        }

        if (args.length < 3) {
            new ReportReasonGUI(new PlayerMenu(player), this.plugin, target).open();
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        this.plugin.getReportManager().handleSubmit(player, target, reason);
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}