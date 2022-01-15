package me.andyreckt.holiday.utils.command;

import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CommandUtils;
import me.andyreckt.holiday.utils.command.param.ParameterData;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class CommandData {

    @Getter
    final String[] names;
    @Getter
    final String permissionNode;
    @Getter
    final boolean async;
    @Getter
    final List<ParameterData> parameters;
    @Getter
    final Method method;
    @Getter
    final boolean consoleAllowed;

    public CommandData(Command commandAnnotation, List<ParameterData> parameters, Method method, boolean consoleAllowed) {
        this.names = commandAnnotation.names();
        this.permissionNode = commandAnnotation.perm();
        this.async = commandAnnotation.async();
        this.parameters = parameters;
        this.method = method;
        this.consoleAllowed = consoleAllowed;
    }

    public String getName() {
        return (names[0]);
    }

    public boolean canAccess(CommandSender sender) {
        // Console can do anything.
        if (!(sender instanceof Player)) {
            return (true);
        }

        if (permissionNode.equals("")) return true;
        Player p = (Player) sender;

        if (permissionNode.equals("op")) return p.isOp();

        if (p.isOp()) return true;
        if (p.hasPermission("*")) return true;
        if (p.hasPermission("*.*")) return true;

        String[] array = permissionNode.split("\\.");
        for (int i = 1; i < 4; i++) {
            if (array[i] == null) continue;

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < i; j++) {
                sb.append(array[j]);
                sb.append(".");
            }
            sb.append("*");
            if (p.hasPermission(sb.toString())) return true;
        }
        return p.hasPermission(permissionNode);
    }

    public String getUsageString() {
        return (getUsageString(getName()));
    }

    public String getUsageString(String aliasUsed) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ParameterData paramHelp : getParameters()) {
            boolean needed = paramHelp.getDefaultValue().isEmpty();
            stringBuilder.append(needed ? "<" : "[").append(paramHelp.getName());
            stringBuilder.append(needed ? ">" : "]").append(" ");
        }

        return ("/" + aliasUsed.toLowerCase() + " " + stringBuilder.toString().trim().toLowerCase());
    }

    public void execute(CommandSender sender, String[] params) {
        // We start to build the parameters we call the method with here.
        List<Object> transformedParameters = new ArrayList<>();

        // Add the sender.
        // If the method is expecting a Player or a general CommandSender will be handled by Java.
        transformedParameters.add(sender);

        // Fill in / validate parameters
        for (int parameterIndex = 0; parameterIndex < getParameters().size(); parameterIndex++) {
            ParameterData parameter = getParameters().get(parameterIndex);
            String passedParameter = (parameterIndex < params.length ? params[parameterIndex] : parameter.getDefaultValue()).trim();

            // We needed a parameter where we didn't get one (where there's no default value available)
            if (parameterIndex >= params.length && parameter.getDefaultValue().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsageString());
                return;
            }

            // Wildcards "capture" all strings after them
            if (parameter.isWildcard() && !passedParameter.trim().equals(parameter.getDefaultValue().trim())) {
                passedParameter = toString(params, parameterIndex);
            }

            // We try to transform the parameter given to us.
            Object result = CommandHandler.transformParameter(sender, passedParameter, parameter.getParameterClass());

            // If it's null that means the transformer tried (and failed) at transforming the value.
            // It'll have sent them a message and such, so we can just return.
            if (result == null) {
                return;
            }

            transformedParameters.add(result);

            // If it was a wildcard we don't want to bother parsing anything else
            // (even though there shouldn't have been anything else)
            if (parameter.isWildcard()) {
                break;
            }
        }

        try {
            // null = static method.
            method.invoke(null, transformedParameters.toArray());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "There was an error while trying to execute this command, please contact an admin.");
            e.printStackTrace();
        }

    }

    public static String toString(String[] args, int start) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int arg = start; arg < args.length; arg++) {
            stringBuilder.append(args[arg]).append(" ");
        }

        return (stringBuilder.toString().trim());
    }

}