package com.zeeyeh.infinitemining;

import com.zeeyeh.infinitemining.entity.Mining;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class MiningHandler extends BukkitRunnable {

    private final Material material;
    private final Location location;
    private long waitLong = 0L;

    public MiningHandler(Material material, Location location) {
        this.material = material;
        this.location = location;
    }

    private int toInt(double value) {
        String[] strings = String.valueOf(value).split("[.]");
        return Integer.parseInt(strings[0]);
    }

    public void updateRefresh() {
        waitLong += 1000L;
    }

    @Override
    public void run() {
        Block block = location.getWorld().getBlockAt(
                toInt(location.getX()),
                toInt(location.getY()),
                toInt(location.getZ())
        );
        if (block.isEmpty()) {
            Random random = new Random();
            List<Material> noDestructMaps = InfiniteMining.getInstance().getNoDestructMaps();
            int i = random.nextInt(noDestructMaps.size());
            Material noMaterial = noDestructMaps.get(i);
            if (noMaterial == null) {
                noMaterial = Material.STONE;
            }
            block.setType(noMaterial);
        }
        Mining mining = InfiniteMining.getInstance().getMiningManager().getMining(material);
        if (mining.getInterval() < waitLong) {
            block.setType(material);
            waitLong = 0L;
        }
        updateRefresh();
        //System.out.println(waitLong);
    }
}
