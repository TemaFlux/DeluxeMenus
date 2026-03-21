package com.extendedclip.deluxemenus.utils;

import com.cryptomorin.xseries.XEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;

import java.util.LinkedHashMap;
import java.util.Map;

public final class NMSUtil {
    private static String cachedVersion = null;
    private static int cachedVersionNumber = -1;

    /**
     * @return The server version
     */
    public static String getVersion() {
        if (cachedVersion == null) {
            cachedVersion = extractVersionString(Bukkit.getServer().getClass().getPackage().getName());
            if (StringUtils.isBlank(cachedVersion)) cachedVersion = extractVersionString(Bukkit.getVersion());
            if (StringUtils.isBlank(cachedVersion)) cachedVersion = extractVersionString(Bukkit.getBukkitVersion());
        }

        return cachedVersion;
    }

    private static String extractVersionString(String packageVersion) {
        if (packageVersion == null || packageVersion.isBlank()) return "";

        if (packageVersion.equals("org.bukkit.craftbukkit")) {
            String version = Bukkit.getVersion(); // Example: "1.21-DEV-dbfeb87 (MC: 1.21)"
            return version.split(" ")[1].replaceAll("[^0-9.]", "");
        } else if (packageVersion.startsWith("org.bukkit.craftbukkit.v")) {
            // Example: org.bukkit.craftbukkit.v1_16_R3

            int lastSplitIndex = packageVersion.lastIndexOf('.') + 1;
            String versionId = packageVersion.substring(lastSplitIndex);

            return versionId.substring(1, versionId.lastIndexOf('_') + 1).replace('_', '.');
        } else {
            String bukkitVersion = Bukkit.getBukkitVersion(); // Example: "1.21-R0.1-SNAPSHOT"
            return bukkitVersion.split("-")[0];
        }
    }

    /**
     * @return the server version major release number
     */
    public static int getVersionNumber() {
        if (cachedVersionNumber == -1) {
            String name = getVersion();
            cachedVersionNumber = Integer.parseInt(name.replace(".", ""));
        }

        return cachedVersionNumber;
    }

    private static final Map<String, Enchantment> enchantmentCache = new LinkedHashMap<>();

    public static Enchantment getEnchantment(String enchantName) {
        if (enchantName == null || enchantName.trim().isEmpty()) return null;

        if (enchantmentCache.containsKey(enchantName.toLowerCase()))
            return enchantmentCache.get(enchantName.toLowerCase());

        final XEnchantment xEnchantment = XEnchantment.matchXEnchantment(enchantName).orElse(null);
        final Enchantment enchantment = xEnchantment == null ? null : xEnchantment.getEnchant();
        if (enchantment != null) return enchantment;

        try {
            Enchantment value = Enchantment.getByName(enchantName);
            if (value != null) return value;
        } catch (Throwable ignored) {}

        try {
            for (Enchantment value : Enchantment.values()) {
                if (value.getName().equalsIgnoreCase(enchantName.toLowerCase()) ||
                    value.getKey().value().equalsIgnoreCase(enchantName.toLowerCase())
                ) {
                    enchantmentCache.put(enchantName.toLowerCase(), value);
                    return value;
                }
            }
        } catch (Throwable ignored) {}

        return null;
    }
}
