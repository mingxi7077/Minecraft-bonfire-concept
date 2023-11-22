package com.zeeyeh.archcareerlevel.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zeeyeh.archcareerlevel.ArchCareerLevel;
import com.zeeyeh.archcareerlevel.utils.MessageUtil;
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
            languageFolder = new File(ArchCareerLevel.getInstance().getDataFolder(), "locales");
            if (!languageFolder.exists()) {
                ClassLoader classLoader = ArchCareerLevel.class.getClassLoader();
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
                        IoUtil.copy(classLoader.getResourceAsStream(name), Files.newOutputStream(new File(ArchCareerLevel.getInstance().getDataFolder(), name).toPath()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&4语言文件加载失败");
            Bukkit.getPluginManager().disablePlugin(ArchCareerLevel.getInstance());
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
        langName = ArchCareerLevel.getInstance().getConfig().getString("locale");
    }

    public Object translate(String path) {
        if (langName == null) {
            Bukkit.getConsoleSender().sendMessage("[ZeeyehX] Plugin language configuration error, please go to the " +
                    new File(ArchCareerLevel.getInstance().getDataFolder(), "config.yml").getAbsolutePath() +
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
    //private static final String DEFAULT_LANGUAGE = "zh_cn";
    //private final File localesFolder;
    //private final Map<String, Configuration> localeConfigs;
    //private Configuration localeConfig;
    //
    //public LanguageManager() {
    //    this(true);
    //}
    //
    //public LanguageManager(boolean saveDefaultLanguage) {
    //    localesFolder = new File(ArchCareerLevel.getInstance().getDataFolder(), "locales");
    //    localeConfigs = new HashMap<>();
    //    if (!localesFolder.exists()) {
    //        localesFolder.mkdirs();
    //    }
    //    if (saveDefaultLanguage) {
    //        saveDefaultLanguageFile();
    //    }
    //    loadConfigs();
    //}
    //
    //public void loadConfigs() {
    //    File[] localeFiles = localesFolder.listFiles(pathname -> pathname.getName().endsWith(".yml") && pathname.isFile());
    //    if (localeFiles == null) {
    //        return;
    //    }
    //    for (File localeFile : localeFiles) {
    //        String localeName = localeFile.getName().split("[.]")[0];
    //        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(localeFile);
    //        localeConfigs.put(serializeLocaleName(localeName), configuration);
    //    }
    //}
    //
    //public void clear() {
    //    localeConfigs.clear();
    //}
    //
    //public void reload() {
    //    clear();
    //    loadConfigs();
    //    String localName = ArchCareerLevel.getInstance().getConfig().getString("locale");
    //    localName = serializeLocaleName(localName);
    //    File localeFile = new File(localesFolder, localName + ".yml");
    //    if (!localeFile.exists()) {
    //        MessageUtil.send(Bukkit.getConsoleSender(), "&4语言文件不存在");
    //        Bukkit.getPluginManager().disablePlugin(ArchCareerLevel.getInstance());
    //        return;
    //    }
    //    register(localName);
    //}
    //
    //public String serializeLocaleName(String oldLocalName) {
    //    oldLocalName = oldLocalName.trim();
    //    oldLocalName = oldLocalName.replace("-", "_");
    //    oldLocalName = oldLocalName.replace(" ", "_");
    //    oldLocalName = oldLocalName.toLowerCase();
    //    return oldLocalName;
    //}
    //
    //public boolean hasLocale(String localeName) {
    //    return localeConfigs.containsKey(localeName);
    //}
    //
    //public void saveDefaultLanguageFile() {
    //    saveDefaultLanguageFile(false);
    //}
    //
    //public void saveDefaultLanguageFile(boolean replaced) {
    //    File file = new File(localesFolder, "zh_cn.json");
    //    if (replaced) {
    //        if (file.exists()) {
    //            file.delete();
    //        }
    //    }
    //    InputStream inputStream = ArchCareerLevel.getInstance().getResource("locales/zh_cn.json");
    //    try {
    //        Files.copy(inputStream, new File(file.getAbsolutePath()).toPath());
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //        MessageUtil.send(Bukkit.getConsoleSender(), "&4语言文件加载失败");
    //        Bukkit.getPluginManager().disablePlugin(ArchCareerLevel.getInstance());
    //    }
    //}
    //
    //public void register(String localeName) {
    //    if (hasLocale(localeName)) {
    //        MessageUtil.send(Bukkit.getConsoleSender(), "&4语言文件加载失败");
    //        Bukkit.getPluginManager().disablePlugin(ArchCareerLevel.getInstance());
    //        return;
    //    }
    //    localeConfig = localeConfigs.get(localeName);
    //}
    //
    //public void getLanguage(String path) {
    //}
}
