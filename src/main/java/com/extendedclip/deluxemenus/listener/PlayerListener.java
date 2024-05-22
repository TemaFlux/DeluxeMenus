package com.extendedclip.deluxemenus.listener;

import com.extendedclip.deluxemenus.DeluxeMenus;
import com.extendedclip.deluxemenus.action.ClickHandler;
import com.extendedclip.deluxemenus.menu.Menu;
import com.extendedclip.deluxemenus.menu.MenuHolder;
import com.extendedclip.deluxemenus.menu.MenuItem;
import com.extendedclip.deluxemenus.requirement.Requirement;
import com.extendedclip.deluxemenus.requirement.RequirementList;
import com.extendedclip.deluxemenus.utils.SchedulerUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

  private final DeluxeMenus plugin;
  private final Cache<UUID, Long> cache = CacheBuilder.newBuilder()
      .expireAfterWrite(75, TimeUnit.MILLISECONDS).build();

  // This is so dumb. Mojang fix your shit.
  private final Cache<UUID, Long> shiftCache = CacheBuilder.newBuilder()
      .expireAfterWrite(200, TimeUnit.MILLISECONDS).build();

  public PlayerListener(DeluxeMenus plugin) {
    this.plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this, this.plugin);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onCommandExecute(PlayerCommandPreprocessEvent event) {

    String cmd = event.getMessage().substring(1);
    Menu menu = Menu.getMenuByCommand(cmd.toLowerCase());

    if (menu == null) {
      return;
    }

    if (menu.registersCommand()) {
      return;
    }

    Player player = event.getPlayer();
    menu.openMenu(player);
    event.setCancelled(true);
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    if (Menu.inMenu(player)) {
      Menu.closeMenu(player, false);
    }
  }

  @EventHandler
  public void onOpen(InventoryOpenEvent event) {
    if (!(event.getPlayer() instanceof Player)) {
      return;
    }

    final Player player = (Player) event.getPlayer();

    if (player.isSleeping()) {
      event.setCancelled(true);
    }

    if (Menu.inMenu(player)) {
      Menu.closeMenu(player, true);
    }
  }

  @EventHandler
  public void onClose(InventoryCloseEvent event) {

    if (!(event.getPlayer() instanceof Player)) {
      return;
    }

    final Player player = (Player) event.getPlayer();

    if (Menu.inMenu(player)) {
      Menu.closeMenu(player, false);
      SchedulerUtil.runTaskLater(plugin, player, () -> {
        Menu.cleanInventory(player, plugin.getMenuItemMarker());
        player.updateInventory();
      }, 3L);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onClick(InventoryClickEvent event) {

    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    final Player player = (Player) event.getWhoClicked();

    MenuHolder holder = Menu.getMenuHolder(player);

    if (holder == null) {
      return;
    }

    if (holder.getMenu() == null) {
      Menu.closeMenu(player, true);
    }

    if (holder.isUpdating()) {
      event.setCancelled(true);
      return;
    }

    event.setCancelled(true);

    int slot = event.getRawSlot();

    MenuItem item = holder.getItem(slot);

    if (item == null) {
      return;
    }

    if (this.cache.getIfPresent(player.getUniqueId()) != null) {
      return;
    }

    if (this.shiftCache.getIfPresent(player.getUniqueId()) != null) {
      return;
    }

    if (event.getClick() == ClickType.DOUBLE_CLICK) {
      return;
    }

    if (event.getClick() == ClickType.SHIFT_LEFT) {
      this.shiftCache.put(player.getUniqueId(), System.currentTimeMillis());
    }

    Consumer<Optional<RequirementList>> giveItem = requirements -> {
      if (!item.options().giveItem()) return;

      if (requirements != null && requirements.isPresent()) {
        int successful = 0;
        RequirementList requirementList = requirements.get();
        boolean result = true;

        for (Requirement r : requirementList.getRequirements()) {
          if (r.evaluate(holder)) {
            successful = successful + 1;
            if (requirementList.stopAtSuccess() && successful >= requirementList.getMinimumRequirements()) {
              break;
            }
          } else {
            if (!r.isOptional()) {
              result = false;
              break;
            }
          }
        }

        if (!result || successful < requirementList.getMinimumRequirements()) return;
      }

      ItemStack itemStack = item.getItemStack(holder);
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        player.getInventory().addItem(itemStack);
      }
    };

    if (handleClick(player, holder, item.options().clickHandler(),
            item.options().clickRequirements())) {
      giveItem.accept(item.options().clickRequirements());
      return;
    }

    if (event.isShiftClick() && event.isLeftClick()) {
      if (handleClick(player, holder, item.options().shiftLeftClickHandler(),
              item.options().shiftLeftClickRequirements())) {
        giveItem.accept(item.options().shiftLeftClickRequirements());
        return;
      }
    }

    if (event.isShiftClick() && event.isRightClick()) {
      if (handleClick(player, holder, item.options().shiftRightClickHandler(),
              item.options().shiftRightClickRequirements())) {
        giveItem.accept(item.options().shiftRightClickRequirements());
        return;
      }
    }

    if (event.getClick() == ClickType.LEFT) {
      if (handleClick(player, holder, item.options().leftClickHandler(),
              item.options().leftClickRequirements())) {
        giveItem.accept(item.options().leftClickRequirements());
        return;
      }
    }

    if (event.getClick() == ClickType.RIGHT) {
      if (handleClick(player, holder, item.options().rightClickHandler(),
              item.options().rightClickRequirements())) {
        giveItem.accept(item.options().rightClickRequirements());
        return;
      }
    }

    if (event.getClick() == ClickType.MIDDLE) {
      if (handleClick(player, holder, item.options().middleClickHandler(),
              item.options().middleClickRequirements())) {
        giveItem.accept(item.options().middleClickRequirements());
        return;
      }
    }

    giveItem.accept(null);
  }

  /**
   * Handles menu click by player
   * @param player player who clicked
   * @param holder menu holder
   * @param handler click handler
   * @param requirements click requirements
   * @return true if click was handled successfully. will ever return false if no click handler was found
   */
  private boolean handleClick(final @NotNull Player player, final @NotNull MenuHolder holder,
                              final @NotNull Optional<ClickHandler> handler,
                              final @NotNull Optional<RequirementList> requirements) {
    if (handler.isEmpty()) {
      return false;
    }

    if (requirements.isPresent()) {
      final ClickHandler denyHandler = requirements.get().getDenyHandler();

      if (!requirements.get().evaluate(holder)) {
        if (denyHandler == null) {
          return true;
        }

        denyHandler.onClick(holder);
        return true;
      }
    }

    this.cache.put(player.getUniqueId(), System.currentTimeMillis());
    handler.get().onClick(holder);

    return true;
  }
}
