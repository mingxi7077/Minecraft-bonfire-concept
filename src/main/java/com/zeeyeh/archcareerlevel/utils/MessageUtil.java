package com.zeeyeh.archcareerlevel.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    public static void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, '&');
    }

    public static void sendMessage(CommandSender sender, String message, char colorChar) {
        message = ChatColor.translateAlternateColorCodes(
                colorChar,
                message
        );
        sender.sendMessage(message);
    }
}
