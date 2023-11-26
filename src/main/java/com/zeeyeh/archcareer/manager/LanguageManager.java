package com.zeeyeh.archcareer.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zeeyeh.archcareer.ArchCareer;
import com.zeeyeh.archcareer.utils.MessageUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LanguageManager {

    private Map<String, JSONObject> languageConfigs;
    private File languageFolder;
    public static final String LANGUAGE_SUFFIX = "json";
    private String langName = "zh_cn";

    public LanguageManager() {
        try {
            languageConfigs = new HashMap<>();
            languageFolder = new File(ArchCareer.getInstance().getDataFolder(), "locales");
            if (!languageFolder.exists()) {
                ClassLoader classLoader = ArchCareer.class.getClassLoader();
                URL resource = classLoader.getResource("locales/");
                String jarFilePath = resource.toString().substring(0, resource.toString().indexOf("!/") + 2);
                URL url = new URL(jarFilePath);
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                languageFolder.mkdirs();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String name = jarEntry.getName();
                    if (name.startsWith("locales/") && !jarEntry.isDirectory()) {
                        IoUtil.copy(classLoader.getResourceAsStream(name), Files.newOutputStream(new File(ArchCareer.getInstance().getDataFolder(), name).toPath()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&4语言文件加载失败");
            Bukkit.getPluginManager().disablePlugin(ArchCareer.getInstance());
        }
        initLangs();
    }

    public void initLangs() {
        List<File> files = FileUtil.loopFiles(languageFolder, file -> file.getName().endsWith("." + LANGUAGE_SUFFIX) && file.isFile());
        for (File file : files) {
            JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf("."));
            languageConfigs.put(name, jsonObject);
        }
        langName = ArchCareer.getInstance().getConfig().getString("locale");
    }

    public Object translate(String path) {
        if (langName == null) {
            Bukkit.getConsoleSender().sendMessage("Plugin language configuration error, please go to the " +
                    new File(ArchCareer.getInstance().getDataFolder(), "config.yml").getAbsolutePath() +
                    " file in a timely manner and fix it on line 6(item: locale).");
            return "";
        }
        if (!languageConfigs.containsKey(langName)) {
            return "";
        }
        JSONObject jsonObject = languageConfigs.get(langName);
        return jsonObject.getByPath(path);
    }

    public void reload() {
        languageConfigs.clear();
        initLangs();
    }
}
