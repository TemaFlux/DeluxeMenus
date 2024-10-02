package com.extendedclip.deluxemenus.requirement.custom;

import com.extendedclip.deluxemenus.DeluxeMenus;
import com.extendedclip.deluxemenus.menu.MenuHolder;
import com.extendedclip.deluxemenus.requirement.Requirement;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownRequirement
extends Requirement {
    private static final Path CACHE_FOLDER = Paths.get("cache/cooldowns");

    private final Path coolDownFolder;
    private final int time;
    private final Map<UUID, Long> expiredMap = new HashMap<>();

    public CooldownRequirement(String menuName, int time) {
        this.time = time;

        Path dataFolder = DeluxeMenus.getInstance().getDataFolder().toPath();
        coolDownFolder = dataFolder.resolve(CACHE_FOLDER.resolve(menuName));
    }

    @Override public boolean evaluate(MenuHolder holder) {
        Player player = holder.getViewer();
        if (player == null) return true;

        Long expiredTime = expiredMap.get(player.getUniqueId());
        Path filePath = coolDownFolder.resolve(player.getUniqueId().toString());

        if (expiredTime == null && Files.exists(filePath)) {
            try {
                expiredTime = Long.parseLong(Files.readString(filePath));
                expiredMap.put(player.getUniqueId(), expiredTime);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (expiredTime == null || System.currentTimeMillis() >= expiredTime) {
            expiredTime = System.currentTimeMillis() + (time * 1000L);
            expiredMap.put(player.getUniqueId(), expiredTime);

            try {
                Files.createDirectories(coolDownFolder);
                Files.write(filePath, expiredTime.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}
