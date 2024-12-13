package us.kg.gjyyds1;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Guild {
    private UUID leader;
    private Map<UUID, Boolean> members = new HashMap<>(); // True means member, False means invited
    private int expPool = 0;
    private double bank = 0.0;
    private Map<String, Warp> warps = new HashMap<>();
    private Location tpPoint;

    public Guild(UUID leader) {
        this.leader = leader;
        members.put(leader, true);
    }

    public boolean isLeader(UUID playerId) {
        return leader.equals(playerId);
    }

    public boolean isMember(UUID playerId) {
        return members.containsKey(playerId);
    }

    public void addMember(UUID playerId) {
        members.put(playerId, true);
    }

    public void removeMember(UUID playerId) {
        members.remove(playerId);
    }

    public int getExpPool() {
        return expPool;
    }

    public void addToExpPool(int amount) {
        expPool += amount;
    }

    public double getBank() {
        return bank;
    }

    public void addToBank(double amount) {
        bank += amount;
    }

    public void subtractFromBank(double amount) {
        bank -= amount;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public void addWarp(Warp warp) {
        warps.put(warp.getName(), warp);
    }

    public void removeWarp(String name) {
        warps.remove(name);
    }

    public Warp getWarp(String name) {
        return warps.get(name);
    }

    public void setTpPoint(Location location) {
        this.tpPoint = location;
    }

    public Location getTpPoint() {
        return tpPoint;
    }
}



