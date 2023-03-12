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

    @Override
    public List<IGrant> getGrants() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<IGrant> getGrants(UUID uuid) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IGrant getGrantFromId(UUID grantId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void refreshGrants() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void saveGrant(IGrant grant) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void revokeGrant(IGrant grant, UUID revokedBy, String revokedOn, String revokedReason) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Profile getProfile(UUID uuid) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public CompletableFuture<HashMap<UUID, Profile>> getAllProfiles() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void saveProfile(Profile profile) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void deleteProfile(Profile profile) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason, String revokedOn) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<IPunishment> getPunishments(UUID uniqueId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<IPunishment> getPunishments() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void savePunishment(IPunishment punishment) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void refreshPunishments() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IPunishment getPunishment(String id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IRank getDefaultRank() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Set<IRank> getRanks() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<IRank> getRanksSorted() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IRank createRank(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void saveRank(IRank rank) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void deleteRank(IRank rank) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IRank getRank(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IRank getRank(UUID uuid) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isOnline(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Map<UUID, String> getOnlinePlayers() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Map<String, IServer> getServers() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IServer getServer(String serverId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public IServer getServer(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    @Override
    public <T> T runRedisCommand(RedisCommand<T> command) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
