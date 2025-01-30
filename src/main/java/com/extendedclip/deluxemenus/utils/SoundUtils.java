package com.extendedclip.deluxemenus.utils;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;

public class SoundUtils {

    public static Sound getSound(String name) {
        /* try {
            // As of Minecraft 1.21.3, the org.bukkit.Sound class type changed from Enum to Interface.
            // This fixes java.lang.IncompatibleClassChangeError when trying to use versions prior to 1.21.3.
            Method valueOfMethod = Class.forName("org.bukkit.Sound").getMethod("valueOf", String.class);
            return (Sound) valueOfMethod.invoke(null, name);
        } catch (Exception e) {
            // Use the Sound#valueOf method if Reflection fails.
            return Sound.valueOf(name);
        } */

        return XSound.matchXSound(name.toUpperCase()).orElseThrow().parseSound();
    }
}