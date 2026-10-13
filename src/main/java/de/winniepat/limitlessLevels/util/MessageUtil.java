package de.winniepat.limitlessLevels.util;

import de.winniepat.limitlessLevels.LimitlessLevels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    private final LimitlessLevels plugin;

    public MessageUtil(LimitlessLevels plugin) {
        this.plugin = plugin;
    }

    public void send(CommandSender sender, String key) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&aLimitlessLevels&8] &r");
        String message = plugin.getConfig().getString("messages." + key, "&cMissing message: " + key);

        sender.sendMessage(color(prefix + message));
    }

    public void send(CommandSender sender, String key, String... replacements) {
        String prefix = plugin.getConfig().getString(
                "messages.prefix", "&8[&aLimitlessLevels&8] &r"
        );

        String message = plugin.getConfig().getString(
                "messages." + key, "&cMissing message: " + key
        );

        for (int i = 0;  i + 1 < replacements.length; i += 2) {
            message = message.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }

        sender.sendMessage(color(prefix + message));
    }

    private static final String FONT_CHARS =
            "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ\uD835\uDFE2\uD835\uDFE3\uD835\uDFE4\uD835\uDFE5\uD835\uDFE6\uD835\uDFE7\uD835\uDFE8\uD835\uDFE9\uD835\uDFEA\uD835\uDFEB";

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        int total = countVisible(message);
        int gradientLimit = countVisibleUntilBracket(message);
        StringBuilder output = new StringBuilder();
        int index = 0;
        int i = 0;

        while (i < message.length()) {
            char character = message.charAt(i);

            if (character == '&' && i + 1 < message.length()) {
                output.append(character);
                output.append(message.charAt(i + 1));
                i += 2;
                continue;
            }

            if (Character.isWhitespace(character)) {
                output.append(character);
                i++;
                continue;
            }

            if (character == '[' || character == ']') {
                output.append("&7");
                output.append(character);
                index++;
                i++;
                continue;
            }

            if (index < gradientLimit) {
                String hexColor = gradientColor(
                        index, gradientLimit <= 1 ? 1 : gradientLimit
                );
                output.append(hexToLegacy(hexColor));
            }
            output.append(mapFont(character));
            index++;
            i++;
        }

        return ChatColor.translateAlternateColorCodes('&', output.toString());
    }

    private static int countVisible(String message) {
        int count = 0;
        int i = 0;

        while (i < message.length()) {
            char character = message.charAt(i);

            if (character == '&' && i + 1 < message.length()) {
                i += 2;
                continue;
            }

            if (!Character.isWhitespace(character)) {
                count++;
            }
            i++;
        }

        return count;
    }

    private static int countVisibleUntilBracket(String message) {
        int count = 0;
        boolean stopped = false;
        int i = 0;

        while (i < message.length() && !stopped) {
            char character = message.charAt(i);

            if (character == '&' && i + 1 < message.length()) {
                i += 2;
                continue;
            }

            if (!Character.isWhitespace(character)) {
                count++;
            }

            if (character == ']') {
                stopped = true;
            }
            i++;
        }

        return count;
    }

    private static char mapFont(char character) {
        if (character >= 'a' && character <= 'z') {
            return FONT_CHARS.charAt(character - 'a');
        }

        if (character >= 'A' && character <= 'Z') {
            return FONT_CHARS.charAt(character - 'A');
        }

        if (character >= '0' && character <= '9') {
            return FONT_CHARS.charAt(26 + (character - '0'));
        }

        return character;
    }

    private static String gradientColor(int index, int total) {
        if (total <= 1) {
            return "#55ff55";
        }

        double progress = (double) index / (double) (total - 1);

        int startRed = 0x55;
        int startGreen = 0xff;
        int startBlue = 0x55;

        int endRed = 0x55;
        int endGreen = 0x55;
        int endBlue = 0xff;

        int red = (int) (startRed + (endRed - startRed) * progress);
        int green = (int) (startGreen + (endGreen - startGreen) * progress);
        int blue = (int) (startBlue + (endBlue - startBlue) * progress);

        return String.format("#%02x%02x%02x", red, green, blue);
    }

    private static String hexToLegacy(String hex) {
        return "&x&"
                + hex.charAt(1) + "&" + hex.charAt(2) + "&"
                + hex.charAt(3) + "&" + hex.charAt(4) + "&"
                + hex.charAt(5) + "&" + hex.charAt(6);
    }
}
