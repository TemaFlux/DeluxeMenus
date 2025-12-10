package com.extendedclip.deluxemenus.menu;

import com.extendedclip.deluxemenus.DeluxeMenus;
import com.extendedclip.deluxemenus.menu.options.MenuOptions;
import com.extendedclip.deluxemenus.utils.SchedulerUtil;
import com.extendedclip.deluxemenus.utils.StringUtils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MenuHolder implements InventoryHolder {

    private final DeluxeMenus plugin;
    @Getter
    private final Player viewer;

    @Getter
    @Setter
    private Player placeholderPlayer;
    @Setter
    @Getter
    private String menuName;
    @Setter
    @Getter
    private Set<MenuItem> activeItems;
    @Getter
    private SchedulerUtil.Task updateTask = null;
    private SchedulerUtil.Task refreshTask = null;
    @Setter
    private Inventory inventory;
    @Setter
    @Getter
    private boolean updating;
    private boolean parsePlaceholdersInArguments;
    private boolean parsePlaceholdersAfterArguments;
    @Setter
    @Getter
    private Map<String, String> typedArgs;

    public MenuHolder(final @NotNull DeluxeMenus plugin, final @NotNull Player viewer) {
        this.plugin = plugin;
        this.viewer = viewer;
    }

    public MenuHolder(final @NotNull DeluxeMenus plugin, final @NotNull Player viewer, final @NotNull String menuName,
                      final @NotNull Set<@NotNull MenuItem> activeItems, final @NotNull Inventory inventory) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.menuName = menuName;
        this.activeItems = activeItems;
        this.inventory = inventory;
    }

    public String getViewerName() {
        return viewer.getName();
    }

    public MenuHolder getHolder() {
        return this;
    }

    public MenuItem getItem(int slot) {
        for (MenuItem item : activeItems) {
            if (item.options().slot() == slot) {
                return item;
            }
        }
        return null;
    }

    public Optional<Menu> getMenu() {
        return Menu.getMenuByName(menuName);
    }

    public @NotNull String setPlaceholdersAndArguments(final @NotNull String string) {
        if (parsePlaceholdersAfterArguments) {
            return setPlaceholders(setArguments(string));
        }
        return setArguments(setPlaceholders(string));
    }

    public @NotNull String setPlaceholders(final @NotNull String string) {
        final Player player = this.placeholderPlayer != null ? this.placeholderPlayer : this.getViewer();
        if (player == null) {
            return string;
        }

        return StringUtils.replacePlaceholders(string, player);
    }

    public @NotNull String setArguments(final @NotNull String string) {
        final Player player = this.placeholderPlayer != null ? this.placeholderPlayer : this.getViewer();

        return StringUtils.replaceArguments(
                string,
                this.typedArgs,
                player,
                this.parsePlaceholdersInArguments
        );
    }

    public void refreshMenu() {

        Optional<Menu> optionalMenu = getMenu();
        if (optionalMenu.isEmpty()) {
            return;
        }

        Menu menu = optionalMenu.get();

        if (menu.getMenuItems().isEmpty()) {
            return;
        }

        setUpdating(true);

        stopPlaceholderUpdate();

        // SchedulerUtil.runTaskAsynchronously(this.plugin, () -> { // placeholder desync (todo fix)

            final Set<MenuItem> active = new HashSet<>();

            for (int i = 0; i < getInventory().getSize(); i++) {
                TreeMap<Integer, MenuItem> e = menu.getMenuItems().get(i);

                if (e == null) {
                    getInventory().setItem(i, null);
                    continue;
                }

                boolean m = false;
                for (MenuItem item : e.values()) {

                    if (item.options().viewRequirements().isPresent()) {

                        if (item.options().viewRequirements().get().evaluate(this)) {
                            m = true;
                            active.add(item);
                            break;
                        }
                    } else {
                        m = true;
                        active.add(item);
                        break;
                    }
                }

                if (!m) {
                    getInventory().setItem(i, null);
                }
            }

            if (active.isEmpty()) {
                Menu.closeMenu(plugin, getViewer(), true);
            }

            boolean update = false;
            Map<Integer, ItemStack> itemStacks = new HashMap<>(active.size() - 1);
            // Bukkit.getScheduler().runTask(plugin, () -> {

            for (MenuItem item : active) {
                ItemStack iStack = item.getItemStack(this);

                int slot = item.options().slot();

                if (slot >= menu.options().size()) {
                    continue;
                }

                if (item.options().updatePlaceholders()) {
                    update = true;
                }

                itemStacks.put(slot, iStack);
            }

            // SchedulerUtil.runTask(plugin, getViewer(), () -> {
                for (Map.Entry<Integer, ItemStack> entry : itemStacks.entrySet()) {
                    getInventory().setItem(entry.getKey(), entry.getValue());
                }

                setActiveItems(active);

                if (update && updateTask == null) {
                    startUpdatePlaceholdersTask();
                } else if (!update && updateTask != null) {
                    stopPlaceholderUpdate();
                }

                setUpdating(false);
            // });
        // });
    }

    public void stopPlaceholderUpdate() {
        if (updateTask != null) {
            try {
                updateTask.cancel();
            } catch (Exception ignored) {
            }
            updateTask = null;
        }
    }

    public void stopRefreshTask() {
        if(refreshTask != null) {
            try {
                refreshTask.cancel();
            } catch (Exception ignored) {
            }
            refreshTask = null;
        }
    }

    public void startRefreshTask() {
        if(refreshTask != null) {
            stopRefreshTask();
        }

        refreshTask = SchedulerUtil.runTaskTimerAsynchronously(plugin, this::refreshMenu, 20L, 20L * Menu.getMenuByName(menuName)
                        .map(Menu::options)
                        .map(MenuOptions::refreshInterval)
                        .orElse(10));
    }

    public void startUpdatePlaceholdersTask() {

        if (updateTask != null) {
            stopPlaceholderUpdate();
        }

        updateTask = SchedulerUtil.runTaskTimerAsynchronously(plugin, () -> {
            if (updating) {
                return;
            }

            Set<MenuItem> items = getActiveItems();

            if (items == null) {
                return;
            }

            for (MenuItem item : items) {

                if (item.options().updatePlaceholders()) {

                    ItemStack i = inventory.getItem(item.options().slot());

                    if (i == null) {
                        continue;
                    }

                    int amt = i.getAmount();

                    if (item.options().dynamicAmount().isPresent()) {
                        try {
                            amt = Integer.parseInt(setPlaceholdersAndArguments(item.options().dynamicAmount().get()));
                            if (amt <= 0) {
                                amt = 1;
                            }
                        } catch (Exception exception) {
                            plugin.printStacktrace(
                                    "Something went wrong while updating item in slot " + item.options().slot() +
                                            ". Invalid dynamic amount: " + setPlaceholdersAndArguments(item.options().dynamicAmount().get()),
                                    exception
                            );
                        }
                    }

                    ItemMeta meta = i.getItemMeta();

                    if (item.options().displayNameHasPlaceholders() && item.options().displayName().isPresent()) {
                        meta.setDisplayName(StringUtils.color(setPlaceholdersAndArguments(item.options().displayName().get())));
                    }

                    if (item.options().loreHasPlaceholders()) {
                        meta.setLore(item.getMenuItemLore(getHolder(), item.options().lore()));
                    }

                    i.setItemMeta(meta);
                    i.setAmount(amt);
                }
            }
        }, 20L, 20L * Menu.getMenuByName(menuName)
                        .map(Menu::options)
                        .map(MenuOptions::updateInterval)
                        .orElse(10));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void parsePlaceholdersInArguments(final boolean parsePlaceholdersInArguments) {
        this.parsePlaceholdersInArguments = parsePlaceholdersInArguments;
    }

    public void parsePlaceholdersAfterArguments(final boolean parsePlaceholdersAfterArguments) {
        this.parsePlaceholdersAfterArguments = parsePlaceholdersAfterArguments;
    }

    public boolean parsePlaceholdersInArguments() {
        return parsePlaceholdersInArguments;
    }

    public boolean parsePlaceholdersAfterArguments() {
        return parsePlaceholdersAfterArguments;
    }

    public @NotNull DeluxeMenus getPlugin() {
        return plugin;
    }
}
