package me.andyreckt.holiday.player.rank;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.RankPacket;
import me.andyreckt.holiday.other.enums.UpdateType;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * This Class is from Zowpy
 * All credits to him
 *
 * @author Zowpy
 */

@SuppressWarnings("unchecked")
@Getter @Setter
@AllArgsConstructor
public class Rank {


    private final UUID uuid;
    private String name;
    private String prefix, suffix, displayName;
    private boolean bold, italic, isDefault, isStaff, isAdmin, isDev, isVisible;
    private ChatColor color;

    private int priority;

    private List<String> permissions;
    private List<UUID> childs;

    public Rank(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    public Rank(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Rank(Document document) {
        this.uuid = UUID.fromString(document.getString("_id"));
        loadFromDocument(document);
    }

    public Rank(UUID uuid) {
        this.uuid = uuid;
    }

    public void load() {
        Document document = (Document) MongoUtils.getRankCollection().find(Filters.eq("_id", uuid.toString())).first();

        if (document == null) {
            save();
            return;
        }

        loadFromDocument(document);
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new RankPacket(this, UpdateType.UPDATE));
    }


    public Document toBson() {

        List<String> childs = new ArrayList<>();
        for (UUID child : this.childs) {
            childs.add(child.toString());
        }

        return new Document("_id", uuid.toString())
                .append("name", name)
                .append("prefix", prefix)
                .append("suffix", suffix)
                .append("displayName", displayName)
                .append("bold", bold)
                .append("italic", italic)
                .append("default", isDefault)
                .append("staff", isStaff)
                .append("admin", isAdmin)
                .append("dev", isDev)
                .append("visible", isVisible)
                .append("priority", priority)
                .append("color", color.name())
                .append("childs", this.childs == null ? new ArrayList<String>() : childs)
                .append("permissions", permissions == null ? new ArrayList<String>() : permissions);
    }


    private void loadFromDocument(Document document) {
        this.name = document.getString("name");
        this.prefix = document.getString("prefix");
        this.suffix = document.getString("suffix");
        this.displayName = document.getString("displayName");
        this.bold = document.getBoolean("bold");
        this.italic = document.getBoolean("italic");
        this.isDefault = document.getBoolean("default");
        this.isStaff = document.getBoolean("staff");
        this.isAdmin = document.getBoolean("admin");
        this.isDev = document.getBoolean("dev");
        this.isVisible = document.getBoolean("visible");
        this.color = ChatColor.valueOf(document.getString("color"));
        this.priority = document.getInteger("priority");
        this.permissions = document.getList("permissions", String.class);

        if (!document.getList("childs", String.class).isEmpty() && !(document.getList("childs", String.class) == null))
        for (String child : document.getList("childs", String.class)) {
            this.childs.add(UUID.fromString(child));
        }
        else this.childs = new ArrayList<>();
    }

    public void addPermission(String perm) {
        this.permissions.add(perm);
    }

    public void addChild(Rank rank) {
        this.childs.add(rank.getUuid());
    }

    public void removeChild(Rank rank) {
        this.childs.add(rank.getUuid());
    }

    public void removePermission(String perm) {
        this.permissions.remove(perm);
    }
}
