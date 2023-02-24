package me.andyreckt.holiday.staff;

import lombok.Getter;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.staff.commands.Commands;
import me.andyreckt.holiday.staff.server.FreezeListeners;
import me.andyreckt.holiday.staff.server.FreezeMessageTask;
import me.andyreckt.holiday.staff.server.ModItemsListeners;
import me.andyreckt.holiday.staff.server.ModListeners;
import me.andyreckt.holiday.staff.user.StaffManager;
import me.andyreckt.holiday.staff.util.files.SLocale;
import me.andyreckt.holiday.staff.util.files.SPerms;
import me.andyreckt.holiday.staff.util.sunset.Sunset;
import me.andyreckt.holiday.staff.util.sunset.parameter.custom.ProfileParameterType;
import me.andyreckt.holiday.staff.util.sunset.parameter.custom.RankParameterType;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Staff extends JavaPlugin {

    @Getter
    private static Staff instance;

    private Sunset commandManager;
    private StaffManager staffManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            this.setupConfigFiles();
            this.setupCommands();
            this.staffManager = new StaffManager(this);
            this.setupListeners();
            new FreezeMessageTask();

        } catch (Exception ex) {
            Logger.error("An error occurred while enabling the staff addon. Showing stacktrace:");
            ex.printStackTrace();
            Logger.error("Stopping the server...");
        }

    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new ModListeners(), this);
        getServer().getPluginManager().registerEvents(new FreezeListeners(), this);
        getServer().getPluginManager().registerEvents(new ModItemsListeners(), this);
    }


    private void setupCommands() {
        this.commandManager = new Sunset(this);
        this.commandManager.registerType(new ProfileParameterType(), Profile.class);
        this.commandManager.registerType(new RankParameterType(), IRank.class);
        this.commandManager.registerCommands(new Commands());
    }

    private void setupConfigFiles() {
        SLocale.init(this);
        SPerms.init(this);
    }
}

