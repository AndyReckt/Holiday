package me.andyreckt.holiday.rank;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.utils.MongoUtils;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.UUID;
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
     boolean bold, italic, purchasable, isDefault, isStaff, isAdmin, isDev;
     ChatColor color;

     int priority; //price

     String[] permissions;

    public Rank(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    public Rank(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
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
        this.purchasable = document.getBoolean("purchasable");
        this.isDefault = document.getBoolean("default");
        this.isStaff = document.getBoolean("staff");
        this.isAdmin = document.getBoolean("admin");
        this.isDev = document.getBoolean("dev");


        this.color = ChatColor.valueOf(document.getString("color"));

        this.priority = document.getInteger("priority");
        //this.price = document.getInteger("price");

        if (document.getString("permissions").equalsIgnoreCase("null")) {
            this.permissions = new String[0];
            return;
        }

        this.permissions = document.getList("permissions", String.class).toArray(new String[0]);
    }

    public void save() {
        MongoUtils.getExecutor().execute(() -> MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public Document toBson() {
        return new Document("_id", uuid.toString())
                .append("name", name)
                .append("prefix", prefix)
                .append("suffix", suffix)
                .append("bold", bold)
                .append("italic", italic)
                .append("purchasable", purchasable)
                .append("default", isDefault)
                .append("staff", isStaff)
                .append("admin", isAdmin)
                .append("dev", isDev)
                //.append("price", price)
                .append("priority", priority)
                .append("color", color.name())
                .append("permissions", permissions == null ? "null" : permissions);
    }
}
