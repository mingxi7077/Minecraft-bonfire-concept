package com.zeeyeh.infinitemining.entity;

import com.google.common.base.CaseFormat;

import java.util.ArrayList;
import java.util.List;

public enum CreatesSettingMap {
    POSITION("location"),
    TITLE("title"),
    XP("xp"),
    XP_LEVEL("xpLevel"),
    COMMAND("command"),
    MATERIAL("material"),
    INTERVAL("interval"),
    TIP("tip");
    private final static List<String> keyMaps;

    private final String key;

    static {
        keyMaps = new ArrayList<>();
        CreatesSettingMap[] values = CreatesSettingMap.values();
        for (CreatesSettingMap value : values) {
            keyMaps.add(serializeString(value.getKey()));
        }
    }

    CreatesSettingMap(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static CreatesSettingMap getKey(String key) {
        key = deserializeString(key);
        CreatesSettingMap[] values = CreatesSettingMap.values();
        for (CreatesSettingMap value : values) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }

    private static String serializeString(String key) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
    }

    private static String deserializeString(String key) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key);
    }

    public static List<String> listKeys() {
        return getKeyMaps();
    }

    private static List<String> getKeyMaps() {
        return keyMaps;
    }
}
