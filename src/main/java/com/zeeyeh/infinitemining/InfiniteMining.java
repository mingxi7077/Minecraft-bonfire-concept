package com.zeeyeh.infinitemining;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zeeyeh.infinitemining.entity.CreatesSettingMap;
import com.zeeyeh.infinitemining.entity.Mining;
import com.zeeyeh.infinitemining.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class InfiniteMining extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static InfiniteMining instance;
    private final Map<String, JSONObject> playerCreates = new HashMap<>();
    private MiningManager miningManager;
    private final Map<BukkitRunnable, Long> runnables = new HashMap<>();
    private final List<Material> noDestructMaps = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("enabled")) {
            instance = this;
            loadNoDestructMaps();
            miningManager = new MiningManager();
            Bukkit.getPluginCommand("infinitemining").setExecutor(this);
            Bukkit.getPluginCommand("infinitemining").setTabCompleter(this);
            registerMiningRefresh();
            enablesMiningRefresh();
            Bukkit.getPluginManager().registerEvents(new PLayerMiningListener(), this);
        }
    }

    public void loadNoDestructMaps() {
        List<String> maps = getConfig().getStringList("no-destruct-maps");
        for (String map : maps) {
            Material material = Material.getMaterial(map);
            noDestructMaps.add(material);
        }
    }

    public List<Material> getNoDestructMaps() {
        return noDestructMaps;
    }

    public void registerMiningRefresh() {
        List<Mining> minings = getMiningManager().getMinings();
        for (Mining mining : minings) {
            long interval = mining.getInterval();
            MiningHandler miningHandler = new MiningHandler(mining.getMaterial(), mining.getLocation());
            runnables.put(miningHandler, interval);
        }
    }

    public void enablesMiningRefresh() {
        for (Map.Entry<BukkitRunnable, Long> entry : runnables.entrySet()) {
            BukkitRunnable runnable = entry.getKey();
            runnable.runTaskTimer(this, 0L, 20L);
        }
    }

    public void disablesMiningRefresh() {
        for (BukkitRunnable runnable : runnables.keySet()) {
            runnable.cancel();
        }
    }

    public static InfiniteMining getInstance() {
        return instance;
    }

    public MiningManager getMiningManager() {
        return miningManager;
    }

    @Override
    public void onDisable() {
        disablesMiningRefresh();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Set<PermissionAttachmentInfo> effectivePermissions = sender.getEffectivePermissions();
        for (PermissionAttachmentInfo effectivePermission : effectivePermissions) {
            if (!effectivePermission.getPermission().equalsIgnoreCase("InfiniteMining.Basic")) {
                MessageUtil.send(sender, "&4你没有权限这么做");
                return true;
            }
        }
        if (args.length == 0) {
            MessageUtil.send(sender, "&b/infinitemining           - &a帮助菜单");
            MessageUtil.send(sender, "&b/infinitemining create    - &a创建矿产");
            MessageUtil.send(sender, "&b/infinitemining remove    - &a删除矿产");
            MessageUtil.send(sender, "&b/infinitemining set       - &a设置矿产属性");
            MessageUtil.send(sender, "&b/infinitemining save      - &a保存矿产");
            MessageUtil.send(sender, "&b/infinitemining cancel    - &a取消创建矿产");
            MessageUtil.send(sender, "&b/infinitemining clear     - &a清空现有所以矿产");
            MessageUtil.send(sender, "&b/infinitemining reload    - &a重载插件");
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("create"))
        {
            playerCreates.put(player.getName(), new JSONObject());
            MessageUtil.send(sender, "&a已开启创建配置模式");
        }
        else if (args[0].equalsIgnoreCase("remove"))
        {
            if (args.length != 2) {
                MessageUtil.send(sender, "&4格式错误,应为: /<command> remove <name>");
                return true;
            }
            String saveName = args[1];
            File file = new File(new File(getDataFolder(), "minings"), saveName + ".json");
            if (!file.exists()) {
                MessageUtil.send(sender, "&4配置不存在");
                return true;
            }
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.send(sender, "&4配置删除失败, 详情请查看控制台报错信息");
                return true;
            }
            MessageUtil.send(sender, "&a配置删除成功");
            disablesMiningRefresh();
            getMiningManager().reload();
            registerMiningRefresh();
            enablesMiningRefresh();
        }
        else if (args[0].equalsIgnoreCase("set"))
        {
            if (hasCreateStatics(player.getName())) {
                // 未处于建设状态
                MessageUtil.send(sender, "&4目前未处于创建配置模式");
                return true;
            }
            if (args.length < 2) {
                // 格式错误
                MessageUtil.send(sender, "&4格式错误,应为: /<command> set <key> [value]");
                return true;
            }
            String option = args[1];
            if (!CreatesSettingMap.listKeys().contains(option)) {
                // 未知设置类型
                MessageUtil.send(sender, "&4未知属性名");
                return true;
            }
            if (args.length == 3) {
                String param = args[2];
                return addCreateConfig(option, player, param);
            }
            return addCreateConfig(option, player, null);
        }
        else if (args[0].equalsIgnoreCase("save"))
        {
            if (hasCreateStatics(player.getName())) {
                // 未处于建设状态
                MessageUtil.send(sender, "&4目前未处于创建配置模式");
                return true;
            }
            if (args.length != 2) {
                // 语法不正确
                MessageUtil.send(sender, "&4格式错误, 应为: /<command> save <name>");
                return true;
            }
            String saveName = args[1];
            JSONObject jsonObject = playerCreates.get(player.getName());
            boolean isFinish = true;
            StringBuilder missing = new StringBuilder();
            if (!jsonObject.containsKey(CreatesSettingMap.POSITION.getKey())) {
                missing.append(CreatesSettingMap.POSITION.getKey()).append(", ");
                isFinish = false;
            }
            if (!jsonObject.containsKey(CreatesSettingMap.TITLE.getKey())) {
                missing.append(CreatesSettingMap.TITLE.getKey()).append(", ");
                isFinish = false;
            }
            if (!jsonObject.containsKey(CreatesSettingMap.TIP.getKey())) {
                missing.append(CreatesSettingMap.TIP.getKey()).append(", ");
                isFinish = false;
            }
            if (!jsonObject.containsKey(CreatesSettingMap.INTERVAL.getKey())) {
                missing.append(CreatesSettingMap.INTERVAL.getKey()).append(", ");
                isFinish = false;
            }
            if (!jsonObject.containsKey(CreatesSettingMap.MATERIAL.getKey())) {
                missing.append(CreatesSettingMap.MATERIAL.getKey()).append(", ");
                isFinish = false;
            }
            String missingString = missing.toString().trim();
            if (missingString.endsWith(",")) {
                missingString = missingString.substring(0, missingString.length() - 1);
            }
            if (!isFinish) {
                // 缺失必要配置项
                MessageUtil.send(sender, "&4配置保存失败!缺失必要配置项: " + missingString);
                return true;
            }
            JSONObject object = playerCreates.get(player.getName());
            if (object.containsKey("world")) {
                object.remove("world");
            }
            object.put("world", player.getWorld().getName());
            try {
                String jsonString = JSON.toJSONString(jsonObject);
                File file = new File(new File(getDataFolder(), "minings"), saveName + ".json");
                if (!file.exists()) {
                    file.createNewFile();
                }
                Files.writeString(file.toPath(), jsonString);
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.send(sender, "&4配置保存失败，详情查看控制台报错信息");
                return true;
            }
            MessageUtil.send(sender, "&a配置保存成功");
            disablesMiningRefresh();
            getMiningManager().reload();
            registerMiningRefresh();
            enablesMiningRefresh();
        }
        else if (args[0].equalsIgnoreCase("cancel"))
        {
            if (hasCreateStatics(player.getName())) {
                // 未处于建设状态
                MessageUtil.send(sender, "&4目前未处于创建配置模式");
                return true;
            }
            playerCreates.remove(player.getName());
            MessageUtil.send(sender, "&a成功取消创建配置");
        }
        else if (args[0].equalsIgnoreCase("clear"))
        {
            File file = new File(getDataFolder(), "minings");
            File[] files = file.listFiles(pathname -> pathname.getName().endsWith(".json") && pathname.isFile());
            if (files == null) {
                return true;
            }
            try {
                for (File miningFile : files) {
                    Files.delete(miningFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.send(sender, "&4配置清空失败，详情查看控制台报错信息");
            }
            MessageUtil.send(sender, "&a配置清空成功");
        }
        else if (args[0].equalsIgnoreCase("reload"))
        {
            reloadConfig();
            getMiningManager().reload();
            MessageUtil.send(sender, "&a配置重载完成");
        }
        return true;
    }

    public boolean addCreateConfig(String option, Player player, Object key) {
        JSONObject jsonObject = playerCreates.get(player.getName());
        switch (option) {
            case "location" -> {
                Location location = player.getLocation();
                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();
                JSONObject locationJsonObject = new JSONObject();
                locationJsonObject.put("x", x);
                locationJsonObject.put("y", y);
                locationJsonObject.put("z", z);
                if (jsonObject.containsKey(CreatesSettingMap.POSITION.getKey())) {
                    jsonObject.remove(CreatesSettingMap.POSITION.getKey());
                }
                jsonObject.put(CreatesSettingMap.POSITION.getKey(), locationJsonObject);
                MessageUtil.send(player, "&a配置项 'location' 设置完成");
                return true;
            }
            case "title" -> {
                String titleString = String.valueOf(key);
                if (jsonObject.containsKey(CreatesSettingMap.TITLE.getKey())) {
                    jsonObject.remove(CreatesSettingMap.TITLE.getKey());
                }
                jsonObject.put(CreatesSettingMap.TITLE.getKey(), MessageUtil.serialize(titleString));
                MessageUtil.send(player, "&a配置项 'title' 设置完成");
                return true;
            }
            case "xp" -> {
                Integer xp = Integer.parseInt(String.valueOf(key));
                if (jsonObject.containsKey(CreatesSettingMap.XP.getKey())) {
                    jsonObject.remove(CreatesSettingMap.XP.getKey());
                }
                jsonObject.put(CreatesSettingMap.XP.getKey(), xp);
                MessageUtil.send(player, "&a配置项 'xp' 设置完成");
                return true;
            }
            case "xpLevel" -> {
                Integer xpLevel = Integer.parseInt(String.valueOf(key));
                if (jsonObject.containsKey(CreatesSettingMap.XP_LEVEL.getKey())) {
                    jsonObject.remove(CreatesSettingMap.XP_LEVEL.getKey());
                }
                jsonObject.put(CreatesSettingMap.XP_LEVEL.getKey(), xpLevel);
                MessageUtil.send(player, "&a配置项 'xpLevel' 设置完成");
                return true;
            }
            case "command" -> {
                String commandString = String.valueOf(key);
                if (jsonObject.containsKey(CreatesSettingMap.COMMAND.getKey())) {
                    jsonObject.remove(CreatesSettingMap.COMMAND.getKey());
                }
                jsonObject.put(CreatesSettingMap.COMMAND.getKey(), commandString);
                MessageUtil.send(player, "&a配置项 'command' 设置完成");
                return true;
            }
            case "material" -> {
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                if (!itemInMainHand.getType().isBlock()) {
                    // 手持物品不是一个方块
                    return false;
                }
                String blockKey = itemInMainHand.getType().getKey().getKey();
                if (jsonObject.containsKey(CreatesSettingMap.MATERIAL.getKey())) {
                    jsonObject.remove(CreatesSettingMap.MATERIAL.getKey());
                }
                jsonObject.put(CreatesSettingMap.MATERIAL.getKey(), blockKey);
                MessageUtil.send(player, "&a配置项 'material' 设置完成");
                return true;
            }
            case "interval" -> {
                Long interval = Long.parseLong(String.valueOf(key));
                if (jsonObject.containsKey(CreatesSettingMap.INTERVAL.getKey())) {
                    jsonObject.remove(CreatesSettingMap.INTERVAL.getKey());
                }
                jsonObject.put(CreatesSettingMap.INTERVAL.getKey(), interval);
                MessageUtil.send(player, "&a配置项 'interval' 设置完成");
                return true;
            }
            case "tip" -> {
                String tipString = String.valueOf(key);
                if (jsonObject.containsKey(CreatesSettingMap.TIP.getKey())) {
                    jsonObject.remove(CreatesSettingMap.TIP.getKey());
                }
                jsonObject.put(CreatesSettingMap.TIP.getKey(), MessageUtil.serialize(tipString));
                MessageUtil.send(player, "&a配置项 'tip' 设置完成");
                return true;
            }
        }
        return false;
    }

    public boolean hasCreateStatics(String playerName) {
        return !playerCreates.containsKey(playerName);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "remove", "set", "save", "cancel", "clear", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return CreatesSettingMap.listKeys();
        }
        return new ArrayList<>();
    }
}
