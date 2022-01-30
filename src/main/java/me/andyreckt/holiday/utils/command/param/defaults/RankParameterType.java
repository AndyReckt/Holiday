package me.andyreckt.holiday.utils.command.param.defaults;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RankParameterType implements ParameterType<Rank> {

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

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (Rank rank : Holiday.getInstance().getRankHandler().ranks()) {
            if (StringUtils.startsWithIgnoreCase(rank.getName(), source)) {
                completions.add(rank.getName());
            }
        }

        return (completions);
    }

}