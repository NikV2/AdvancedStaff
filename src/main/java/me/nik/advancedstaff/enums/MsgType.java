package me.nik.advancedstaff.enums;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.utils.ChatUtils;

import java.util.List;

public enum MsgType {
    PREFIX(ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("prefix"))),
    STAFFMODE_ENABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffmode_enabled"))),
    STAFFMODE_DISABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffmode_disabled"))),
    FROZEN(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("frozen"))),
    TELEPORTED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("teleported"))),
    UNFROZEN(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("unfrozen"))),
    FROZEN_ACTIONBAR(ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("frozen_actionbar"))),
    VANISH_ENABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("vanish_enabled"))),
    VANISH_DISABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("vanish_disabled"))),
    VANISH_ACTIONBAR(ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("vanish_actionbar"))),
    UPDATE_FOUND(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("update_found"))),
    UPDATE_NOT_FOUND(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("update_not_found"))),
    NO_PERMISSION(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("no_perm"))),
    RELOADED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("reloaded"))),
    CONSOLE_COMMANDS(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("console_commands")));

    private final String message;

    MsgType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private static String stringFromList(List<String> list) {

        StringBuilder sb = new StringBuilder();

        int size = list.size();

        for (int i = 0; i < size; i++) {

            String str = list.get(i);

            sb.append(str.isEmpty() ? " " : str);

            if (size - 1 != i) sb.append("\n");
        }

        return ChatUtils.format(sb.toString());
    }
}