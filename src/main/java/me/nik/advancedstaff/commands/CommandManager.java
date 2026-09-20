package me.nik.advancedstaff.commands;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.commands.subcommands.FreezeCommand;
import me.nik.advancedstaff.commands.subcommands.InspectCommand;
import me.nik.advancedstaff.commands.subcommands.InventoryCommand;
import me.nik.advancedstaff.commands.subcommands.MenuCommand;
import me.nik.advancedstaff.commands.subcommands.ReloadCommand;
import me.nik.advancedstaff.commands.subcommands.StaffModeCommand;
import me.nik.advancedstaff.commands.subcommands.VanishCommand;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements TabExecutor, Listener {

    private static final String INFO_MESSAGE = MsgType.PREFIX.getMessage() + ChatUtils.format(
            "&fThis server is running &b"
                    + AdvancedStaff.getInstance().getDescription().getName()
                    + " &fversion &bv" + AdvancedStaff.getInstance().getDescription().getVersion()
                    + " &fby Nik"
    );

    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandManager(AdvancedStaff plugin) {
        this.subCommands.add(new ReloadCommand(plugin));
        this.subCommands.add(new MenuCommand(plugin));
        this.subCommands.add(new VanishCommand(plugin));
        this.subCommands.add(new FreezeCommand(plugin));
        this.subCommands.add(new StaffModeCommand(plugin));
        this.subCommands.add(new InventoryCommand(plugin));
        this.subCommands.add(new InspectCommand(plugin));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAlias(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        String[] args = e.getMessage().replace("/", "").split(" ");

        for (SubCommand command : this.subCommands) {

            if (command.getAliases() == null) continue;

            for (String alias : command.getAliases()) {
                if (args[0].equalsIgnoreCase(alias)) {

                    e.setCancelled(true);

                    if (!p.hasPermission(command.getPermission())) {
                        p.sendMessage(MsgType.NO_PERMISSION.getMessage());
                        return;
                    }

                    if (args.length < command.maxArguments()) {
                        helpMessage(p);
                        return;
                    }

                    command.perform(p, args);
                }
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length > 0) {

            for (SubCommand subCommand : this.subCommands) {

                if (args[0].equalsIgnoreCase(subCommand.getName())) {

                    if (!subCommand.canConsoleExecute() && sender instanceof ConsoleCommandSender) {

                        sender.sendMessage(MsgType.CONSOLE_COMMANDS.getMessage());

                        return true;
                    }

                    if (!sender.hasPermission(subCommand.getPermission())) {

                        sender.sendMessage(MsgType.NO_PERMISSION.getMessage());

                        return true;
                    }

                    if (args.length < subCommand.maxArguments()) {

                        helpMessage(sender);

                        return true;
                    }

                    subCommand.perform(sender, args);

                    return true;
                }

                if (args[0].equalsIgnoreCase("help")) {

                    helpMessage(sender);

                    return true;
                }
            }

        } else {

            sender.sendMessage(INFO_MESSAGE);

            return true;
        }

        helpMessage(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (args.length < 2) {

            return this.subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());

        } else {

            for (SubCommand subCommand : this.subCommands) {

                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    return subCommand.getSubcommandArguments(sender, args);
                }
            }
        }

        return null;
    }

    private void helpMessage(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(MsgType.PREFIX.getMessage() + ChatColor.WHITE + "Available Commands");
        sender.sendMessage("");

        this.subCommands.stream()
                .filter(subCommand -> sender.hasPermission(subCommand.getPermission()))
                .forEach(subCommand ->
                        sender.sendMessage(ChatColor.GOLD + subCommand.getSyntax() + ChatColor.DARK_GRAY + " - "
                                + ChatColor.GRAY + subCommand.getDescription()));

        sender.sendMessage("");
    }
}