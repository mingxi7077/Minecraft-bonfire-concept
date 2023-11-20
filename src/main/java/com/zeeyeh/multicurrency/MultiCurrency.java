package com.zeeyeh.multicurrency;

import com.zeeyeh.multicurrency.api.CurrencyManager;
import com.zeeyeh.multicurrency.command.MultiCurrencyCommand;
import com.zeeyeh.multicurrency.manager.ConfigManagerProvider;
import com.zeeyeh.multicurrency.manager.DatasourceManagerProvider;
import com.zeeyeh.multicurrency.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MultiCurrency extends JavaPlugin {

    private static MultiCurrency instance;
    private ConfigManagerProvider configManager;
    private DatasourceManagerProvider datasourceManagerProvider;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        if (getConfig().getBoolean("enabled")) {
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l插件正在加载中...");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b=================================================================");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b    __  ___      ____  _ ______");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b   /  |/  /_  __/ / /_(_) ____/_  _______________  ____  _______  __");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b  / /|_/ / / / / / __/ / /   / / / / ___/ ___/ _ \\/ __ \\/ ___/ / / /");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b / /  / / /_/ / / /_/ / /___/ /_/ / /  / /  /  __/ / / / /__/ /_/ /");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b/_/  /_/\\__,_/_/\\__/_/\\____/\\__,_/_/  /_/   \\___/_/ /_/\\___/\\__, /");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "                                                           /____/");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&lBy:&b&n&l Zeeyeh Studio,&r&a&lWebsite:&e&n&l https://www.zeeyeh.com/");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&b=================================================================");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l正在加载配置...");
            configManager = new ConfigManagerProvider();
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l配置加载完毕");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l正在加载货币...");
            datasourceManagerProvider = new DatasourceManagerProvider();
            CurrencyManager.initDatasourceManagerProvider(getDatasourceManagerProvider());
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a&l货币加载完毕");
            MultiCurrencyCommand multiCurrencyCommand = new MultiCurrencyCommand();
            Objects.requireNonNull(getCommand("multicurrency")).setExecutor(multiCurrencyCommand);
            Objects.requireNonNull(getCommand("multicurrency")).setTabCompleter(multiCurrencyCommand);
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a插件定制，问题咨询、反馈&cQQ:&e&n&l3615331065");
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&a插件加载完毕");
        }
    }

    public static MultiCurrency getInstance() {
        return instance;
    }

    public ConfigManagerProvider getConfigManager() {
        return configManager;
    }

    public DatasourceManagerProvider getDatasourceManagerProvider() {
        return datasourceManagerProvider;
    }

    @Override
    public void onDisable() {
    }
}
