package me.nik.advancedstaff.commands.subcommands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.SubCommand;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand extends SubCommand {

    private final AdvancedStaff plugin;

    public FreezeCommand(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "freeze";
    }

    @Override
    protected String getDescription() {
        return "Freeze/Unfreeze a player";
    }

    @Override
    protected String getSyntax() {
        return "/advancedstaff freeze <player>";
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
        return true;
    }

    @Override
    protected List<String> getAliases() {
        return Config.Setting.FREEZE_ALIASES.getStringList();
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) return;

        if (this.plugin.getFreezeManager().isFrozen(target)) {
            this.plugin.getFreezeManager().unFreeze(target);
            sender.sendMessage(MsgType.UNFROZEN.getMessage().replace("%player%", target.getName()));
        } else {
            this.plugin.getFreezeManager().freeze(target);
            sender.sendMessage(MsgType.FROZEN.getMessage().replace("%player%", target.getName()));
        }
    }

    @Override
    protected List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}