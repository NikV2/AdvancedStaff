package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public ReloadCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "reload";
    }

    @Override
    protected String getDescription() {
        return "Reload the plugin";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff reload";
    }

    @Override
    protected String getPermission() {
        return Permissions.RELOAD.getPermission();
    }

    @Override
    protected int maxArguments() {
        return 1;
    }

    @Override
    protected boolean canConsoleExecute() {
        return true;
    }

    @Override
    protected List<String> getAliases() {
        return null;
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {
        this.plugin.getConfiguration().setup();
        this.plugin.getLang().reload();
        sender.sendMessage(MsgType.RELOADED.getMessage());
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}