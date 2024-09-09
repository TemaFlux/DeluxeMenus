package com.extendedclip.deluxemenus.utils;

import org.bukkit.Bukkit;

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
        if (packageVersion.equals("org.bukkit.craftbukkit")) {
            String version = Bukkit.getVersion(); // Example: "1.21-DEV-dbfeb87 (MC: 1.21)"
            return version.split(" ")[1].replaceAll("[^0-9.]", "");
        } else if (packageVersion.startsWith("org.bukkit.craftbukkit.v")) {
            return packageVersion.substring(packageVersion.lastIndexOf('.') + 1);
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
}
