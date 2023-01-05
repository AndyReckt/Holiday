package me.andyreckt.holiday.staff.commands;

import me.andyreckt.holiday.staff.Staff;
import me.andyreckt.holiday.staff.util.files.SPerms;
import me.andyreckt.holiday.staff.util.sunset.annotations.Command;
import org.bukkit.entity.Player;

public class Commands {

    @Command(names = {"staffmode", "staff", "modmode", "mod"}, permission = SPerms.STAFF, description = "Toggle staff mode.")
    public void staff(Player player) {
        Staff.getInstance().getStaffManager().toggleStaffMode(player);
    }

    @Command(names = {"vanish", "v"}, permission = SPerms.STAFF, description = "Toggle vanish.")
    public void vanish(Player player) {
        Staff.getInstance().getStaffManager().vanish(player);
    }

    //TODO: freeze

}
