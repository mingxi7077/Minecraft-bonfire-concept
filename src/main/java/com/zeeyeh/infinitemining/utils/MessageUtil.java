package com.zeeyeh.infinitemining.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    public static final char COLOR_CHAR = '&';

    public static String serialize(String content) {
        return ChatColor.translateAlternateColorCodes(COLOR_CHAR, content);
    }

    public static String serialize(char colorChar, String content) {
        return ChatColor.translateAlternateColorCodes(colorChar, content);
    }

    public static void send(CommandSender sender, String content) {
        send(sender, COLOR_CHAR, content);
    }

    public static void send(CommandSender sender, char colorChar, String content) {
        content = serialize(colorChar, "&b[&6InfiniteMining&b]&r " + content);
        sender.sendMessage(content);
    }
}
