package com.zeeyeh.archcareerlevel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zeeyeh.archcareerlevel.api.ArchCareerLevelLangApi;
import com.zeeyeh.archcareerlevel.command.ArchCareerLevelCommand;
import com.zeeyeh.archcareerlevel.entity.CareerLevel;
import com.zeeyeh.archcareerlevel.manager.ArchCareerLevelManagerProvider;
import com.zeeyeh.archcareerlevel.manager.ConfigManagerProvider;
import com.zeeyeh.archcareerlevel.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ArchCareerLevel extends JavaPlugin implements PluginMessageListener {

    private static ArchCareerLevel instance;
    private ConfigManagerProvider configManager;
    private ArchCareerLevelManagerProvider careerLevelManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        if (getConfig().getBoolean("enabled")) {
            configManager = new ConfigManagerProvider();
            careerLevelManager = new ArchCareerLevelManagerProvider();
            String path = getConfigManager().getConfig("config").getString("path");
            File file = new File(getDataFolder(), path);
            if (!file.exists()){
                file.mkdirs();
            }
            ArchCareerLevelCommand archCareerLevelCommand = new ArchCareerLevelCommand();
            getCommand("archcareerlevel").setExecutor(archCareerLevelCommand);
            getCommand("archcareerlevel").setTabCompleter(archCareerLevelCommand);
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLevelLangApi.translate("loadedPlugin"));
        }
    }

    public static ArchCareerLevel getInstance() {
        return instance;
    }

    public ConfigManagerProvider getConfigManager() {
        return configManager;
    }

    public ArchCareerLevelManagerProvider getCareerLevelManager() {
        return careerLevelManager;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        String data = new String(bytes, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        String action = jsonObject.get("action").getAsString();
        JsonObject retJsonObject = new JsonObject();
        if (action.equalsIgnoreCase("ArchCareer.Level.getLevelPlayers")) {
            // 获取所有职业等级为指定内容的玩家数
            String levelName = jsonObject.get("levelName").getAsString();
            List<CareerLevel> levels = getCareerLevelManager().getLevels();
            for (CareerLevel level : levels) {
                if (level.getLevelName().equals(levelName)) {
                    JsonArray array = new JsonArray();
                    for (String levelPlayer : level.getPlayers()) {
                        array.add(levelPlayer);
                    }
                    retJsonObject.addProperty("action", "ArchCareer.Level.getLevelPlayers");
                    retJsonObject.add("players", array);
                    player.sendPluginMessage(getInstance(), channel, gson.toJson(retJsonObject).getBytes(StandardCharsets.UTF_8));
                }
            }
        } else if (action.equalsIgnoreCase("ArchCareer.Level.getPlayerLevelName")) {
            // 获取玩家的职业等级名称
            String playerName = jsonObject.get("playerName").getAsString();
            List<CareerLevel> levels = getCareerLevelManager().getLevels();
            for (CareerLevel level : levels) {
                for (String levelPlayer : level.getPlayers()) {
                    if (levelPlayer.equals(playerName)) {
                        retJsonObject.addProperty("action", "ArchCareer.Level.getPlayerLevelName");
                        retJsonObject.addProperty("levelName", level.getLevelName());
                        player.sendPluginMessage(getInstance(), channel, gson.toJson(retJsonObject).getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        } else if (action.equalsIgnoreCase("ArchCareer.Level.getPlayerLevelTitle")) {
            // 获取玩家的职业等级标题
            String playerName = jsonObject.get("playerName").getAsString();
            List<CareerLevel> levels = getCareerLevelManager().getLevels();
            for (CareerLevel level : levels) {
                for (String levelPlayer : level.getPlayers()) {
                    if (levelPlayer.equals(playerName)) {
                        retJsonObject.addProperty("action", "ArchCareer.Level.getPlayerLevelTitle");
                        retJsonObject.addProperty("levelName", ChatColor.translateAlternateColorCodes(
                                '&',
                                level.getLevelTitle()
                        ));
                        player.sendPluginMessage(getInstance(), channel, gson.toJson(retJsonObject).getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }
}
