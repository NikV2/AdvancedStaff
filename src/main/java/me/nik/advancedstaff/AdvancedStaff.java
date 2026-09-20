package me.nik.advancedstaff;

import me.nik.advancedstaff.commands.CommandManager;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.files.Lang;
import me.nik.advancedstaff.listeners.FreezeListener;
import me.nik.advancedstaff.listeners.GuiListener;
import me.nik.advancedstaff.listeners.InventoryListener;
import me.nik.advancedstaff.listeners.StaffModeListener;
import me.nik.advancedstaff.listeners.VanishListener;
import me.nik.advancedstaff.managers.FreezeManager;
import me.nik.advancedstaff.managers.InventoryManager;
import me.nik.advancedstaff.managers.StaffModeManager;
import me.nik.advancedstaff.managers.VanishManager;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.MillisTest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class AdvancedStaff extends JavaPlugin {

    private final String[] STARTUP_MESSAGE = new String[]{
            " ",
            ChatColor.AQUA + "Advanced Staff v" + this.getDescription().getVersion(),
            " ",
            ChatColor.WHITE + "  Author: Nik",
            " "
    };

    private final Config config = new Config(this);
    private final Lang lang = new Lang();

    private VanishManager vanishManager;
    private FreezeManager freezeManager;
    private StaffModeManager staffModeManager;
    private InventoryManager inventoryManager;

    private static AdvancedStaff instance;

    @Override
    public void onEnable() {

        this.getServer().getConsoleSender().sendMessage(STARTUP_MESSAGE);

        instance = this;

        //Shared millis test
        MillisTest test = new MillisTest();

        //Load Files
        test.reset();
        this.config.setup();
        this.lang.setup(this);
        this.lang.addDefaults();
        this.lang.get().options().copyDefaults(true);
        this.lang.save();
        ChatUtils.log("Files have been loaded in " + test.getMillis() + "ms");

        //Managers
        test.reset();
        this.vanishManager = new VanishManager(this);
        this.vanishManager.initialize();

        this.freezeManager = new FreezeManager(this);
        this.freezeManager.initialize();

        this.staffModeManager = new StaffModeManager(this);
        this.staffModeManager.initialize();

        this.inventoryManager = new InventoryManager();
        this.inventoryManager.initialize();
        ChatUtils.log("Managers have been loaded in " + test.getMillis() + "ms");

        //Listeners
        test.reset();
        Arrays.asList(
                new GuiListener(),
                new VanishListener(this),
                new FreezeListener(this),
                new StaffModeListener(this),
                new InventoryListener(this),
                new CommandManager(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        ChatUtils.log("Managers have been loaded in " + test.getMillis() + "ms");

        //Command
        test.reset();
        getCommand("advancedstaff").setExecutor(new CommandManager(this));
        ChatUtils.log("Commands have been loaded in " + test.getMillis() + "ms");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        Bukkit.getScheduler().cancelTasks(this);

        this.vanishManager.shutdown();
        this.freezeManager.shutdown();
        this.staffModeManager.shutdown();
        this.inventoryManager.shutdown();

        this.config.reset();
        this.lang.reload();
        this.lang.save();

        instance = null;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public StaffModeManager getStaffModeManager() {
        return staffModeManager;
    }

    public FreezeManager getFreezeManager() {
        return freezeManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public Config getConfiguration() {
        return config;
    }

    public Lang getLang() {
        return lang;
    }

    public static AdvancedStaff getInstance() {
        return instance;
    }
}