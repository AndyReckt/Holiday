package me.andyreckt.holiday.utils.inventory.crafter;

import org.bukkit.scheduler.*;
import java.util.function.*;

public class TaskUpdate extends BukkitRunnable
{
     Consumer<TaskUpdate> consumer;
    
    public TaskUpdate(final Consumer<TaskUpdate> consumer) {
        this.consumer = consumer;
    }
    
    public void run() {
        this.consumer.accept(this);
    }
}
