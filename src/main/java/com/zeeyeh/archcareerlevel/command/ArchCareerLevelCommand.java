package com.zeeyeh.archcareerlevel.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.utils.MessageUtil;
import com.zeeyeh.archcareerlevel.ArchCareerLevel;
import com.zeeyeh.archcareerlevel.api.ArchCareerLevelLangApi;
import com.zeeyeh.archcareerlevel.entity.CareerLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ArchCareerLevelCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Set<PermissionAttachmentInfo> effectivePermissions = sender.getEffectivePermissions();
        boolean hasRun = false;
        for (PermissionAttachmentInfo effectivePermission : effectivePermissions) {
            if (effectivePermission.getPermission().equalsIgnoreCase("ArchCareerLevel.admin")) {
                hasRun = true;
            }
        }
        if (!hasRun) {
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("noPermission"));
            return true;
        }
        if (args.length == 0) {
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_0"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_1"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_2"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_3"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_4"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_5"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_6"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_7"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_8"));
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpCommand_9"));
            return true;
        }
        if (args[0].equalsIgnoreCase("list"))
        {
            List<CareerLevel> levels = ArchCareerLevel.getInstance().getCareerLevelManager().getLevels();
            int pageLimit = 10;
            int pages = (levels.size() / 10) + (levels.size() % pageLimit == 0 ? 0 : 1);
            if (args.length == 2) {
                int pageNumber = Integer.parseInt(args[1]);
                if (pageNumber > pages) {
                    MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("notPageNumber")
                            .replace("{0}", String.valueOf(pageNumber)));
                    return true;
                }
                int pageFirstIndex = pageNumber * pageLimit;
                if (pageFirstIndex > levels.size()) {
                    MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("notPageNumber")
                            .replace("{0}", String.valueOf(pageFirstIndex)));
                    return true;
                }
                List<CareerLevel> pageLevels = levels.subList(pageFirstIndex, pageFirstIndex + pageLimit);
                int i = pageFirstIndex;
                MessageUtil.sendMessage(sender, "");
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpHeadFoot")
                        .replace("{0}", String.valueOf(pageNumber))
                        .replace("{1}", String.valueOf(pages)));
                for (CareerLevel level : pageLevels) {
                    MessageUtil.sendMessage(sender, i + ". " +
                            ArchCareerLevelLangApi.translate("helpLine")
                                    .replace("{0}", level.getLevelName())
                                    .replace("{1}", level.getLevelTitle())
                                    .replace("{2}", String.valueOf(level.getPlayers().size()))
                    );
                    i++;
                }
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("helpHeadFoot")
                        .replace("{0}", String.valueOf(pageNumber))
                        .replace("{1}", String.valueOf(pages)));
            }
            int i = 1;
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("oneHelpHeadFoot")
                    .replace("{0}", String.valueOf(pages)));
            MessageUtil.sendMessage(sender, "");
            List<CareerLevel> pageLevels;
            if (levels.size() > 10) {
                pageLevels = levels.subList(0, 10);
            } else {
                pageLevels = levels;
            }
            for (CareerLevel level : pageLevels) {
                MessageUtil.sendMessage(sender, i + ". " +
                        ArchCareerLevelLangApi.translate("helpLine")
                                .replace("{0}", level.getLevelName())
                                .replace("{1}", level.getLevelTitle())
                                .replace("{2}", String.valueOf(level.getPlayers().size()))
                );
                i++;
            }
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("oneHelpHeadFoot")
                    .replace("{0}", String.valueOf(pages)));
        } else if (args[0].equalsIgnoreCase("create"))
        {
            Map<String, Object> map = new HashMap<>();
            long id = IdUtil.getSnowflakeNextId();
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("formatError")
                        .replace("{0}", "/archcareerlevel create <name> <title>"));
                return true;
            }
            String name = args[1];
            String title = args[2];
            map.put("level", new HashMap<String, Object>() {
                {
                    put("id", id);
                    put("name", name);
                    put("title", title);
                    put("players", new ArrayList<>());
                }
            });
            String careerFolder = ArchCareerLevel.getInstance().getConfigManager().getConfig("config").getString("path");
            File file = new File(new File(ArchCareerLevel.getInstance().getDataFolder(), careerFolder), name + ".yml");
            try {
                if (!file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                YamlUtil.dump(map, new FileWriter(file));
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("careerCreatedSuccessfully"));
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("careerCreatedError"));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("remove"))
        {

            String name = args[1];
            ArchCareerLevel.getInstance().getCareerLevelManager().unregisterLevel(name);
            String careerLevelFolder = ArchCareerLevel.getInstance().getConfigManager().getConfig("config").getString("path");
            if (careerLevelFolder != null) {
                File folder = new File(ArchCareerLevel.getInstance().getDataFolder(), careerLevelFolder);
                File careerConfigFile = new File(folder, name + ".yml");
                if (careerConfigFile.exists()) {
                    careerConfigFile.delete();
                }
            }
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("careerDeleteSuccessfully"));
        }
        else if (args[0].equalsIgnoreCase("join"))
        {
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("formatError")
                        .replace("{0}", "/archcareerlevel join <playerName> <levelName>"));
                return true;
            }
            String playerName = args[1];
            String levelName = args[2];
            ArchCareerLevel.getInstance().getCareerLevelManager().addPlayer(playerName, levelName, sender);
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("levelUpping")
                    .replace("{0}", playerName)
                    .replace("{1}", levelName));
        }else if (args[0].equalsIgnoreCase("delete"))
        {
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("formatError")
                        .replace("{0}", "/archcareerlevel delete <playerName> <levelName>"));
                return true;
            }
            String playerName = args[1];
            String levelName = args[2];
            ArchCareerLevel.getInstance().getCareerLevelManager().removePlayer(playerName, levelName, sender);
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("levelDowning")
                    .replace("{0}", playerName)
                    .replace("{1}", levelName));
        } else if (args[0].equalsIgnoreCase("clear"))
        {
            if (args.length != 2) {
                MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("commandConfirm"));
                return true;
            }
            if (args[1].equalsIgnoreCase("confirm")) {
                ArchCareerLevel.getInstance().getCareerLevelManager().clear();
            }
            String careerLevelFolder = ArchCareerLevel.getInstance().getConfigManager().getConfig("config").getString("path");
            List<File> files = FileUtil.loopFiles(new File(ArchCareerLevel.getInstance().getDataFolder(), careerLevelFolder));
            for (File file : files) {
                if (file.exists()) {
                    file.delete();
                }
            }
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("careerClearSuccessfully"));
        } else if (args[0].equalsIgnoreCase("reload"))
        {
            ArchCareerLevel.getInstance().getConfigManager().reload();
            ArchCareerLevel.getInstance().getCareerLevelManager().reload();
            ArchCareerLevel.getInstance().reloadConfig();
            MessageUtil.sendMessage(sender, ArchCareerLevelLangApi.translate("careerReloadSuccessfully"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "create", "remove", "join", "delete", "clear", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<CareerLevel> levels = ArchCareerLevel.getInstance().getCareerLevelManager().getLevels();
            List<String> careerLevelNames = new ArrayList<>();
            for (CareerLevel level : levels) {
                careerLevelNames.add(level.getLevelName());
            }
            return careerLevelNames;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("delete")) {
            List<String> players = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                players.add(onlinePlayer.getName());
            }
            return players;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("delete")) {
            List<String> levels = new ArrayList<>();
            List<CareerLevel> hasLevels = ArchCareerLevel.getInstance().getCareerLevelManager().getLevels();
            for (CareerLevel level : hasLevels) {
                levels.add(level.getLevelName());
            }
            return levels;
        }
        return new ArrayList<>();
    }
}
