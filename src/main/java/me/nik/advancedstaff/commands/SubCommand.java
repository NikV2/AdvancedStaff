package me.nik.advancedstaff.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A subcommand class that we'll be using to extend on our commands
 */
public abstract class SubCommand {

    /**
     * @return The command's name
     */
    protected abstract String getName();

    /**
     * @return The command's description
     */
    protected abstract String getDescription();

    /**
     * @return The command's syntax
     */
    protected abstract String getSyntax();

    /**
     * @return The permission required in order to run this command
     */
    protected abstract String getPermission();

    /**
     * @return The maximum arguments for this command
     */
    protected abstract int maxArguments();

    /**
     * @return Whether this command can be executed through the console
     */
    protected abstract boolean canConsoleExecute();

    /**
     * @return The alias to be used instead of the whole command
     */
    protected abstract List<String> getAliases();

    /**
     * The method that will be run once the command is executed
     *
     * @param sender Sender
     * @param args   Args
     */
    protected abstract void perform(CommandSender sender, String[] args);

    /**
     * The command arguments returned for this command
     *
     * @param sender Sender
     * @param args   Args
     * @return The sub command arguments
     */
    protected abstract List<String> getSubcommandArguments(CommandSender sender, String[] args);
}