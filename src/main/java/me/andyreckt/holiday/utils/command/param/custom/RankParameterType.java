package me.andyreckt.holiday.utils.command.param.custom;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.sunset.parameter.PType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankParameterType implements PType<Rank> {

    public Rank transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equals("")) {
            return Holiday.getInstance().getProfileHandler().getByPlayer(((Player) sender)).getHighestRank();
        }
        if (source.equalsIgnoreCase("default")) return Holiday.getInstance().getRankHandler().getDefaultRank();

        Rank rank = Holiday.getInstance().getRankHandler().getFromName(source);

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