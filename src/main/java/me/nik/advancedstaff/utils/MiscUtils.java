package me.nik.advancedstaff.utils;

import com.google.common.base.Charsets;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("all")
public final class MiscUtils {

    private MiscUtils() {
    }

    public static final ItemStack EMPTY_ITEM = new ItemStack(Material.AIR);

    private static final String LINE_SEPERATOR = System.getProperty("line.separator");

    public static ItemStack makeItem(Material material, String displayName, List<String> lore) {

        ItemStack item = new ItemStack(material);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatUtils.format(displayName));

        if (lore != null) {

            List<String> loreList = new ArrayList<>();

            for (String l : lore) {

                loreList.add(ChatUtils.format(l));
            }

            itemMeta.setLore(loreList);
        }

        item.setItemMeta(itemMeta);

        return item;
    }

    public static String normalizeString(final String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static double decimalRound(final double val, int scale) {
        return BigDecimal.valueOf(val).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static boolean containsIgnoreCase(final String str, final String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();

        if (length == 0) return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    public static boolean containsMultiple(final String str, final String... checkStrings) {
        for (String check : checkStrings) {
            if (str.contains(check)) {
                return true;
            }
        }

        return false;
    }

    public static boolean containsMultiple(final String str, final List<String> checkStrings) {
        for (String check : checkStrings) {
            if (str.contains(check)) {
                return true;
            }
        }

        return false;
    }

    public static <E> E randomElement(final Collection<? extends E> collection) {
        if (collection.size() == 0) return null;

        int index = new Random().nextInt(collection.size());

        if (collection instanceof List) {

            return ((List<? extends E>) collection).get(index);

        } else {

            Iterator<? extends E> iter = collection.iterator();

            for (int i = 0; i < index; i++) iter.next();

            return iter.next();
        }
    }

    public static String capitalizeFirstLetter(final String data) {

        final char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));

        final String restLetters = data.substring(1).toLowerCase();

        return firstLetter + restLetters;
    }

    public static String wrapString(final String str, int wrapLength) {
        if (str == null) {

            return null;

        } else {

            if (wrapLength < 1) wrapLength = 1;

            int inputLineLength = str.length();

            int offset = 0;

            StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

            while (inputLineLength - offset > wrapLength) {

                if (str.charAt(offset) == ' ') {

                    offset++;

                } else {

                    int spaceToWrapAt = str.lastIndexOf(32, wrapLength + offset);

                    if (spaceToWrapAt >= offset) {

                        wrappedLine.append(str, offset, spaceToWrapAt);

                        wrappedLine.append(LINE_SEPERATOR);

                        offset = spaceToWrapAt + 1;

                    } else {

                        spaceToWrapAt = str.indexOf(32, wrapLength + offset);

                        if (spaceToWrapAt >= 0) {

                            wrappedLine.append(str, offset, spaceToWrapAt);

                            wrappedLine.append(LINE_SEPERATOR);

                            offset = spaceToWrapAt + 1;

                        } else {

                            wrappedLine.append(str.substring(offset));

                            offset = inputLineLength;
                        }
                    }
                }
            }

            wrappedLine.append(str.substring(offset));

            return wrappedLine.toString();
        }
    }

    public static YamlConfiguration loadConfigurationUTF_8(final File file) {

        final YamlConfiguration config = new YamlConfiguration();

        try {

            final FileInputStream stream = new FileInputStream(file);

            config.load(new InputStreamReader(stream, Charsets.UTF_8));

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return config;
    }
}