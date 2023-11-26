package com.zeeyeh.archcareer.command;

import cn.hutool.core.util.IdUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.api.ArchCareerLangApi;
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
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("noPermission"));
            return true;
        }
        if (args.length == 0) {
            // 帮助
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_0"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_1"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_2"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_3"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_4"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_5"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_6"));
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("helpCommand_7"));
            MessageUtil.sendMessage(sender, "");
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
                    MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("notPageNumber").replace("{0}", String.valueOf(pageNumber)));
                    return true;
                }
                int pageFirstIndex = pageNumber * pageLimit;
                if (pageFirstIndex > careers.size()) {
                    MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("notPageNumber").replace("{0}", String.valueOf(pageFirstIndex)));
                    return true;
                }
                List<Career> pageCareers = careers.subList(pageFirstIndex, pageFirstIndex + pageLimit);
                int i = pageFirstIndex;
                MessageUtil.sendMessage(sender, "");
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("pageHeadFoot")
                        .replace("{0}", String.valueOf(pageNumber))
                        .replace("{1}", String.valueOf(pages))
                );
                for (Career career : pageCareers) {
                    MessageUtil.sendMessage(sender, i + ". " + ArchCareerLangApi.translate("pageLine")
                            .replace("{0}", career.getName())
                            .replace("{1}", career.getTitle())
                            .replace("{2}", String.valueOf(career.getPlayerName().size()))
                    );
                    i++;
                }
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("pageHeadFoot")
                        .replace("{0}", String.valueOf(pageNumber))
                        .replace("{1}", String.valueOf(pages))
                );
            }
            int i = 1;
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("pageHeadFoot")
                    .replace("{0}", String.valueOf(pages))
            );
            MessageUtil.sendMessage(sender, "");
            List<Career> pageCareers;
            if (careers.size() > 10) {
                pageCareers = careers.subList(0, 10);
            } else {
                pageCareers = careers;
            }
            for (Career career : pageCareers) {
                MessageUtil.sendMessage(sender, i + ". " + ArchCareerLangApi.translate("pageLine")
                        .replace("{0}", career.getName())
                        .replace("{1}", career.getTitle())
                        .replace("{2}", String.valueOf(career.getPlayerName().size()))
                );
                i++;
            }
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("pageHeadFoot")
                    .replace("{0}", String.valueOf(pages))
            );
        } else if (args[0].equalsIgnoreCase("create")) {
            // 创建
            Map<String, Object> map = new HashMap<>();
            long id = IdUtil.getSnowflakeNextId();
            if (args.length != 3) {
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("formatError")
                        .replace("{0}", "/archcareer create <name> <title>"));
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
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("careerCreateSuccessfully"));
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("careerCreateSuccessfully"));
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("careerCreateError"));
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
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("careerRemoveSuccessfully"));
        } else if (args[0].equalsIgnoreCase("clear")) {
            // 清空
            if (args.length != 2) {
                MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("careerClearConfirm"));
                return true;
            }
            if (args[1].equals("confirm")) {
                List<Career> careers = ArchCareer.getInstance().getCareerManager().getCareers();
                for (Career career : careers) {
                    File file = new File(new File(ArchCareer.getInstance().getDataFolder(), "careers"), career.getName() + ".yml");
                    if (!file.exists()) {
                        file.delete();
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            // 重载
            ArchCareer.getInstance().getConfigManager().reload();
            ArchCareer.getInstance().getCareerManager().clear();
            ArchCareer.getInstance().initCareers();
            MessageUtil.sendMessage(sender, ArchCareerLangApi.translate("careerReload"));
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
