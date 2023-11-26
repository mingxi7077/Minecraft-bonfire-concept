package com.zeeyeh.archcareerarea.listener;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.manager.CareerManagerProvider;
import com.zeeyeh.archcareerarea.ArchCareerArea;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerArchListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (!hasPermissionJoin(
                event.getTo()
                , playerName)) {
            player.teleport(event.getFrom());
        }
    }

    public boolean hasPermissionJoin(Location location, String playerName) {
        ClaimedResidence residence = ResidenceApi.getResidenceManager().getByLoc(location);
        if (residence == null) {
            return true;
        }
        String areaName = residence.getName();
        List<String> areaAllow = ArchCareerArea.getInstance().getAreaManager().getAreaAllow(areaName);
        CareerManagerProvider careerManager = ArchCareerArea.getInstance().getArchCareer().getCareerManager();
        List<Career> careers = careerManager.getCareers();
        for (Career career : careers) {
            if (career.getPlayerName().contains(playerName)) {
                // 找到玩家所处职业信息
                if (areaAllow.contains(career.getName())) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
