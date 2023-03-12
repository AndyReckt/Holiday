package me.andyreckt.holiday.api;

import lombok.Getter;
import me.andyreckt.holiday.api.global.RedisCommand;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;


import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HolidayAPI implements API {

    @Getter
    private static HolidayAPI instance;

    public HolidayAPI() {
        instance = this;
    }

    private me.andyreckt.holiday.core.HolidayAPI getApi() {
        return me.andyreckt.holiday.core.HolidayAPI.getUnsafeAPI();
    }

    @Override
    public List<IGrant> getGrants() {
        return getApi().getGrants();
    }

    @Override
    public List<IGrant> getGrants(UUID uuid) {
        return getApi().getGrants(uuid);
    }

    @Override
    public IGrant getGrantFromId(UUID grantId) {
        return getApi().getGrantFromId(grantId);
    }

    @Override
    public void refreshGrants() {
        getApi().refreshGrants();
    }

    @Override
    public void saveGrant(IGrant grant) {
        getApi().saveGrant(grant);
    }

    @Override
    public void revokeGrant(IGrant grant, UUID revokedBy, String revokedOn, String revokedReason) {
        getApi().revokeGrant(grant, revokedBy, revokedOn, revokedReason);
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return getApi().getProfile(uuid);
    }

    @Override
    public CompletableFuture<HashMap<UUID, Profile>> getAllProfiles() {
        return getApi().getAllProfiles();
    }

    @Override
    public void saveProfile(Profile profile) {
        getApi().saveProfile(profile);
    }

    public void deleteProfile(Profile profile) {
        getApi().deleteProfile(profile);
    }

    @Override
    public void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason, String revokedOn) {
        getApi().revokePunishment(punishment, revokedBy, revokedReason, revokedOn);
    }

    @Override
    public List<IPunishment> getPunishments(UUID uniqueId) {
        return getApi().getPunishments(uniqueId);
    }

    @Override
    public List<IPunishment> getPunishments() {
        return getApi().getPunishments();
    }

    @Override
    public void savePunishment(IPunishment punishment) {
        getApi().savePunishment(punishment);
    }

    @Override
    public void refreshPunishments() {
        getApi().refreshPunishments();
    }

    @Override
    public IPunishment getPunishment(String id) {
        return getApi().getPunishment(id);
    }

    @Override
    public IRank getDefaultRank() {
        return getApi().getDefaultRank();
    }

    @Override
    public Set<IRank> getRanks() {
        return getApi().getRanks();
    }

    @Override
    public List<IRank> getRanksSorted() {
        return getApi().getRanksSorted();
    }

    @Override
    public IRank createRank(String name) {
        return getApi().createRank(name);
    }

    @Override
    public void saveRank(IRank rank) {
        getApi().saveRank(rank);
    }

    @Override
    public void deleteRank(IRank rank) {
        getApi().deleteRank(rank);
    }

    @Override
    public IRank getRank(String name) {
        return getApi().getRank(name);
    }

    @Override
    public IRank getRank(UUID uuid) {
        return getApi().getRank(uuid);
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return getApi().isOnline(playerId);
    }

    @Override
    public Map<UUID, String> getOnlinePlayers() {
        return getApi().getOnlinePlayers();
    }

    @Override
    public Map<String, IServer> getServers() {
        return getApi().getServers();
    }

    @Override
    public IServer getServer(String serverId) {
        return getApi().getServer(serverId);
    }

    @Override
    public IServer getServer(UUID playerId) {
        return getApi().getServer(playerId);
    }

    @Override
    public <T> T runRedisCommand(RedisCommand<T> command) {
        return getApi().runRedisCommand(command);
    }
}
