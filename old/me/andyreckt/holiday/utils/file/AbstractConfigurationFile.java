package me.andyreckt.holiday.utils.file;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class AbstractConfigurationFile {
  public static final String FILE_EXTENSION = ".yml";
  
  private final JavaPlugin plugin;
  
  private final String name;
  
  public JavaPlugin getPlugin() {
    return this.plugin;
  }
  
  public String getName() {
    return this.name;
  }
  
  public AbstractConfigurationFile(JavaPlugin plugin, String name) {
    this.plugin = plugin;
    this.name = name;
  }
  
  public abstract String getString(String paramString);
  
  public abstract String getStringOrDefault(String paramString1, String paramString2);
  
  public abstract int getInteger(String paramString);
  
  public abstract double getDouble(String paramString);
  
  public abstract Object get(String paramString);
  
  public abstract List<String> getStringList(String paramString);
}