package com.zeeyeh.infinitemining;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zeeyeh.infinitemining.entity.Mining;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MiningManager {
    private final List<Mining> minings;
    private final File miningFolder;

    public MiningManager() {
        minings = new ArrayList<>();
        miningFolder = new File(InfiniteMining.getInstance().getDataFolder(), "minings");
        load();
    }

    private void load() {
        if (!miningFolder.exists()) {
            miningFolder.mkdirs();
        }
        File[] files = miningFolder.listFiles(file -> file.getName().endsWith(".json") && file.isFile());
        if (files == null) {
            return;
        }
        try {
            Gson gson = new Gson();
            for (File file : files) {
                String jsonFileContent = Files.readString(file.toPath());
                JsonObject jsonObject = gson.fromJson(jsonFileContent, JsonObject.class);
                String worldName = jsonObject.get("world").getAsString();
                World world = Bukkit.getWorld(worldName);
                JsonObject locationObject = jsonObject.getAsJsonObject("location");
                double x = locationObject.get("x").getAsDouble();
                double y = locationObject.get("y").getAsDouble();
                double z = locationObject.get("z").getAsDouble();
                Location location = new Location(world, x, y, z);
                String materialString = jsonObject.get("material").getAsString();
                long interval = jsonObject.get("interval").getAsLong();
                String tip = jsonObject.has("tip") ? jsonObject.get("tip").getAsString() : null;
                String command = jsonObject.has("command") ? jsonObject.get("command").getAsString() : null;
                Long xp = jsonObject.has("xp") ? jsonObject.get("xp").getAsLong() : null;
                Integer xpLevel = jsonObject.has("xpLevel") ? jsonObject.get("xpLevel").getAsInt() : null;
                Material material = Material.getMaterial(materialString.toUpperCase());
                minings.add(new Mining(
                        location,
                        material,
                        interval,
                        tip,
                        command,
                        xp,
                        xpLevel
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        minings.clear();
    }

    public void reload() {
        clear();
        load();
    }

    public boolean isMiningBlock(Material material) {
        for (Mining mining : minings) {
            if (mining.getMaterial().equals(material)) {
                return true;
            }
        }
        return false;
    }

    public Mining getMiningBlockAttribute(Material material) {
        for (Mining mining : minings) {
            if (mining.getMaterial().equals(material)) {
                return mining;
            }
        }
        return null;
    }

    public Mining getMining(Material material) {
        for (Mining mining : minings) {
            if (mining.getMaterial().equals(material)) {
                return mining;
            }
        }
        return null;
    }

    public List<Mining> getMinings() {
        return minings;
    }
}
