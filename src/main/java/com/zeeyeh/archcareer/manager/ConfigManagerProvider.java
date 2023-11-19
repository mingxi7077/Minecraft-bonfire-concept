package com.zeeyeh.archcareer.manager;

import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManagerProvider {
    private final Map<String, Configuration> configurationMap;

    public ConfigManagerProvider() {
        configurationMap = new HashMap<>();
        initConfigs();
    }

    private void initConfigs() {
        File[] files = ArchCareer.getInstance().getDataFolder().listFiles(file -> file.getName().endsWith(".yml") && file.isFile());
        if (files == null) {
            return;
        }
        for (File file : files) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            configurationMap.put(file.getName().substring(0, file.getName().indexOf(".")), configuration);
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l成功读取配置: " + file.getName());
        }
    }

    public boolean registerConfig(String name) {
        if (existToConfigMap(name)) {
            return false;
        }
        File file = new File(ArchCareer.getInstance().getDataFolder(), name + ".yml");
        if (!file.exists()) {
            return false;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configurationMap.put(name, configuration);
        return true;
    }

    public boolean unregisterConfig(String name) {
        if (!existToConfigMap(name)) {
            return false;
        }
        configurationMap.remove(name);
        return true;
    }

    public boolean existToConfigMap(String name) {
        return configurationMap.containsKey(name);
    }

    public Configuration getConfig(String name) {
        if (!existToConfigMap(name)) {
            return null;
        }
        return configurationMap.get(name);
    }

    public void reload() {
        configurationMap.clear();
        initConfigs();
        registerConfig("config");
    }

    public void clear() {
        configurationMap.clear();
    }
}
