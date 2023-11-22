package com.zeeyeh.archcareerlevel.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.utils.MessageUtil;
import com.zeeyeh.archcareerlevel.ArchCareerLevel;
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
            MessageUtil.sendMessage(sender, "&4&l你没有权限这么做");
            return true;
        }
        if (args.length == 0) {
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&m&l__________");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel                 &a- 显示插件帮助");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6list              &a- 列举所有职业等级");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6create     &a- 创建一个职业等级");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6remove     &a- 删除一个职业等级");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6join     &a- 将玩家添加到指定的职业等级");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6delete     &a- 将玩家从指定的职业等级中删除");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6clear            &a- 清空当前所有职业等级");
            MessageUtil.sendMessage(sender, "&f/&barchcareerlevel &6reload           &a- 重载插件");
            MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&n&l----------");
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
                    MessageUtil.sendMessage(sender, "&4&l未知页码 &e&l" + pageNumber);
                    return true;
                }
                int pageFirstIndex = pageNumber * pageLimit;
                if (pageFirstIndex > levels.size()) {
                    MessageUtil.sendMessage(sender, "&4&l未知索引 &e&l" + pageFirstIndex);
                    return true;
                }
                List<CareerLevel> pageLevels = levels.subList(pageFirstIndex, pageFirstIndex + pageLimit);
                int i = pageFirstIndex;
                MessageUtil.sendMessage(sender, "");
                MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareerLevel&b] &a&l第&e&l" + pageNumber + "&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&m&l__________");
                for (CareerLevel level : pageLevels) {
                    MessageUtil.sendMessage(sender, i + "&b&l名称:&a&l " + level.getLevelName() + "&f&l,&b&l标题:&a&l " + level.getLevelTitle() + "&f&l,&b&l目前已有玩家数: &c&l" + level.getPlayers().size());
                    i++;
                }
                MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareerLevel&b] &a&l第&e&l" + pageNumber + "&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&n&l----------");
            }
            int i = 1;
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&l第&e&l1&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&m&l__________");
            MessageUtil.sendMessage(sender, "");
            List<CareerLevel> pageLevels;
            if (levels.size() > 10) {
                pageLevels = levels.subList(0, 10);
            } else {
                pageLevels = levels;
            }
            for (CareerLevel level : pageLevels) {
                MessageUtil.sendMessage(sender, i + ". &b&l名称:&a&l " + level.getLevelName() + "&f&l,&b&l标题:&a&l " + level.getLevelTitle() + "&f&l,&b&l目前已有玩家数: &c&l" + level.getPlayers().size());
                i++;
            }
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&l第&e&l1&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页  &a&n&l----------");
        } else if (args[0].equalsIgnoreCase("create"))
        {
            Map<String, Object> map = new HashMap<>();
            long id = IdUtil.getSnowflakeNextId();
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, ". &4&l参数有误,正确格式:&e&l /archcareerlevel create <name> <title>");
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
                MessageUtil.sendMessage(sender, "&a职业创建成功");
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.sendMessage(sender, "&4&l职业创建失败，请手动在数据目录中创建配置文件");
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
            MessageUtil.sendMessage(sender, "&a&l职业删除成功");
        }
        else if (args[0].equalsIgnoreCase("join"))
        {
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /archcareerlevel join <playerName> <levelName>");
                return true;
            }
            String playerName = args[1];
            String levelName = args[2];
            ArchCareerLevel.getInstance().getCareerLevelManager().addPlayer(playerName, levelName, sender);
            MessageUtil.sendMessage(sender, "&a&l玩家&e&l" + playerName + "&a&l职业等级成功提示至&e&l" + levelName);
        }else if (args[0].equalsIgnoreCase("delete"))
        {
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /archcareerlevel delete <playerName> <levelName>");
                return true;
            }
            String playerName = args[1];
            String levelName = args[2];
            ArchCareerLevel.getInstance().getCareerLevelManager().removePlayer(playerName, levelName, sender);
            MessageUtil.sendMessage(sender, "&a&l玩家&e&l" + playerName + "&a&l职业等级已不再为&e&l" + levelName);
        } else if (args[0].equalsIgnoreCase("clear"))
        {
            if (args.length != 2) {
                MessageUtil.sendMessage(sender, "&4&l出于安全考虑，请重新键入 &e&l/archcareerlevel clear confirm &4&l指令继续下一步");
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
            MessageUtil.sendMessage(sender, "&4&l职业等级清空完成");
        } else if (args[0].equalsIgnoreCase("reload"))
        {
            ArchCareerLevel.getInstance().getConfigManager().reload();
            ArchCareerLevel.getInstance().getCareerLevelManager().reload();
            ArchCareerLevel.getInstance().reloadConfig();
            MessageUtil.sendMessage(sender, "&e&l插件重载完毕");
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
