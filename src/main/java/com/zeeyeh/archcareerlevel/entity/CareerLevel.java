package com.zeeyeh.archcareerlevel.entity;

import java.util.ArrayList;
import java.util.List;

public class CareerLevel {
    private long levelId;
    private String levelName;
    private String levelTitle;
    private List<String> players;

    public CareerLevel(long levelId, String levelName, String levelTitle) {
        this(levelId, levelName, levelTitle, new ArrayList<>());
    }

    public CareerLevel(long levelId, String levelName, String levelTitle, List<String> players) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.levelTitle = levelTitle;
        this.players = players;
    }

    public long getLevelId() {
        return levelId;
    }

    public CareerLevel setLevelId(long levelId) {
        this.levelId = levelId;
        return this;
    }

    public String getLevelName() {
        return levelName;
    }

    public CareerLevel setLevelName(String levelName) {
        this.levelName = levelName;
        return this;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public CareerLevel setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
        return this;
    }

    public List<String> getPlayers() {
        return players;
    }

    public CareerLevel setPlayers(List<String> players) {
        this.players = players;
        return this;
    }
}
