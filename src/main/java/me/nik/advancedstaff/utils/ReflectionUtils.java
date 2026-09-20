package me.nik.advancedstaff.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ReflectionUtils {

    private static final ConcurrentMap<String, Optional<Method>> METHOD_CACHE = new ConcurrentHashMap<>();

    private static final Method HIDE_PLAYER_MODERN =
            findMethod(Player.class, "hidePlayer", Plugin.class, Player.class).orElse(null);
    private static final Method SHOW_PLAYER_MODERN =
            findMethod(Player.class, "showPlayer", Plugin.class, Player.class).orElse(null);

    private static final Method HIDE_PLAYER_LEGACY =
            findMethod(Player.class, "hidePlayer", Player.class).orElse(null);
    private static final Method SHOW_PLAYER_LEGACY =
            findMethod(Player.class, "showPlayer", Player.class).orElse(null);

    private static final boolean COLLISION_API_AVAILABLE = resolveCollisionApiAvailable();

    private ReflectionUtils() {
    }

    private static boolean resolveCollisionApiAvailable() {
        try {
            return hasMethod(Team.class, "setOption", Team.Option.class, Team.OptionStatus.class);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isCollisionRuleSupported() {
        return COLLISION_API_AVAILABLE;
    }

    public static boolean hidePlayer(Plugin plugin, Player viewer, Player target) {
        if (HIDE_PLAYER_MODERN != null) {
            return invokeSafely(HIDE_PLAYER_MODERN, viewer, plugin, target);
        }
        return invokeSafely(HIDE_PLAYER_LEGACY, viewer, target);
    }

    public static boolean showPlayer(Plugin plugin, Player viewer, Player target) {
        if (SHOW_PLAYER_MODERN != null) {
            return invokeSafely(SHOW_PLAYER_MODERN, viewer, plugin, target);
        }
        return invokeSafely(SHOW_PLAYER_LEGACY, viewer, target);
    }

    public static void setNeverCollide(Team team) {
        if (!COLLISION_API_AVAILABLE || team == null) return;
        try {
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        } catch (Throwable ignored) {
        }
    }

    public static Optional<Method> findMethod(Class<?> owner, String name, Class<?>... paramTypes) {
        String key = buildKey(owner, name, paramTypes);
        return METHOD_CACHE.computeIfAbsent(key, k -> {
            try {
                Method method = owner.getMethod(name, paramTypes);
                method.setAccessible(true);
                return Optional.of(method);
            } catch (NoSuchMethodException e) {
                return Optional.empty();
            }
        });
    }

    public static boolean hasMethod(Class<?> owner, String name, Class<?>... paramTypes) {
        return findMethod(owner, name, paramTypes).isPresent();
    }

    public static boolean invokeSafely(Method method, Object target, Object... args) {
        if (method == null) return false;
        try {
            method.invoke(target, args);
            return true;
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            return false;
        }
    }

    public static Object invoke(Method method, Object target, Object... args) {
        if (method == null) return null;
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            return null;
        }
    }

    private static String buildKey(Class<?> owner, String name, Class<?>... paramTypes) {
        StringBuilder sb = new StringBuilder(owner.getName()).append('#').append(name).append('(');
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(paramTypes[i].getName());
        }
        return sb.append(')').toString();
    }
}