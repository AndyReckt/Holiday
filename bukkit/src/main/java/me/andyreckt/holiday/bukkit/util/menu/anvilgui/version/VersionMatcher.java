package me.andyreckt.holiday.bukkit.util.menu.anvilgui.version;


import org.bukkit.Bukkit;

/**
 * Matches the me.andyreckt.holiday.server's NMS version to its {@link VersionWrapper}
 *
 * @author Wesley Smith
 * @since 1.2.1
 */
public class VersionMatcher {

    /**
     * Matches the me.andyreckt.holiday.server version to it's {@link VersionWrapper}
     *
     * @return The {@link VersionWrapper} for this me.andyreckt.holiday.server
     * @throws IllegalStateException If the version wrapper failed to be instantiated or is unable to be found
     */
    public VersionWrapper match() {
        final String serverVersion = Bukkit.getServer()
                .getClass()
                .getPackage()
                .getName()
                .split("\\.")[3]
                .substring(1);
        try {
            return (VersionWrapper) Class.forName(getClass().getPackage().getName() + ".Wrapper" + serverVersion)
                    .newInstance();
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new IllegalStateException(
                    "Failed to instantiate version wrapper for version " + serverVersion, exception);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException(
                    "AnvilGUI does not support me.andyreckt.holiday.server version \"" + serverVersion + "\"", exception);
        }
    }
}
