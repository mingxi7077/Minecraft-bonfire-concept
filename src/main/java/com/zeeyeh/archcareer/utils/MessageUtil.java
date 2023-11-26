package com.zeeyeh.archcareer.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    public static void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, '&', true);
    }

    public static void sendMessage(CommandSender sender, String message, boolean showHeader) {
        sendMessage(sender, message, '&', showHeader);
    }

    public static void sendMessage(CommandSender sender, String message, char colorChar, boolean showHeader) {
        if (showHeader) message = message;
        message = ChatColor.translateAlternateColorCodes(
                colorChar,
                message
        );
        sender.sendMessage(message);
    }
}
