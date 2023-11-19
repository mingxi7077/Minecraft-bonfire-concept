package com.zeeyeh.archcareer;

import cn.hutool.core.io.FileUtil;
import com.zeeyeh.archcareer.api.CareerManager;
import com.zeeyeh.archcareer.command.ArchCareerCommand;
import com.zeeyeh.archcareer.manager.CareerManagerProvider;
import com.zeeyeh.archcareer.manager.ConfigManagerProvider;
import com.zeeyeh.archcareer.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public final class ArchCareer extends JavaPlugin {

    private static ArchCareer instance;
    private CareerManagerProvider careerManager;
    private ConfigManagerProvider configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        if (getConfig().getBoolean("enabled")) {
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l插件正在加载中...");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b=================================================================");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b    ___              __    ______");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b   /   |  __________/ /_  / ____/___ _________  ___  _____");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b  / /| | / ___/ ___/ __ \\/ /   / __ `/ ___/ _ \\/ _ \\/ ___/");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b / ___ |/ /  / /__/ / / / /___/ /_/ / /  /  __/  __/ /   ");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b/_/  |_/_/   \\___/_/ /_/\\____/\\__,_/_/   \\___/\\___/_/");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&lBy:&b&n&l Zeeyeh Studio,&r&a&lWebsite:&e&n&l https://www.zeeyeh.com/");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b=================================================================");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            careerManager = new CareerManagerProvider();
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l正在加载配置...");
            configManager = new ConfigManagerProvider();
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l配置加载完毕");
            String careersFolderPath = configManager.getConfig("config").getString("path");
            if (careersFolderPath == null) {
                MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&4职业目录路径配置错误");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            File careersFolder = new File(getDataFolder(), careersFolderPath);
            if (!careersFolder.exists()) {
                //noinspection ResultOfMethodCallIgnored
                careersFolder.mkdirs();
            }
            initCareers();
            CareerManager.initCareerManagerProvider(getCareerManager());
            ArchCareerCommand archCareerCommand = new ArchCareerCommand();
            Objects.requireNonNull(getCommand("archcareer")).setExecutor(archCareerCommand);
            Objects.requireNonNull(getCommand("archcareer")).setTabCompleter(archCareerCommand);
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a插件定制，问题咨询、反馈&cQQ:&e&n&l3615331065");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a插件加载完毕");
        }
    }

    public void initCareers() {
        String dataPath = ArchCareer.getInstance().getConfigManager().getConfig("config").getString("path");
        List<File> careerFiles = FileUtil.loopFiles(new File(getDataFolder(), dataPath), file -> file.getName().endsWith(".yml"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l正在加载职业");
            for (File careerFile : careerFiles) {
                try {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(careerFile);
                    ConfigurationSection careerSection = configuration.getConfigurationSection("career");
                    if (careerSection == null) {
                        continue;
                    }
                    long id = careerSection.getLong("id");
                    String name = careerSection.getString("name");
                    String title = careerSection.getString("title");
                    MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l职业: " + careerFile.getName() + " 加载成功");
                    List<String> players = careerSection.getStringList("players");
                    ArchCareer.getInstance().getCareerManager().registerCareer(id, name, title, players);
                } catch (Exception e) {
                    MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&4&l职业: " + careerFile.getName() + " 加载失败");
                    e.printStackTrace();
                }
            }
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l职业加载完毕");
    }

    public static ArchCareer getInstance() {
        return instance;
    }

    public CareerManagerProvider getCareerManager() {
        return careerManager;
    }

    public ConfigManagerProvider getConfigManager() {
        return configManager;
    }

    @Override
    public void onDisable() {
    }
}
