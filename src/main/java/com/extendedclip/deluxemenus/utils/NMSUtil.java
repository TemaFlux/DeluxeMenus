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
            String name = Bukkit.getServer().getClass().getPackage().getName();

            if (name.equals("org.bukkit.craftbukkit")) cachedVersion = "v1_" + Bukkit.getVersion().split("-")[0]; // todo change
            else cachedVersion = name.substring(name.lastIndexOf('.') + 1);
        }

        return cachedVersion;
    }

    /**
     * @return the server version major release number
     */
    public static int getVersionNumber() {
        if (cachedVersionNumber == -1) {
            String name = getVersion().substring(3);
            cachedVersionNumber = Integer.parseInt(name.substring(0, name.length() - 3));
        }

        return cachedVersionNumber;
    }
}
