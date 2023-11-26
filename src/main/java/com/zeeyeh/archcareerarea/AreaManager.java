package com.zeeyeh.archcareerarea;

import java.util.*;

public class AreaManager {
    private final Map<String, List<String>> areas;

    public AreaManager() {
        areas = new HashMap<>();
        load();
    }

    public void load() {
        List<String> areaLines = ArchCareerArea.getInstance().getConfig().getStringList("areas");
        for (String line : areaLines) {
            if (line.contains(":")) {
                String[] configs = line.split(":");
                String areaName = configs[0];
                String levelsString = configs[1];
                String[] levels;
                if (levelsString.contains("|")) {
                    levels = levelsString.split("\\|");
                } else {
                    levels = new String[]{levelsString};
                }
                areas.put(areaName, Arrays.asList(levels));
            }
        }
    }

    public void clear() {
        areas.clear();
    }

    public void reload() {
        clear();
        load();
    }

    public List<String> getAreaAllow(String name) {
        if (!hasAreaName(name)) {
            return new ArrayList<>();
        }
        return areas.get(name);
    }

    public boolean hasAreaName(String name) {
        return areas.containsKey(name);
    }
}
