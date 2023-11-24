package com.zeeyeh.infinitemining;

import com.zeeyeh.infinitemining.entity.Mining;
import com.zeeyeh.infinitemining.utils.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PLayerMiningListener implements Listener {

    private int toInt(double value) {
        String[] strings = String.valueOf(value).split("[.]");
        return Integer.parseInt(strings[0]);
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        MiningManager miningManager = InfiniteMining.getInstance().getMiningManager();
        Mining mining = miningManager.getMiningBlockAttribute(block.getType());
        if (mining == null) {
            return;
        }
        Location saveLocation = mining.getLocation();
        int saveLocationX = toInt(saveLocation.getX());
        int saveLocationY = toInt(saveLocation.getY());
        int saveLocationZ = toInt(saveLocation.getZ());
        World saveLocationWorld = saveLocation.getWorld();
        Location blockLocation = block.getLocation();
        int blockLocationX = toInt(blockLocation.getX());
        int blockLocationY = toInt(blockLocation.getY());
        int blockLocationZ = toInt(blockLocation.getZ());
        World blockLocationWorld = blockLocation.getWorld();
        if (saveLocationX != blockLocationX &&
                saveLocationY != blockLocationY &&
                saveLocationZ != blockLocationZ &&
                saveLocationWorld != blockLocationWorld) {
            return;
        }
        //System.out.println("===============");
        //System.out.println(block.getLocation());
        //System.out.println(saveLocation);
        //System.out.println("===============");
        //if (!block.getLocation().equals(saveLocation)) {
        //    return;
        //}
        String tip = mining.getTip();
        if (tip.contains("[") && tip.contains("]")) {
            String[] tips = tip.split("]");
            String tipType = tips[0].replace("[", "").replace("]", "");
            String tipContent = tips[1].trim();
            if (tipType.equalsIgnoreCase("chatbox")) {
                MessageUtil.send(player, tipContent);
            } else if (tipType.equalsIgnoreCase("actionbar")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(tipContent));
            }
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(tip));
        }
        String command = mining.getCommand();
        if (command != null) {
            if (command.contains("[") && command.contains("]")) {
                String[] commands = command.split("]");
                String commandType = commands[0].replace("[", "").replace("]", "");
                String commandContent = commands[1].trim();
                if (commandType.equalsIgnoreCase("command")) {
                    player.performCommand(commandContent);
                } else if (commandType.equalsIgnoreCase("op")) {
                    if (!player.isOp()) {
                        player.setOp(true);
                        player.performCommand(commandContent);
                        player.setOp(false);
                    }
                    player.performCommand(commandContent);
                } else if (commandType.equalsIgnoreCase("console")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandContent);
                }
            }
        }
    }
}
