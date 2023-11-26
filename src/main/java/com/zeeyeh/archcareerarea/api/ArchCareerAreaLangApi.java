package com.zeeyeh.archcareerarea.api;

import cn.hutool.core.util.ReUtil;
import com.zeeyeh.archcareerarea.LanguageManager;

import java.util.List;

public class ArchCareerAreaLangApi {
    private static LanguageManager languageManagerObject;

    /**
     * 初始化语言管理器
     * @param languageManager 语言管理器实例
     */
    public static void initLang(LanguageManager languageManager) {
        languageManagerObject = languageManager;
    }

    /**
     * 获取语言翻译
     * @param path 语言路径
     */
    public static String translate(String path) {
        String translate = (String) languageManagerObject.translate(path);
        List<String> results = ReUtil.findAll("%(.*?)%", translate, 0);
        for (String result : results) {
            String inPath = result.replace("%", "");
            translate = translate.replaceAll(result, ArchCareerAreaLangApi.translate(inPath));
        }
        return translate;
    }
}
