package me.andyreckt.holiday.player.rank;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.RankUpdatePacket;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * This Class is from Zowpy
 * All credits to him
 *
 * @author Zowpy
 */

@Getter @Setter @SuppressWarnings("unchecked")
public class Rank {


    final UUID uuid;
    String name;

    String prefix, suffix;
    boolean bold, italic, isDefault, isStaff, isAdmin, isDev, isVisible;
    ChatColor color;

    int priority;

    List<String> permissions;

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
        this.name = document.getString("name");
        this.prefix = document.getString("prefix");
        this.suffix = document.getString("suffix");
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

        this.name = document.getString("name");
        this.prefix = document.getString("prefix");
        this.suffix = document.getString("suffix");
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
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new RankUpdatePacket(uuid));
    }


    public Document toBson() {
        return new Document("_id", uuid.toString())
                .append("name", name)
                .append("prefix", prefix)
                .append("suffix", suffix)
                .append("bold", bold)
                .append("italic", italic)
                .append("default", isDefault)
                .append("staff", isStaff)
                .append("admin", isAdmin)
                .append("dev", isDev)
                .append("visible", isVisible)
                .append("priority", priority)
                .append("color", color.name())
                .append("permissions", permissions == null ? new ArrayList<String>() : permissions);
    }

}
