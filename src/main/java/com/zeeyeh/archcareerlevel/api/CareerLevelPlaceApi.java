package com.zeeyeh.archcareerlevel.api;

import java.util.List;

import com.zeeyeh.archcareerlevel.ArchCareerLevel;
import com.zeeyeh.archcareerlevel.entity.CareerLevel;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CareerLevelPlaceApi extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "archcareerlevel";
    }

    @Override
    public @NotNull String getAuthor() {
        return "LingQi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1.2";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String name = player.getName();
        if (name == null) {
            return null;
        }
        if (params.equalsIgnoreCase("name")) {
            List<CareerLevel> levels = ArchCareerLevel.getInstance().getCareerLevelManager().getLevels();
            for (CareerLevel level : levels) {
                List<String> players = level.getPlayers();
                for (String playerName : players) {
                    if (name.equals(playerName)) {
                        return level.getLevelName();
                    }
                }
            }
        }
        if (params.equalsIgnoreCase("title")) {
            List<CareerLevel> levels = ArchCareerLevel.getInstance().getCareerLevelManager().getLevels();
            for (CareerLevel level : levels) {
                List<String> players = level.getPlayers();
                for (String playerName : players) {
                    if (name.equals(playerName)) {
                        return level.getLevelTitle();
                    }
                }
            }
        }
        if (params.equalsIgnoreCase("id")) {
            List<CareerLevel> levels = ArchCareerLevel.getInstance().getCareerLevelManager().getLevels();
            for (CareerLevel level : levels) {
                List<String> players = level.getPlayers();
                for (String playerName : players) {
                    if (name.equals(playerName)) {
                        return String.valueOf(level.getLevelId());
                    }
                }
            }
        }
        return null;
    }
}
