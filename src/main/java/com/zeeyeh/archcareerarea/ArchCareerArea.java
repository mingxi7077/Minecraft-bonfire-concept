package com.zeeyeh.archcareerarea;

import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareerarea.api.ArchCareerAreaLangApi;
import com.zeeyeh.archcareerarea.listener.PlayerArchListener;
import com.zeeyeh.archcareerarea.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class ArchCareerArea extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static ArchCareerArea instance;
    private AreaManager areaManager;
    private ArchCareer archCareer;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        Plugin residence = Bukkit.getServer().getPluginManager().getPlugin("Residence");
        if (checkPluginInstalled(residence)) return;
        Plugin archCareerPlugin = Bukkit.getServer().getPluginManager().getPlugin("ArchCareer");
        archCareer = (ArchCareer) archCareerPlugin;
        if (checkPluginInstalled(archCareer)) return;
        saveDefaultConfig();
        if (getConfig().getBoolean("enabled")) {
            instance = this;
            languageManager = new LanguageManager();
            areaManager = new AreaManager();
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerArchListener(), this);
            getCommand("archcareerarea").setExecutor(this);
            getCommand("archcareerarea").setTabCompleter(this);
        }
    }

    public ArchCareer getArchCareer() {
        return archCareer;
    }

    private boolean checkPluginInstalled(Plugin archCareer) {
        if (archCareer == null) {
            MessageUtil.send(Bukkit.getConsoleSender(), ArchCareerAreaLangApi.translate("notFindResidence"));
            Bukkit.getPluginManager().disablePlugin(this);
            return true;
        }
        if (!archCareer.isEnabled()) {
            MessageUtil.send(Bukkit.getConsoleSender(), ArchCareerAreaLangApi.translate("notEnableResidence"));
            Bukkit.getPluginManager().disablePlugin(this);
            return true;
        }
        return false;
    }

    public static ArchCareerArea getInstance() {
        return instance;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Set<PermissionAttachmentInfo> effectivePermissions = sender.getEffectivePermissions();
        boolean isRun = false;
        for (PermissionAttachmentInfo effectivePermission : effectivePermissions) {
            if (effectivePermission.getPermission().equalsIgnoreCase("ArchCareerArea.admin")) {
                isRun = true;
                break;
            }
        }
        if (!isRun) {
            MessageUtil.send(sender, ArchCareerAreaLangApi.translate("noPermission"));
            return true;
        }
        if (args[0].equals("reload")) {
            ArchCareerArea.getInstance().reloadConfig();
            ArchCareerArea.getInstance().getAreaManager().reload();
            MessageUtil.send(sender, ArchCareerAreaLangApi.translate("reloadSuccessfully"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>(Collections.singleton("reload"));
    }
}
