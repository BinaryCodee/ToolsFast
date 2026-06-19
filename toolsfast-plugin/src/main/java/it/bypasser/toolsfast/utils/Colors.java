package it.bypasser.toolsfast.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Colors {

    private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_BRACE = Pattern.compile("\\{#([A-Fa-f0-9]{6})}");
    private static final Pattern HEX_X = Pattern.compile("<#([A-Fa-f0-9]{6})>");

    private Colors() {}

    public static String color(String input) {
        if (input == null) return "";
        Matcher m = HEX.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String hex = m.group(1);
            m.appendReplacement(sb, Matcher.quoteReplacement(ChatColor.of("#" + hex).toString()));
        }
        m.appendTail(sb);
        String out = sb.toString();
        out = HEX_BRACE.matcher(out).replaceAll(match -> ChatColor.of("#" + match.group(1)).toString());
        out = HEX_X.matcher(out).replaceAll(match -> ChatColor.of("#" + match.group(1)).toString());
        return ChatColor.translateAlternateColorCodes('&', out);
    }

    public static List<String> color(List<String> input) {
        List<String> out = new ArrayList<>();
        if (input == null) return out;
        for (String s : input) out.add(color(s));
        return out;
    }

    public static String strip(String input) {
        return ChatColor.stripColor(color(input));
    }

    public static List<String> replace(List<String> input, String placeholder, String value) {
        List<String> out = new ArrayList<>();
        if (input == null) return out;
        for (String s : input) out.add(s.replace(placeholder, value));
        return out;
    }
}
