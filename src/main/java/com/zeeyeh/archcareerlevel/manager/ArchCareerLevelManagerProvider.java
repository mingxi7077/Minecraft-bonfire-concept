package com.zeeyeh.archcareerlevel.manager;


import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareerlevel.ArchCareerLevel;
import com.zeeyeh.archcareerlevel.api.ArchCareerLevelLangApi;
import com.zeeyeh.archcareerlevel.entity.CareerLevel;
import com.zeeyeh.archcareerlevel.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArchCareerLevelManagerProvider {
    private final List<CareerLevel> levels = new ArrayList<>();

    public ArchCareerLevelManagerProvider() {
        initLevels();
    }

    private void initLevels() {
        String path = ArchCareerLevel.getInstance().getConfigManager().getConfig("config").getString("path");
        File folder = new File(ArchCareerLevel.getInstance().getDataFolder(), path);
        List<File> levelFiles = FileUtil.loopFiles(folder);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLevelLangApi.translate("loadingArchCareerLevel"));
        for (File levelFile : levelFiles) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(levelFile);
            ConfigurationSection section = configuration.getConfigurationSection("level");
            long id = section.getLong("id");
            String name = section.getString("name");
            String title = section.getString("title");
            List<String> players = section.getStringList("players");
            levels.add(new CareerLevel(id, name, title, players));
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLevelLangApi.translate("loadArchCareerLevel").replace("{0}", levelFile.getName()));
        }
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLevelLangApi.translate("loadedArchCareerLevel"));
    }

    public boolean registerLevel(long id, String name, String title, List<String> players) {
        if (existLevel(name)) {
            return false;
        }
        levels.add(new CareerLevel(id, name, title, players));
        return true;
    }

    public boolean unregisterLevel(long id) {
        if (!existLevel(id)) {
            return false;
        }
        int i = 0;
        for (CareerLevel level : levels) {
            if (level.getLevelId() == id) {
                levels.remove(i);
            }
            i++;
        }
        return true;
    }

    public boolean unregisterLevel(String name) {
        if (!existLevel(name)) {
            return false;
        }
        int i = 0;
        for (CareerLevel level : levels) {
            if (level.getLevelName().equals(name)) {
                levels.remove(i);
            }
            i++;
        }
        return true;
    }

    public void clear() {
        levels.clear();
    }

    public void reload() {
        clear();
        initLevels();
    }

    public boolean existLevel(long id) {
        for (CareerLevel level : levels) {
            if (level.getLevelId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean addPlayer(String playerName, String levelName, CommandSender sender) {
        if (existPlayer(playerName, levelName)) {
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("alreadyInLevel"));
            return false;
        }
        for (CareerLevel level : levels) {
            if (level.getLevelName().equals(levelName)) {
                level.getPlayers().add(playerName);
                return saveNewLevelConfigtoFile(sender, level);
            }
        }
        return false;
    }

    public boolean removePlayer(String playerName, String levelName, CommandSender sender) {
        if (!existPlayer(playerName, levelName)) {
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("cloudFindInLevel"));
            return false;
        }
        for (CareerLevel level : levels) {
            if (level.getLevelName().equals(levelName)) {
                level.getPlayers().remove(playerName);
                return saveNewLevelConfigtoFile(sender, level);
            }
        }
        return false;
    }

    private boolean saveNewLevelConfigtoFile(CommandSender sender, CareerLevel level) {
        String careerLevelFolder = ArchCareer.getInstance().getConfigManager().getConfig("config").getString("path");
        try {
            YamlUtil.dump(level, new FileWriter(new File( new File(ArchCareerLevel.getInstance().getDataFolder(), careerLevelFolder),level.getLevelName() + ".yml")));
        } catch (IOException e) {
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("settingPlayerLevelError"));
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean existPlayer(String playerName, String levelName) {
        for (CareerLevel level : levels) {
            if (level.getLevelName().equals(levelName)) {
                List<String> players = level.getPlayers();
                if (players.contains(playerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<CareerLevel> getLevels() {
        return levels;
    }

    public boolean existLevel(String name) {
        for (CareerLevel level : levels) {
            if (level.getLevelName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
