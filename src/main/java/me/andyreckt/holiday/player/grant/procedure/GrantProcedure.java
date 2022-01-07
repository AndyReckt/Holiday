package me.andyreckt.holiday.player.grant.procedure;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.player.rank.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Class is from Zowpy
 * All credits to him
 *
 * @author Zowpy
 */

@Getter @Setter
public class GrantProcedure {

    @Getter  static List<GrantProcedure> procedures = new ArrayList<>();

     final UUID user, issuer;

     GrantStage stage;

     Rank rank;
     long duration;

    public GrantProcedure(UUID user, UUID issuer) {
        this.user = user;
        this.issuer = issuer;
        this.stage = GrantStage.RANK;

        procedures.add(this);
    }

    public static GrantProcedure getByIssuer(UUID issuer) {
        return procedures.stream().filter(grantProcedure -> grantProcedure.getIssuer().equals(issuer)).findFirst().orElse(null);
    }
}