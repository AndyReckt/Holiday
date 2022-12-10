package me.andyreckt.holiday.utils.file.language;

import me.andyreckt.holiday.utils.file.AbstractConfigurationFile;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class LanguageConfigurationFile extends AbstractConfigurationFile {
  private static final LanguageConfigurationFileLocale DEFAULT_LOCALE = LanguageConfigurationFileLocale.ENGLISH;
  
  private final Map<LanguageConfigurationFileLocale, YamlConfiguration> configurations;
  
  public Map<LanguageConfigurationFileLocale, YamlConfiguration> getConfigurations() {
    return this.configurations;
  }
  
  public LanguageConfigurationFile(JavaPlugin plugin, String name, boolean overwrite) {
    super(plugin, name);
    this.configurations = new HashMap<>();
    for (LanguageConfigurationFileLocale locale : LanguageConfigurationFileLocale.values()) {
      File file = new File(plugin.getDataFolder(), name + "_" + locale.getAbbreviation() + ".yml");
      String path = name + "_" + locale.getAbbreviation() + ".yml";
      if (plugin.getResource(path) != null) {
        plugin.saveResource(path, overwrite);
        this.configurations.put(locale, YamlConfiguration.loadConfiguration(file));
      } 
    } 
  }
  
  public LanguageConfigurationFile(JavaPlugin plugin, String name) {
    this(plugin, name, false);
  }
  
  public List<String> replace(List<String> list, int position, Object argument) {
    List<String> toReturn = new ArrayList<>();
    for (String string : list)
      toReturn.add(string.replace("{" + position + "}", argument.toString())); 
    return toReturn;
  }
  
  public List<String> replace(List<String> list, int position, Object... arguments) {
    return replace(list, 0, position, arguments);
  }
  
  public List<String> replace(List<String> list, int index, int position, Object... arguments) {
    List<String> toReturn = new ArrayList<>();
    for (String string : list) {
      for (int i = 0; i < arguments.length; i++)
        toReturn.add(string.replace("{" + position + "}", arguments[index + i].toString())); 
    } 
    return toReturn;
  }
  
  public List<String> getStringListWithArgumentsOrRemove(String path, LanguageConfigurationFileLocale locale, Object... arguments) {
    List<String> toReturn = new ArrayList<>();
    label27: for (String string : getStringList(path, locale)) {
      for (int i = 0; i < arguments.length; i++) {
        if (string.contains("{" + i + "}")) {
          Object object = arguments[i];
          if (object != null) {
            if (object instanceof List) {
              for (Object obj : arguments) {
                if (obj instanceof String)
                  toReturn.add((String)obj); 
              } 
              continue label27;
            } 
            string = string.replace("{" + i + "}", object.toString());
          } else {
            continue label27;
          } 
        } 
      } 
      toReturn.add(string);
    } 
    return toReturn;
  }
  
  public int indexOf(List<String> list, int position) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).contains("{" + position + "}"))
        return i; 
    } 
    return -1;
  }
  
  public String getString(String path, LanguageConfigurationFileLocale locale) {
    if (!this.configurations.containsKey(locale))
      return (locale == DEFAULT_LOCALE) ? null : getString(path, DEFAULT_LOCALE); 
    YamlConfiguration configuration = this.configurations.get(locale);
    if (configuration.contains(path))
      return ChatColor.translateAlternateColorCodes('&', configuration.getString(path)); 
    return null;
  }
  
  public String getString(String path, LanguageConfigurationFileLocale locale, Object... arguments) {
    String toReturn = getString(path, locale);
    if (toReturn != null) {
      for (int i = 0; i < arguments.length; i++)
        toReturn = toReturn.replace("{" + i + "}", arguments[i].toString()); 
      return toReturn;
    } 
    return null;
  }
  
  public String getString(String path) {
    return getString(path, DEFAULT_LOCALE);
  }
  
  public String getStringOrDefault(String path, String or, LanguageConfigurationFileLocale locale) {
    String toReturn = getString(path, locale);
    if (toReturn == null)
      return or; 
    return toReturn;
  }
  
  public String getStringOrDefault(String path, String or) {
    return getStringOrDefault(path, or, DEFAULT_LOCALE);
  }
  
  public int getInteger(String path) {
    throw new UnsupportedOperationException("");
  }
  
  @Deprecated
  public double getDouble(String path) {
    throw new UnsupportedOperationException("");
  }
  
  @Deprecated
  public Object get(String path) {
    throw new UnsupportedOperationException("");
  }
  
  public List<String> getStringList(String path, LanguageConfigurationFileLocale locale, Object... arguments) {
    List<String> toReturn = new ArrayList<>();
    label25: for (String line : getStringList(path, locale)) {
      for (int i = 0; i < arguments.length; i++) {
        Object object = arguments[i];
        if (object instanceof List && line.contains("{" + i + "}")) {
          for (Object obj : arguments) {
            if (obj instanceof String)
              toReturn.add(line.replace("{" + i + "}", "") + obj); 
          } 
          continue label25;
        } 
        line = line.replace("{" + i + "}", arguments[i].toString());
      } 
      toReturn.add(line);
    } 
    return toReturn;
  }
  
  public List<String> getStringList(String path, LanguageConfigurationFileLocale locale) {
    if (!this.configurations.containsKey(locale))
      return (locale == DEFAULT_LOCALE) ? null : getStringList(path, DEFAULT_LOCALE); 
    YamlConfiguration configuration = this.configurations.get(locale);
    if (configuration.contains(path)) {
      List<String> toReturn = new ArrayList<>();
      for (String string : configuration.getStringList(path))
        toReturn.add(ChatColor.translateAlternateColorCodes('&', string)); 
      return toReturn;
    } 
    return Collections.emptyList();
  }
  
  public List<String> getStringList(String path) {
    return getStringList(path, DEFAULT_LOCALE);
  }
}