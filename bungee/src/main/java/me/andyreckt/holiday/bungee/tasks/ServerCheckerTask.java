package me.andyreckt.holiday.bungee.tasks;

import io.netty.channel.unix.DomainSocketAddress;
import lombok.SneakyThrows;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.bungee.Bungee;
import me.andyreckt.holiday.bungee.util.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * This class takes a lot of code from <a href="https://github.com/tavonkelly/BungeeServerManager/">@tavonkelly's BungeeServerManager</a>.
 * Thanks to <a href="https://github.com/tavonkelly">him</a>.
 */
public class ServerCheckerTask {
    private static File file;
    private static Configuration bungeeConfig;

    public ServerCheckerTask() {
        if (!Locale.SERVER_AUTO_ADD.getBoolean()) return;
        setupConfig();
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), () -> {
            Bungee.getInstance().getApi().getServers().values().stream().filter(IServer::isOnline).forEach(server -> {
                if (ProxyServer.getInstance().getServers().get(server.getServerId()) == null) {
                    CreateInstance.createServerInstance(server);
                }
            });
            Bungee.getInstance().getApi().getServers().values().stream().filter(server -> !server.isOnline()).forEach(server -> {
                if (ProxyServer.getInstance().getServers().get(server.getServerId()) != null) {
                    ProxyServer.getInstance().getServers().remove(server.getServerId());
                }
            });
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void setupConfig() {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            file = new File(ProxyServer.getInstance().getPluginsFolder().getParentFile(), "config.yml");

            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);

            bungeeConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(isr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ignored) {}
        }
    }

    private static class CreateInstance {
        private static void createServerInstance(IServer server) {
            SocketAddress socketAddress = new InetSocketAddress(server.getAddress(), server.getPort());
            ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(server.getServerId(), socketAddress, "", false);
            ProxyServer.getInstance().getServers().put(serverInfo.getName(), serverInfo);
            addToConfig(serverInfo);
        }

        private static String socketAddressToString(SocketAddress socketAddress) {
            return socketAddressToString(socketAddress, true);
        }

        private static String socketAddressToString(SocketAddress socketAddress, boolean appendPort) {
            String addressString;

            if (socketAddress instanceof DomainSocketAddress) {
                addressString = "unix:" + ((DomainSocketAddress) socketAddress).path();
            } else if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress inetAddress = (InetSocketAddress) socketAddress;

                addressString = inetAddress.getHostString();

                if (appendPort) {
                    addressString += ":" + inetAddress.getPort();
                }
            } else {
                addressString = socketAddress.toString();
            }

            return addressString;
        }

        private static void addToConfig(ServerInfo serverInfo) {
            bungeeConfig.set("servers." + serverInfo.getName() + ".motd", serverInfo.getMotd().replace(ChatColor.COLOR_CHAR, '&'));
            bungeeConfig.set("servers." + serverInfo.getName() + ".address", socketAddressToString(serverInfo.getSocketAddress()));
            bungeeConfig.set("servers." + serverInfo.getName() + ".restricted", serverInfo.isRestricted());
            saveConfig();
        }

        @SneakyThrows
        private static void saveConfig() {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(bungeeConfig, file);
        }

    }


}
