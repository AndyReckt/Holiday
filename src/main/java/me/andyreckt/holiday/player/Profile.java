package me.andyreckt.holiday.player;


import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.ProfilePacket;
import me.andyreckt.holiday.player.disguise.IDisguiseHandler;
import me.andyreckt.holiday.player.disguise.impl.v1_8.DisguiseHandler_1_8;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.grant.GrantComparator;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Profile {

    private UUID uuid;
    private String name, ip, lowerCaseName;
    private List<String> ips, permissions;

    private boolean online, liked, inStaffMode, messagesEnabled, socialSpy;
    private int coins;

    private long firstLogin, lastSeen;

    private boolean console = false;
    @Nullable
    private String currentServer;
    @Nullable
    private IDisguiseHandler.DisguiseData disguiseData;


    public Profile(Player player) {
        new Profile(player.getUniqueId(), true);
    }

    public Profile(String name) {
        if (hasProfile(name.toLowerCase())) {
            getProfileFromDb(name.toLowerCase());
        }
    }

    public Profile() {
        this.uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.name = "&4Console";
        this.ip = "0";
        this.lowerCaseName = "&4CONSOLE";
        this.ips = new ArrayList<>();
        this.permissions = Collections.singletonList("*");
        this.online = true;
        this.firstLogin = 0L;
        this.lastSeen = 0L;
        this.console = true;
        this.coins = 0;
        this.liked = false;
        this.inStaffMode = false;
        this.messagesEnabled = false;
        this.socialSpy = false;
    }

    public Profile(UUID uuid) {
        new Profile(uuid, true);
    }

    public Profile(Document document) {
        loadFromDocument(document);
    }

    public Profile(UUID uuid, boolean cache) {
        if (hasProfile(uuid)) {
            this.uuid = uuid;
            load(cache);
        } else {
            create(uuid);
        }
    }

    private boolean hasProfile(UUID uuid) {
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null;
    }

    private boolean hasProfile(String name) {
        if (DisguiseHandler_1_8.DisguiseRequest.alreadyUsed(name)) return true;

        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("lname", name.toLowerCase())).first();
        return document != null;
    }

    public void load(boolean cache) {
        getProfileFromDb(uuid);
        if (cache) Holiday.getInstance().getProfileHandler().updateProfile(this);

        Tasks.runLater(() -> {
            setBukkitDisplayName();
            setPlayerListName();
        }, 20L);
    }

    private void getProfileFromDb(UUID uuid) {
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        assert document != null;
        loadFromDocument(document);
    }

    private void getProfileFromDb(String name) {
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("lname", name.toLowerCase())).first();
        assert document != null;
        loadFromDocument(document);
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new ProfilePacket(this));
    }

    public void autoSave() {
        MongoUtils.submitToThread(() -> MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public void saveOnStop() {
        MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true));
    }

    private void create(UUID uuid) {
        this.uuid = uuid;
        if (hasProfile(uuid)) {
            load(true);
            return;
        }
        this.name = "null";
        this.ip = "null";
        this.permissions = new ArrayList<>();
        this.ips = new ArrayList<>();
        this.firstLogin = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
        this.online = true;
        this.liked = false;
        this.inStaffMode = false;
        this.socialSpy = false;
        this.coins = 100;
        this.lowerCaseName = name.toLowerCase();
        this.messagesEnabled = true;
        Holiday.getInstance().getGrantHandler().newDefaultGrant(uuid);
        Holiday.getInstance().getProfileHandler().updateProfile(this);
        save();

        Tasks.runLater(() -> reloadProfile(uuid), 15L);
    }

    private void reloadProfile(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid)).first();

        this.uuid = uuid;

        if (document != null) {
            String ipp;
            if (document.getString("ip").equalsIgnoreCase("null")) {
                ipp = StringUtils.hash(player.getAddress().getAddress().getHostAddress());
            } else {
                ipp = document.getString("ip");
            }
            List<String> ipsL = new ArrayList<>();
            if (document.getList(ips, String.class).isEmpty()) {
                ipsL.add(StringUtils.hash(player.getAddress().getAddress().getHostAddress()));
            } else {
                ipsL.addAll(document.getList("ips", String.class));
            }
            this.name = player.getName();
            this.ip = ipp;
            this.ips = ipsL;
            this.permissions = new ArrayList<>();
            this.firstLogin = document.getLong("firstLogin");
            this.lastSeen = document.getLong("lastSeen");
            this.liked = document.getBoolean("liked");
            this.inStaffMode = document.getBoolean("staff");
            this.online = document.getBoolean("online");
            this.coins = document.getInteger("coins");
            this.socialSpy = false;
            this.lowerCaseName = name.toLowerCase();
            this.messagesEnabled = document.getBoolean("isMessages");

            if (isDisguised()) this.disguiseData = Holiday.getInstance().getDisguiseHandler().getDisguiseData(uuid);
            Holiday.getInstance().getProfileHandler().updateProfile(this);
            save();
        }

        Tasks.runLater(() -> {
            setBukkitDisplayName();
            setPlayerListName();
        }, 20L);
    }

    public boolean isFrozen() {
        return getPlayer() != null && getPlayer().hasMetadata("frozen");
    }

    public Document toBson() {
        Profile profile = this;
        return new Document("_id", profile.getUuid().toString())
                .append("name", profile.getName())
                .append("ip", profile.getIp())
                .append("ips", profile.getIps())
                .append("permissions", profile.getPermissions())
                .append("firstLogin", profile.getFirstLogin())
                .append("lastSeen", profile.getLastSeen())
                .append("online", profile.isOnline())
                .append("liked", profile.isLiked())
                .append("staff", profile.isInStaffMode())
                .append("coins", profile.getCoins())
                .append("server", profile.getCurrentServer())
                .append("socialSpy", profile.isSocialSpy())
                .append("isMessages", profile.isMessagesEnabled())
                .append("lname", profile.getName().toLowerCase());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public String getNameWithColor() {
        Rank rank = getHighestVisibleGrant().getRank();
        String color = "";
        color += rank.getColor().toString();
        if (rank.isItalic()) color += CC.I;
        if (rank.isBold()) color += CC.B;
        return color + name;
    }

    public boolean isVanished() {
        return isInStaffMode() && Holiday.getInstance().getStaffHandler().getStaffPlayer(getPlayer()) != null && Holiday.getInstance().getStaffHandler().getStaffPlayer(getPlayer()).isInVanish();
    }

    public boolean hasEnoughCoins(int number) {
        return this.getCoins() >= number;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public List<PunishData> getPunishments() {
        return new ArrayList<>(Holiday.getInstance().getPunishmentHandler().getAllPunishmentsProfile(this));
    }


    public List<Profile> getAlts() {
        List<Profile> toReturn = new ArrayList<>();
        MongoUtils.getProfileCollection()
                .find(Filters.eq("ip", getIp()))
                .forEach((Block<Document>) doc -> {
                    Profile alt = new Profile(doc);
                    if (!toReturn.contains(alt)) toReturn.add(alt);
                });
        return toReturn;
    }

    public List<String> formatAlts() {
        List<String> toReturn = new ArrayList<>();
        List<Profile> profiles = new ArrayList<>(getAlts());
        profiles.forEach(profile -> {
            if (PunishmentUtils.checkBlacklisted(profile)) toReturn.add("&4" + profile.getName());
            else if (PunishmentUtils.checkIPBanned(profile) || PunishmentUtils.checkBanned(profile))
                toReturn.add("&c" + profile.getName());
            else if (PunishmentUtils.checkMuted(profile)) toReturn.add(CC.CHAT + profile.getName());
            else if (profile.isOnline()) toReturn.add("&a" + profile.getName());
            else toReturn.add("&7" + profile.getName());
        });
        return toReturn;
    }

    public List<Grant> getGrants() {
        return Holiday.getInstance().getGrantHandler().getGrants(uuid);
    }

    public String getDisplayName() {
        if (this.disguiseData != null) {
            return this.disguiseData.displayName();
        } else {
            return getName();
        }
    }

    public String getDisplayNameWithColor() {

        Rank rank = getHighestVisibleRank();
        String name = this.name;

        if (this.disguiseData != null) {
            rank = disguiseData.disguiseRank();
            name = disguiseData.displayName();
        }

        String color = "";
        color += rank.getColor().toString();
        if (rank.isItalic()) color += CC.I;
        if (rank.isBold()) color += CC.B;

        return color + name;
    }

    public String getDisplayNameWithColorAndVanish() {
        String prefix = isVanished() ? CC.GRAY + "*" : "";
        return prefix + getDisplayNameWithColor();
    }

    public boolean isDisguised() {
        return Holiday.getInstance().getDisguiseHandler().isDisguised(uuid);
    }

    public boolean isDisguisedOnLogin() {
        return Holiday.getInstance().getDisguiseHandler().isDisguisedMongo(uuid);
    }

    private void loadFromDocument(Document document) {
        this.uuid = UUID.fromString(document.getString("_id"));

        this.name = document.getString("name");
        this.ip = document.getString("ip");

        this.ips = document.getList("ips", String.class);
        this.permissions = document.getList("permissions", String.class);

        this.online = document.getBoolean("online");
        this.liked = document.getBoolean("liked");
        this.inStaffMode = document.getBoolean("staff");

        this.firstLogin = document.getLong("firstLogin");
        this.lastSeen = document.getLong("lastSeen");

        this.coins = document.getInteger("coins");
        this.currentServer = document.getString("server");
        this.lowerCaseName = name.toLowerCase();
        this.messagesEnabled = document.getBoolean("isMessages");
        this.socialSpy = document.getBoolean("socialSpy") != null && document.getBoolean("socialSpy");

        if (Holiday.getInstance().getDisguiseHandler().isDisguisedMongo(uuid)) this.disguiseData = Holiday.getInstance().getDisguiseHandler().getDisguiseData(uuid);
    }

    public Grant getHighestGrant() {
        return getActiveGrants().stream().max(new GrantComparator()).orElseGet(() -> Holiday.getInstance().getGrantHandler().newDefaultGrant(uuid));
    }

    public Grant getHighestVisibleGrant() {
        return getActiveGrants().stream().filter(grant -> grant.getRank().isVisible()).max(new GrantComparator()).orElseGet(() -> Holiday.getInstance().getGrantHandler().newDefaultGrant(uuid));
    }

    public Rank getHighestRank() {
        return getHighestGrant().getRank();
    }

    public Rank getHighestVisibleRank() {
        return getHighestVisibleGrant().getRank();
    }

    public Rank getDisplayRank() {
        return disguiseData != null ? disguiseData.disguiseRank() : getHighestVisibleRank();
    }

    public List<Grant> getActiveGrants() {
        return getGrants().stream().filter(Grant::isActive).collect(Collectors.toList());
    }

    public boolean isStaff() {
        for (Grant o : getActiveGrants()) {
            if (o.getRank().isStaff()) return true;
        }
        return false;
    }

    public boolean isAdmin() {
        for (Grant o : getActiveGrants()) {
            if (o.getRank().isAdmin()) return true;
        }
        return false;
    }

    public boolean isOp() {
        for (Grant o : getActiveGrants()) {
            if (o.getRank().isDev()) return true;
        }
        return false;
    }

    public boolean hasRank(Rank rank) {
        for (Grant grant : getActiveGrants()) {
            if (grant.getRank().getUuid() == rank.getUuid()) return true;
        }
        return false;
    }

    public void delete() {
        MongoUtils.getProfileCollection().deleteOne(Filters.eq("_id", uuid.toString()));
        Holiday.getInstance().getRedis().sendPacket(new ProfilePacket.ProfileDeletePacket(this));
    }

    public boolean hasPermission(String perm) {
        if (getPermissions().contains(perm)) return true;
        for (Grant o : getActiveGrants()) {
            if (o.getRank().getPermissions().contains(perm)) return true;
            if (o.getRank().getPermissions().contains("*")) return true;
        }
        for (OfflinePlayer player : Bukkit.getOperators()) {
            if (player.getUniqueId() == uuid) return true;
        }
        return false;
    }


    public String getName() {
        return name == null ? "CONSOLE" : name;
    }


    public void setBukkitDisplayName() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) return;
        if (Holiday.getInstance().getSettings().getBoolean("PLAYER.DISPLAYNAME"))
            player.setDisplayName(getDisplayRank().getPrefix() + getDisplayNameWithColor());
    }

    public void setPlayerListName() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) return;
        if (Holiday.getInstance().getSettings().getBoolean("PLAYER.PLAYERLISTNAME"))
            player.setPlayerListName(getDisplayNameWithColor());
    }
}
