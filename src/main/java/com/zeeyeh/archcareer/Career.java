package com.zeeyeh.archcareer;

import java.util.List;
import java.util.Set;

public class Career {
    private long id;
    private String name;
    private String title;
    private List<String> playerName;

    public Career(long id, String name, String title, List<String> playerName) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.playerName = playerName;
    }

    public long getId() {
        return id;
    }

    public Career setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Career setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Career setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<String> getPlayerName() {
        return playerName;
    }

    public Career setPlayerName(List<String> playerName) {
        this.playerName = playerName;
        return this;
    }
}
