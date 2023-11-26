package com.zeeyeh.archcareerarea.listener;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.manager.CareerManagerProvider;
import com.zeeyeh.archcareerarea.ArchCareerArea;
import com.zeeyeh.archcareerarea.api.ArchCareerAreaLangApi;
import com.zeeyeh.archcareerarea.utils.MessageUtil;
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
        ClaimedResidence residence = ResidenceApi.getResidenceManager().getByLoc(event.getTo());
        if (residence == null) {
            return;
        }
        String areaName = residence.getName();
        List<String> areaAllow = ArchCareerArea.getInstance().getAreaManager().getAreaAllow(areaName);
        if (!hasPermissionJoin(
                areaAllow
                , playerName)) {
            player.teleport(event.getFrom());
            MessageUtil.send(event.getPlayer(), ArchCareerAreaLangApi.translate("notJoinArea")
                    .replace("{0}", listToStringBuilder(areaAllow)));
        }
    }

    public String listToStringBuilder(List<String> areaAllow) {
        StringBuilder builder = new StringBuilder();
        for (String allow : areaAllow) {
            builder.append(allow).append(",");
        }
        String builderString = builder.toString();
        return builderString.substring(0, builderString.length() - 1);
    }

    public boolean hasPermissionJoin(List<String> areaAllow, String playerName) {
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
