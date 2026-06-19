package it.bypasser.toolsfast.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeParser {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)\\s*([smhdw]?)", Pattern.CASE_INSENSITIVE);

    private TimeParser() {}

    public static long parseToMillis(String input) {
        if (input == null || input.isBlank()) return -1;
        Matcher m = PATTERN.matcher(input.trim().toLowerCase());
        long total = 0;
        boolean found = false;
        while (m.find()) {
            found = true;
            long value = Long.parseLong(m.group(1));
            String unit = m.group(2);
            total += switch (unit) {
                case "s", "" -> TimeUnit.SECONDS.toMillis(value);
                case "m" -> TimeUnit.MINUTES.toMillis(value);
                case "h" -> TimeUnit.HOURS.toMillis(value);
                case "d" -> TimeUnit.DAYS.toMillis(value);
                case "w" -> TimeUnit.DAYS.toMillis(value * 7L);
                default -> value;
            };
        }
        return found ? total : -1;
    }

    public static String formatRemaining(long millis) {
        if (millis <= 0) return "0s";
        long days = millis / TimeUnit.DAYS.toMillis(1);
        millis %= TimeUnit.DAYS.toMillis(1);
        long hours = millis / TimeUnit.HOURS.toMillis(1);
        millis %= TimeUnit.HOURS.toMillis(1);
        long minutes = millis / TimeUnit.MINUTES.toMillis(1);
        millis %= TimeUnit.MINUTES.toMillis(1);
        long seconds = millis / TimeUnit.SECONDS.toMillis(1);
        Map<String, Long> parts = new LinkedHashMap<>();
        if (days > 0) parts.put("d", days);
        if (hours > 0 || days > 0) parts.put("h", hours);
        if (minutes > 0 || hours > 0 || days > 0) parts.put("m", minutes);
        parts.put("s", seconds);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Long> e : parts.entrySet()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(e.getValue()).append(e.getKey());
        }
        return sb.toString();
    }

    public static String formatShort(long millis) {
        if (millis <= 0) return "0s";
        if (millis < 60_000) {
            long s = millis / 1000;
            return s + "s";
        }
        if (millis < 3_600_000) {
            long m = millis / 60_000;
            long s = (millis % 60_000) / 1000;
            return m + "m " + s + "s";
        }
        long h = millis / 3_600_000;
        long m = (millis % 3_600_000) / 60_000;
        return h + "h " + m + "m";
    }
}
