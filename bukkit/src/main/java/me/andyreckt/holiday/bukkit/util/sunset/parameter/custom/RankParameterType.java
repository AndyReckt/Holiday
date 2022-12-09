package me.andyreckt.holiday.bukkit.util.sunset.parameter.custom;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.PType;
import me.andyreckt.holiday.core.user.rank.Rank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankParameterType implements PType<IRank> {

    public IRank transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equals("")) {
            return Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId()).getHighestRank();
        }
        if (source.equalsIgnoreCase("default")) return Holiday.getInstance().getApi().getDefaultRank();

        IRank rank = Holiday.getInstance().getApi().getRank(source);

        if (rank == null) {
            sender.sendMessage(CC.translate("&cThis rank doesnt exist"));
            return (null);
        }

        return (rank);
    }

    public List<String> complete(Player sender, String source) {
        List<String> completions = new ArrayList<>();

        for (Rank rank : Holiday.getInstance().getRankHandler().ranks()) {
            if (StringUtils.startsWithIgnoreCase(rank.getName(), source)) {
                completions.add(rank.getName());
            }
        }

        return (completions);
    }

}