package me.andyreckt.holiday.staff.util.sunset.executor;

import lombok.SneakyThrows;
import me.andyreckt.holiday.staff.util.sunset.Sunset;
import me.andyreckt.holiday.staff.util.sunset.annotations.Command;
import me.andyreckt.holiday.staff.util.sunset.annotations.Param;
import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class SunsetCommand extends org.bukkit.command.Command {

    private final Sunset sunset;
    private final Command command;
    private final Method method;
    private final Object instance;


    public SunsetCommand(Sunset sunset, Method method, Object instance, Command command, String usage, List<String> aliases) {
        super(command.names()[0], command.description(), usage, aliases);
        this.sunset = sunset;
        this.command = command;
        this.method = method;
        this.instance = instance;
    }


    @Override @SneakyThrows
    public boolean execute(CommandSender commandSender, String alias, String[] args) {

        List<Object> parameters = new ArrayList<>();

        if (method.getParameterTypes()[0].equals(ConsoleCommandSender.class)) {
            if (!(commandSender instanceof ConsoleCommandSender)) {
                commandSender.sendMessage(ChatColor.RED + "This command can only be executed with the console.");
                return false;
            }
        }

        if (method.getParameterTypes()[0].equals(Player.class)) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "This command can only be executed as a player.");
                return false;
            }

            parameters.add(commandSender);
        } else parameters.add(commandSender);

        if (!command.permission().get().equalsIgnoreCase("")) {
            if (command.permission().get().equalsIgnoreCase("op")) {
                if (commandSender instanceof Player && (!commandSender.hasPermission("op")) && (!commandSender.isOp())) {
                    commandSender.sendMessage(sunset.getPermissionMessage());
                    return false;
                }
            }
            if (!commandSender.hasPermission(command.permission().get())) {
                commandSender.sendMessage(sunset.getPermissionMessage());
                return false;
            }
        }
        if (method.getParameterTypes().length > 1) {
            for (int index = 1; index < method.getParameterTypes().length; index++) {

                Param param = null;

                for (Annotation annotation : method.getParameterAnnotations()[index]) {
                    if (annotation instanceof Param) {
                        param = (Param) annotation;
                        break;
                    }
                }

                if (param == null) {
                    commandSender.sendMessage(ChatColor.RED + "Parameter annotation is null ?!");
                    return false;
                }

                if (args.length == 0) {
                    boolean bool = false;
                    int i = 0;
                    for (Annotation[] annotationArray : method.getParameterAnnotations()) {
                        for (Annotation annotation : annotationArray) {
                            if (annotation instanceof Param) {
                                param = (Param) annotation;
                                if (param.baseValue().equalsIgnoreCase("")) bool = true;
                                else parameters.add(sunset.getTypesMap().get(method.getParameterTypes()[i]).transform(commandSender, param.baseValue()));
                            }
                        }
                        i++;
                    }
                    if (bool) {
                        StringBuilder usage = new StringBuilder();
                        for (Annotation[] annotations : method.getParameterAnnotations()) {
                            for (Annotation annotation : annotations) {
                                if (annotation instanceof Param) {
                                    param = (Param) annotation;
                                    usage.append(!param.baseValue().equals("") ? '[' + param.name() + ']' : '<' + param.name() + '>').append(" ");
                                }
                            }
                        }
                        commandSender.sendMessage(ChatColor.RED + "Usage: /" + command.names()[0] + " " + usage.toString().trim());
                        return false;
                    }
                } else {
                    if (args.length <= index-1 || args[index-1] == null || args[index-1].equals("")) {
                        if (param.baseValue().equalsIgnoreCase("")) {
                            StringBuilder usage = new StringBuilder();
                            for (Annotation[] annotations : method.getParameterAnnotations()) {
                                for (Annotation annotation : annotations) {
                                    if (annotation instanceof Param) {
                                        param = (Param) annotation;
                                        usage.append(!param.baseValue().equals("") ? '[' + param.name() + ']' : '<' + param.name() + '>').append(" ");
                                    }
                                }
                            }
                            commandSender.sendMessage(ChatColor.RED + "Usage: /" + command.names()[0] + " " + usage.toString().trim());
                            return false;
                        }

                        if (param.wildcard()) {
                            parameters.add(sunset.getTypesMap().get(method.getParameterTypes()[index]).transform(commandSender, param.baseValue()));
                            break;
                        } else {
                            parameters.add(sunset.getTypesMap().get(method.getParameterTypes()[index]).transform(commandSender, param.baseValue()));
                        }
                    } else if (param.wildcard()) {
                        StringBuilder sb = new StringBuilder();

                        for (int arg = index-1; arg < args.length; arg++) {
                            sb.append(args[arg]).append(" ");
                        }

                        parameters.add(sunset.getTypesMap().get(method.getParameterTypes()[index]).transform(commandSender, sb.toString()));
                        break;
                    } else parameters.add(sunset.getTypesMap().get(method.getParameterTypes()[index]).transform(commandSender, args[index-1]));
                }
            }
        }
        for (Object parameter : parameters) {
            if (parameter == null) {
                return false;
            }
        }

        if (command.async()) ForkJoinPool.commonPool().execute(() -> {
            try {
                method.invoke(instance, parameters.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        else method.invoke(instance, parameters.toArray());
        return true;
    }

    @Override @SneakyThrows
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) return (null);

        Player player = (Player) sender;

        Param param = null;
        // if (!((method.getParameterCount() - 1) <= args.length)) return (new ArrayList<>());

        int index = args.length - 1;
        if (args.length == 0 || args.length > (method.getParameterCount() - 1)) {
            return (new ArrayList<>());
        }
        for (Annotation annotation : method.getParameters()[args.length].getAnnotations()) {
            if (annotation instanceof Param) {
                param = (Param) annotation;
                break;
            }
        }

        if (param == null) return (new ArrayList<>());
        if (!Arrays.equals(param.tabCompleteFlags(), new String[]{""}))
            return (Arrays.asList(param.tabCompleteFlags()));
        PType<?> pType = sunset.getTypesMap().get(method.getParameterTypes()[args.length]);
        if (pType == null) return (new ArrayList<>());

        if (param.wildcard()) {

            StringBuilder sb = new StringBuilder();

            for (int arg = index; arg < args.length - 1; arg++) {
                sb.append(args[arg]).append(" ");
            }

            return (pType.complete(player, sb.toString()));

        } else return (pType.complete(player, args[index]));
    }
}
