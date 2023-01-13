package me.andyreckt.holiday.bukkit.util.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PlayerList { //PVPTEMPLE

	private final List<Player> players;

	public static PlayerList getVisiblyOnline(CommandSender sender) {
		return getOnline().visibleTo(sender);
	}

	public static PlayerList getOnline() {
		return new PlayerList(new ArrayList<>(Bukkit.getOnlinePlayers()));
	}

	public PlayerList visibleTo(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

			this.players.removeIf(other -> other != player &&
					(Holiday.getInstance().getApi().getProfile(other.getUniqueId()).getStaffSettings().isVanished()
							&& !profile.isStaff()));
		}
		return this;
	}

//	public PlayerList canSee(CommandSender sender) {
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//			this.players.removeIf(other -> other != player &&
//					(Holiday.getInstance().getProfileHandler().getByPlayer(player).isVanished()
//							&& !Holiday.getInstance().getProfileHandler().getByPlayer(other).isStaff()));
//		}
//		return this;
//	}

	public PlayerList visibleRankSorted() {
		this.players.sort(RANK_ORDER);
		return this;
	}

	public List<String> asColoredNames() {
		return this.players.stream()
				.map(Player::getUniqueId)
				.map(uid -> Holiday.getInstance().getApi().getProfile(uid))
				.map(profile -> UserConstants.getDisplayNameWithColorAndVanish(profile) + CC.GRAY)
				.collect(Collectors.toList());
	}

	private static final Comparator<Player> RANK_ORDER = ((o1, o2) -> {
		Profile p1 = Holiday.getInstance().getApi().getProfile(o1.getUniqueId());
		Profile p2 = Holiday.getInstance().getApi().getProfile(o2.getUniqueId());
		return - (p1.getDisplayRank().getPriority() - p2.getDisplayRank().getPriority());
	});

}
