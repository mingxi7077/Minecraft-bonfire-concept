package com.zeeyeh.infinitemining.entity;

import org.bukkit.Location;
import org.bukkit.Material;

public class Mining {
    private Location location;
    private Material material;
    private long interval;
    private String tip;
    private String command;
    private Long xp;
    private Integer xpLevel;

    public Mining(Location location, Material material, long interval, String tip, String command, Long xp, Integer xpLevel) {
        this.location = location;
        this.material = material;
        this.interval = interval;
        this.tip = tip;
        this.command = command;
        this.xp = xp;
        this.xpLevel = xpLevel;
    }

    public Location getLocation() {
        return location;
    }

    public Mining setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public Mining setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public long getInterval() {
        return interval;
    }

    public Mining setInterval(long interval) {
        this.interval = interval;
        return this;
    }

    public String getTip() {
        return tip;
    }

    public Mining setTip(String tip) {
        this.tip = tip;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public Mining setCommand(String command) {
        this.command = command;
        return this;
    }

    public Long getXp() {
        return xp;
    }

    public Mining setXp(Long xp) {
        this.xp = xp;
        return this;
    }

    public Integer getXpLevel() {
        return xpLevel;
    }

    public Mining setXpLevel(Integer xpLevel) {
        this.xpLevel = xpLevel;
        return this;
    }
}
