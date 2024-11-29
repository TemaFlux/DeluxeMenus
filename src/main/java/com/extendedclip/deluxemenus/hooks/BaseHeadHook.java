package com.extendedclip.deluxemenus.hooks;

import com.extendedclip.deluxemenus.DeluxeMenus;
import com.extendedclip.deluxemenus.cache.SimpleCache;
import com.extendedclip.deluxemenus.utils.SkullUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaseHeadHook implements ItemHook, SimpleCache {

  private final Map<String, ItemStack> cache = new ConcurrentHashMap<>();

  @Override
  public ItemStack getItem(@NotNull final String... arguments) {
    if (arguments.length == 0) {
      return DeluxeMenus.getInstance().getHead().clone();
    }

    try {
      return cache.computeIfAbsent(arguments[0], SkullUtils::getSkullByBase64EncodedTextureUrl).clone();
    } catch (Exception exception) {
      DeluxeMenus.printStacktrace(
          "Something went wrong while trying to get base64 head: " + arguments[0],
          exception
      );
    }

    return DeluxeMenus.getInstance().getHead().clone();
  }

  @Override
  public boolean isItem(@NotNull ItemStack item, @NotNull String... arguments) {
    if (arguments.length == 0) {
      return false;
    }
    ItemStack skull = SkullUtils.getSkullByBase64EncodedTextureUrl(arguments[0]);
    return Bukkit.getItemFactory().equals(item.getItemMeta(), skull.getItemMeta());
  }

  @Override
  public String getPrefix() {
    return "basehead-";
  }

  @Override
  public void clearCache() {
    cache.clear();
  }
}
