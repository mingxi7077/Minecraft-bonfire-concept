package com.zeeyeh.archcareer.command;

import cn.hutool.core.util.IdUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.utils.MessageUtil;
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

public class ArchCareerCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Set<PermissionAttachmentInfo> effectivePermissions = player.getEffectivePermissions();
            boolean isRun = false;
            for (PermissionAttachmentInfo info : effectivePermissions) {
                if (info.getPermission().equalsIgnoreCase("archcareer.admin")) {
                    isRun = true;
                    break;
                }
            }
            if (!isRun) {
                MessageUtil.sendMessage(sender, "&4&l你没有权限这么做");
                return true;
            }
        }
        if (args.length == 0) {
            // 帮助
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&m&l__________");
            MessageUtil.sendMessage(sender, "&f/&barchcareer                 &a- 显示插件帮助");
            MessageUtil.sendMessage(sender, "&f/&barchcareer &6list              &a- 列举所有职业");
            MessageUtil.sendMessage(sender, "&f/&barchcareer &6create &c<name>    &a- 创建一个职业");
            MessageUtil.sendMessage(sender, "&f/&barchcareer &6remove &c<name>    &a- 删除一个职业");
            MessageUtil.sendMessage(sender, "&f/&barchcareer &6clear            &a- 清空当前所有职业");
            MessageUtil.sendMessage(sender, "&f/&barchcareer &6reload           &a- 重载插件");
            MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&n&l----------");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            // 列举
            List<Career> careers = ArchCareer.getInstance().getCareerManager().getCareers();
            int pageLimit = 10;
            int pages = (careers.size() / 10) + (careers.size() % pageLimit == 0 ? 0 : 1);
            if (args.length == 2) {
                int pageNumber = Integer.parseInt(args[1]);
                if (pageNumber > pages) {
                    MessageUtil.sendMessage(sender, "&4&l未知页码 &e&l" + pageNumber);
                    return true;
                }
                int pageFirstIndex = pageNumber * pageLimit;
                if (pageFirstIndex > careers.size()) {
                    MessageUtil.sendMessage(sender, "&4&l未知索引 &e&l" + pageFirstIndex);
                    return true;
                }
                List<Career> pageCareers = careers.subList(pageFirstIndex, pageFirstIndex + pageLimit);
                int i = pageFirstIndex;
                MessageUtil.sendMessage(sender, "");
                MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&l第&e&l" + pageNumber + "&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&m&l__________");
                for (Career career : pageCareers) {
                    MessageUtil.sendMessage(sender, i + "&b&l名称:&a&l " + career.getName() + "&f&l,&b&l标题:&a&l " + career.getTitle() + "&f&l,&b&l目前已有玩家数: &c&l" + career.getPlayerName().size());
                    i++;
                }
                MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&l第&e&l" + pageNumber + "&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&n&l----------");
            }
            int i = 1;
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&l第&e&l1&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&m&l__________");
            MessageUtil.sendMessage(sender, "");
            List<Career> pageCareers;
            if (careers.size() > 10) {
                pageCareers = careers.subList(0, 10);
            } else {
                pageCareers = careers;
            }
            for (Career career : pageCareers) {
                MessageUtil.sendMessage(sender, i + "&b&l名称:&a&l " + career.getName() + "&f&l,&b&l标题:&a&l " + career.getTitle() + "&f&l,&b&l目前已有玩家数: &c&l" + career.getPlayerName().size());
                i++;
            }
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&l第&e&l1&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页  &a&n&l----------");
        } else if (args[0].equalsIgnoreCase("create")) {
            // 创建
            Map<String, Object> map = new HashMap<>();
            long id = IdUtil.getSnowflakeNextId();
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /archcareer create <name> <title>");
                return true;
            }
            String name = args[1];
            String title = args[2];
            map.put("career", new HashMap<String, Object>() {
                {
                    put("id", id);
                    put("name", name);
                    put("title", title);
                    put("players", new ArrayList<>());
                }
            });
            String careerFolder = ArchCareer.getInstance().getConfigManager().getConfig("config").getString("path");
            File file = new File(new File(ArchCareer.getInstance().getDataFolder(), careerFolder), name + ".yml");
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
        } else if (args[0].equalsIgnoreCase("remove")) {
            // 删除
            String name = args[1];
            ArchCareer.getInstance().getCareerManager().unregisterCareer(name);
            String careerFolder = ArchCareer.getInstance().getConfigManager().getConfig("config").getString("path");
            if (careerFolder != null) {
                File folder = new File(ArchCareer.getInstance().getDataFolder(), careerFolder);
                File careerConfigFile = new File(folder, name + ".yml");
                if (careerConfigFile.exists()) {
                    careerConfigFile.delete();
                }
            }
            MessageUtil.sendMessage(sender, "&a&l职业删除成功");
        } else if (args[0].equalsIgnoreCase("clear")) {
            // 清空
            if (args.length != 2) {
                MessageUtil.sendMessage(sender, "&4&l出于安全考虑，请重新键入 &e&l/archcareer clear confirm &4&l指令继续下一步");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            // 重载
            ArchCareer.getInstance().getConfigManager().reload();
            ArchCareer.getInstance().getCareerManager().clear();
            ArchCareer.getInstance().initCareers();
            MessageUtil.sendMessage(sender, "&e&l插件重载完毕");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "create", "remove", "clear", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<Career> careers = ArchCareer.getInstance().getCareerManager().getCareers();
            List<String> careerNames = new ArrayList<>();
            for (Career career : careers) {
                careerNames.add(career.getName());
            }
            return careerNames;
        }
        return new ArrayList<>();
    }
}
