package me.nik.advancedstaff.enums;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.utils.ChatUtils;

import java.util.List;

public enum MsgType {
    PREFIX(ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("prefix"))),
    STAFFMODE_ENABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffmode_enabled"))),
    STAFFMODE_DISABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffmode_disabled"))),
    STAFFCHAT_ENABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffchat_enabled"))),
    STAFFCHAT_DISABLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffchat_disabled"))),
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
    CONSOLE_COMMANDS(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("console_commands"))),
    STAFFPIN_REGISTERED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_registered"))),
    STAFFPIN_UPDATED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_updated"))),
    STAFFPIN_REMOVED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_removed"))),
    STAFFPIN_ALREADY_REGISTERED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_already_registered"))),
    STAFFPIN_NOT_REGISTERED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_not_registered"))),
    STAFFPIN_INVALID(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_invalid"))),
    STAFFPIN_NUMERIC(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_numeric"))),
    STAFFPIN_PROMPT(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_prompt"))),
    STAFFPIN_REGISTRATION_PROMPT(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_registration_prompt"))),
    STAFFPIN_UNLOCKED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_unlocked"))),
    STAFFPIN_INCORRECT(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_incorrect"))),
    STAFFPIN_LOCKED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("staffpin_locked"))),
    REPORT_SUBMITTED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_submitted"))),
    REPORT_CANNOT_SELF(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_cannot_self"))),
    REPORT_COOLDOWN(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_cooldown"))),
    REPORT_MAX_ACTIVE(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_max_active"))),
    REPORT_DUPLICATE(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_duplicate"))),
    REPORT_TARGET_OFFLINE(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_target_offline"))),
    REPORT_CUSTOM_PROMPT(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_custom_prompt"))),
    REPORT_CANCELLED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_cancelled"))),
    REPORT_STAFF_NOTIFY(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_staff_notify"))),
    REPORT_RESOLVED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_resolved"))),
    REPORT_REJECTED(PREFIX.getMessage() + ChatUtils.format(AdvancedStaff.getInstance().getLang().get().getString("report_rejected")));

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