package com.zeeyeh.archcareer;

import cn.hutool.core.io.FileUtil;
import com.zeeyeh.archcareer.api.ArchCareerLangApi;
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
            careerManager = new CareerManagerProvider();
            configManager = new ConfigManagerProvider();
            String careersFolderPath = configManager.getConfig("config").getString("path");
            if (careersFolderPath == null) {
                MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLangApi.translate("careersFolderPathError"));
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
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLangApi.translate("pluginLoaded"));
        }
    }

    public void initCareers() {
        String dataPath = ArchCareer.getInstance().getConfigManager().getConfig("config").getString("path");
        List<File> careerFiles = FileUtil.loopFiles(new File(getDataFolder(), dataPath), file -> file.getName().endsWith(".yml"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLangApi.translate("loadCareers"));
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
                    MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLangApi.translate("loadedCareerSuccessfully").replace("{0}", careerFile.getName()));
                    List<String> players = careerSection.getStringList("players");
                    ArchCareer.getInstance().getCareerManager().registerCareer(id, name, title, players);
                } catch (Exception e) {
                    MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLangApi.translate("loadedCareerException").replace("{0}", careerFile.getName()));
                    e.printStackTrace();
                }
            }
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ArchCareerLangApi.translate("loadedCareers"));
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
