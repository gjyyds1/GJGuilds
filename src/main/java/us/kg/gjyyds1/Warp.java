package us.kg.gjyyds1;

import org.bukkit.Location;

public class Warp {
    private String name;
    private double cost;
    private Location location;

    public Warp(String name, double cost, Location location) {
        this.name = name;
        this.cost = cost;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public Location getLocation() {
        return location;
    }
}



