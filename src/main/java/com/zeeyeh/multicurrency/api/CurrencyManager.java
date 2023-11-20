package com.zeeyeh.multicurrency.api;

import cn.hutool.db.Entity;
import com.zeeyeh.multicurrency.manager.DatasourceManagerProvider;
import org.bukkit.command.CommandSender;

import java.time.OffsetDateTime;
import java.util.List;

public class CurrencyManager {
    private static DatasourceManagerProvider datasourceManager;

    public static void initDatasourceManagerProvider(DatasourceManagerProvider datasourceManagerProvider) {
        datasourceManager = datasourceManagerProvider;
    }

    public static boolean createCurrency(String name) {
        return datasourceManager.createCurrencyType(name);
    }

    public static boolean removeCurrencyType(String name) {
        return datasourceManager.removeCurrencyType(name);
    }

    public static boolean removeCurrencyType(long id) {
        return datasourceManager.removeCurrencyType(id);
    }

    public static String getCurrencyName(long cid) {
        return datasourceManager.getCurrencyName(cid);
    }

    public static boolean clearCurrencyType() {
        return datasourceManager.clearCurrencyType();
    }

    public static List<Entity> listCurrencyType() {
        return datasourceManager.listCurrencyType();
    }

    public static List<Long> listCurrencyTypeCid() {
        return datasourceManager.listCurrencyTypeCid();
    }

    public static Long getCurrencyCid(String name) {
        return datasourceManager.getCurrencyCid(name);
    }

    public static boolean giveCurrency(String playerName, long cid, int count) {
        return datasourceManager.giveCurrency(playerName, cid, count);
    }

    public static boolean takeCurrency(String playerName, long cid, int count, CommandSender sender) {
        return datasourceManager.takeCurrency(playerName, cid, count, sender);
    }

    public static boolean setCurrency(String playerName, long cid, int count) {
        return datasourceManager.setCurrency(playerName, cid, count);
    }

    public static List<Entity> getCurrency(String playerName) {
        return datasourceManager.getCurrency(playerName);
    }
}
