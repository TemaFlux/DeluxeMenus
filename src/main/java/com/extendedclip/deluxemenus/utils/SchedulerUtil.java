package com.extendedclip.deluxemenus.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerUtil {
    private static Set<ScheduledTask> tasks;

    public static Task runTask(Plugin plugin, Object handle, Runnable action) {
        if (plugin == null || action == null) return null;

        if (Bukkit.isPrimaryThread()) {
            try {
                action.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return Task.EMPTY;
        }

        Object handleTask = null;

        if (VersionHelper.isFolia()) {
            if (handle == null) {
                handle = getAnyEntity();
                if (handle == null) return null;
            }

            ScheduledTask task = null;
            if (handle instanceof Entity) task = ((Entity) handle).getScheduler().run(plugin, ignored -> action.run(), null);
            else if (handle instanceof Location) task = Bukkit.getRegionScheduler().run(plugin, (Location) handle, ignored -> action.run());

            addTask(task);
            if (task != null) handleTask = task;
        } else {
            handleTask = Bukkit.getScheduler().runTask(plugin, action);
        }

        return handleTask == null ? null : new Task(handleTask);
    }

    public static Task runTaskLater(Plugin plugin, Object handle, Runnable action, long delayTicks) {
        if (plugin == null || action == null) return null;

        Object handleTask = null;

        if (VersionHelper.isFolia()) {
            if (handle == null) {
                handle = getAnyEntity();
                if (handle == null) return null;
            }

            ScheduledTask task = null;
            if (handle instanceof Entity) task = ((Entity) handle).getScheduler().runDelayed(plugin, ignored -> action.run(), null, delayTicks);
            else if (handle instanceof Location) task = Bukkit.getRegionScheduler().runDelayed(plugin, (Location) handle, ignored -> action.run(), delayTicks);

            addTask(task);
            if (task != null) handleTask = task;
        } else {
            handleTask = Bukkit.getScheduler().runTaskLater(plugin, action, delayTicks);
        }

        return handleTask == null ? null : new Task(handleTask);
    }

    public static Task runTaskAsynchronously(Plugin plugin, Runnable action) {
        if (plugin == null || action == null) return null;

        Object handleTask;

        if (VersionHelper.isFolia()) {
            ScheduledTask task = Bukkit.getAsyncScheduler().runNow(plugin, ignored -> action.run());

            addTask(task);
            handleTask = task;
        } else {
            handleTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, action);
        }

        return new Task(handleTask);
    }

    public static Task runTaskTimerAsynchronously(Plugin plugin, Runnable action, long initialDelayTicks, long periodTicks) {
        if (plugin == null || action == null) return null;

        Object handleTask;

        if (VersionHelper.isFolia()) {
            initialDelayTicks = (initialDelayTicks * 1000L) / 20;
            periodTicks = (periodTicks * 1000L) / 20;

            ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, ignored -> action.run(), initialDelayTicks, periodTicks, TimeUnit.MILLISECONDS);

            addTask(task);
            handleTask = task;
        } else {
            handleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, action, initialDelayTicks, periodTicks);
        }

        return new Task(handleTask);
    }

    public static void cancelTasks(Plugin plugin) {
        if (plugin == null) return;

        if (VersionHelper.isFolia()) {
            try {
                Bukkit.getAsyncScheduler().cancelTasks(plugin);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                if (tasks == null || tasks.isEmpty()) return;

                tasks.removeIf(task -> {
                    if (Objects.equals(task.getOwningPlugin(), plugin)) {
                        if (!task.isCancelled()) task.cancel();
                        return true;
                    }

                    return false;
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

    private static void addTask(ScheduledTask task) {
        if (task == null || !VersionHelper.isFolia()) return;

        if (tasks == null) tasks = new HashSet<>();
        tasks.add(task);
    }

    private static Entity getAnyEntity() {
        List<World> worldList = Bukkit.getWorlds();
        World world = worldList.isEmpty() ? null : worldList.get(0);

        List<Entity> entities = world == null ? null : world.getEntities();
        return entities == null || entities.isEmpty() ? null : entities.get(0);
    }

    @Getter
    @RequiredArgsConstructor
    public static class Task {
        public static final Task EMPTY = new Task(null);
        private final Object handle;

        public void cancel() {
            if (handle == null) return;

            try {
                if (VersionHelper.isFolia() && handle instanceof ScheduledTask) {
                    ((ScheduledTask) handle).cancel();
                } else if (handle instanceof BukkitTask) {
                    ((BukkitTask) handle).cancel();
                } else if (handle instanceof CompletableFuture) {
                    ((CompletableFuture<?>) handle).cancel(true);
                }
            } catch (Throwable ignored) {}
        }

        public boolean isCancelled() {
            if (handle == null) return false;

            if (VersionHelper.isFolia() && handle instanceof ScheduledTask) {
                return ((ScheduledTask) handle).isCancelled();
            } else if (handle instanceof BukkitTask) {
                return ((BukkitTask) handle).isCancelled();
            } else if (handle instanceof CompletableFuture) {
                return ((CompletableFuture<?>) handle).isCancelled();
            }

            return false;
        }

    }
}
