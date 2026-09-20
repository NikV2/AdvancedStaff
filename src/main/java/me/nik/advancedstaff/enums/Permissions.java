package me.nik.advancedstaff.enums;

public enum Permissions {
    ADMIN("advancedstaff.admin"),
    RELOAD("advancedstaff.reload"),
    MENU("advancedstaff.menu");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}