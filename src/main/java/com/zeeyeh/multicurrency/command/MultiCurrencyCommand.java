package com.zeeyeh.multicurrency.command;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.Entity;
import com.zeeyeh.multicurrency.MultiCurrency;
import com.zeeyeh.multicurrency.api.CurrencyManager;
import com.zeeyeh.multicurrency.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MultiCurrencyCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6MultiCurrency&b] &a&m&l__________");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency                    &a- 显示插件帮助");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency list                &a- 列举所有货币");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency create             &a- 创建一种货币");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency remove             &a- 删除一种货币");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency show               &a- 显示某个玩家的货币数量");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency give               &a- 给予玩家指定数量的目标货币");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency take               &a- 扣除玩家指定数量的目标货币");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency set                &a- 设置玩家指定数量的目标货币");
            MessageUtil.sendMessage(sender, "&f/&bmulticurrency reload             &a- 重载插件");
            MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&n&l----------");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            // 列举
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            List<Entity> entities = CurrencyManager.listCurrencyType();
            int pageLimit = 10;
            int pages = (entities.size() / pageLimit) + (entities.size() % pageLimit == 0 ? 0 : 1);
            if (args.length == 2) {
                int pageNumber = Integer.parseInt(args[1]);
                if (pageNumber > pages) {
                    MessageUtil.sendMessage(sender, "&4&l未知页码 &e&l" + pageNumber);
                    return true;
                }
                int pageFirstIndex = pageNumber * pageLimit;
                if (pageFirstIndex > entities.size()) {
                    MessageUtil.sendMessage(sender, "&4&l未知索引 &e&l" + pageFirstIndex);
                    return true;
                }
                List<Entity> pageEntities = entities.subList(pageFirstIndex, pageFirstIndex + pageLimit);
                int i = pageFirstIndex;
                MessageUtil.sendMessage(sender, "");
                MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&l第&e&l" + pageNumber + "&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&m&l__________");
                for (Entity pageEntity : pageEntities) {
                    MessageUtil.sendMessage(sender, i + ". &b&l名称:&a&l " + pageEntity.getStr("name"));
                    i++;
                }
                MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&l第&e&l" + pageNumber + "&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&n&l----------");
            }
            int i = 1;
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&m&l__________&r &b[&6ArchCareer&b] &a&l第&e&l1&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页 &a&m&l__________");
            MessageUtil.sendMessage(sender, "");
            List<Entity> pageEntities;
            if (entities.size() > pageLimit) {
                pageEntities = entities.subList(0, pageLimit);
            } else {
                pageEntities = entities;
            }
            for (Entity pageEntity : pageEntities) {
                MessageUtil.sendMessage(sender, i + ". &b&l名称:&a&l " + pageEntity.getStr("name"));
                i++;
            }
            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&a&n&l----------&r &b[&6ArchCareer&b] &a&l第&e&l1&a&l页&f&l/&a&l共&e&l" + pages + "&a&l页  &a&n&l----------");
        } else if (args[0].equalsIgnoreCase("create")) {
            // 创建
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            if (args.length != 2) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /multicurrency create <name>");
                return true;
            }
            String name = args[1];
            if (!CurrencyManager.createCurrency(name)) {
                MessageUtil.sendMessage(sender, "&4货币创建失败！");
                return true;
            }
            MessageUtil.sendMessage(sender, "&a货币创建成功！");
        } else if (args[0].equalsIgnoreCase("remove")) {
            // 删除
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            String arg = args[1];
            if (ReUtil.isMatch("[\\d]+", arg)) {
                long id = Long.parseLong(arg);
                if (!CurrencyManager.removeCurrencyType(id)) {
                    MessageUtil.sendMessage(sender, "&4货币删除失败");
                    return true;
                }
            } else {
                if (!CurrencyManager.removeCurrencyType(arg)) {
                    MessageUtil.sendMessage(sender, "&4货币删除失败");
                    return true;
                }
            }
            MessageUtil.sendMessage(sender, "&a货币删除成功");
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            if (!CurrencyManager.clearCurrencyType()) {
                MessageUtil.sendMessage(sender, "&4清空货币失败");
                return true;
            }
            MessageUtil.sendMessage(sender, "&a清空货币成功");
        } else if (args[0].equalsIgnoreCase("show")) {
            // 显示
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            if (args.length != 2) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /multicurrency show <name>");
                return true;
            }
            String playerName = args[1];
            List<Entity> currency = CurrencyManager.getCurrency(playerName);
            List<Long> cids = CurrencyManager.listCurrencyTypeCid();
            int i = 1;
            for (Entity entity : currency) {
                for (Long cid : cids) {
                    String name = CurrencyManager.getCurrencyName(cid);
                    Integer count = entity.getInt("count");
                    MessageUtil.sendMessage(sender, i + ". &b&l名称:&a&l " + name + ", &a&l数量:&c&l " + count);
                }
                i++;
            }
        } else if (args[0].equalsIgnoreCase("give")) {
            // 给予
            if (!hasPermission("MultiCurrency.basic", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            if (args.length != 4) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /multicurrency give <playerName> <currencyName> <count>");
                return true;
            }
            String playerName = args[1];
            String currencyName = args[2];
            Long cid = CurrencyManager.getCurrencyCid(currencyName);
            int count = Integer.parseInt(args[3]);
            if (!CurrencyManager.giveCurrency(playerName, cid, count)) {
                MessageUtil.sendMessage(sender, "&4货币给予失败");
                return true;
            }
            MessageUtil.sendMessage(sender, "&a货币给予成功");
        } else if (args[0].equalsIgnoreCase("take")) {
            // 扣除
            if (!hasPermission("MultiCurrency.basic", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            if (args.length != 4) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /multicurrency take <playerName> <currencyName> <count>");
                return true;
            }
            String playerName = args[1];
            String currencyName = args[2];
            Long cid = CurrencyManager.getCurrencyCid(currencyName);
            int count = Integer.parseInt(args[3]);
            if (!CurrencyManager.takeCurrency(playerName, cid, count, sender)) {
                MessageUtil.sendMessage(sender, "&4货币扣除失败");
                return true;
            }
            MessageUtil.sendMessage(sender, "&a货币扣除成功");
        } else if (args[0].equalsIgnoreCase("set")) {
            // 设置
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            if (args.length != 4) {
                MessageUtil.sendMessage(sender, "&4&l参数有误,正确格式:&e&l /multicurrency set <playerName> <currencyName> <count>");
                return true;
            }
            String playerName = args[1];
            String currencyName = args[2];
            Long cid = CurrencyManager.getCurrencyCid(currencyName);
            int count = Integer.parseInt(args[3]);
            if (!CurrencyManager.setCurrency(playerName, cid, count)) {
                MessageUtil.sendMessage(sender, "&4货币设置失败");
                return true;
            }
            MessageUtil.sendMessage(sender, "&a货币设置成功");
        } else if (args[0].equalsIgnoreCase("reload")) {
            // 重载
            if (!hasPermission("MultiCurrency.admin", sender)) {
                MessageUtil.sendMessage(sender, "&4你没有权限这么做");
                return true;
            }
            MultiCurrency.getInstance().getConfigManager().reload();
            MultiCurrency.getInstance().reloadConfig();
            MessageUtil.sendMessage(sender, "&a插件重载成功");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "create", "remove", "clear", "show", "give", "take", "set", "reload");
        }
        if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
            List<Entity> entities = CurrencyManager.listCurrencyType();
            List<String> names = new ArrayList<>();
            for (Entity entity : entities) {
                names.add(entity.getStr("name"));
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("show") && args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                names.add(onlinePlayer.getName());
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("give") && args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                names.add(onlinePlayer.getName());
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("give") && args.length == 3) {
            List<Entity> entities = CurrencyManager.listCurrencyType();
            List<String> names = new ArrayList<>();
            for (Entity entity : entities) {
                names.add(entity.getStr("name"));
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("take") && args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                names.add(onlinePlayer.getName());
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("take") && args.length == 3) {
            List<Entity> entities = CurrencyManager.listCurrencyType();
            List<String> names = new ArrayList<>();
            for (Entity entity : entities) {
                names.add(entity.getStr("name"));
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("set") && args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                names.add(onlinePlayer.getName());
            }
            return names;
        }
        if (args[0].equalsIgnoreCase("set") && args.length == 3) {
            List<Entity> entities = CurrencyManager.listCurrencyType();
            List<String> names = new ArrayList<>();
            for (Entity entity : entities) {
                names.add(entity.getStr("name"));
            }
            return names;
        }
        return new ArrayList<>();
    }

    public boolean hasPermission(String permission, CommandSender sender) {
        Set<PermissionAttachmentInfo> effectivePermissions = sender.getEffectivePermissions();
        for (PermissionAttachmentInfo effectivePermission : effectivePermissions) {
            if (effectivePermission.getPermission().equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }
}
