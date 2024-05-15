package com.extendedclip.deluxemenus.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public class InventoryUtil {
    public static @NotNull Map<Integer, Character> toRowMap(Iterable<String> rows) {
        Map<Integer, Character> rowMap = new TreeMap<>();
        if (rows == null) return rowMap;

        int row = 0;
        for (String line : rows) {
            if (line != null && !line.trim().isEmpty()) {
                char[] chars = line.toCharArray();

                for (int x = 0; x < 9; x++) {
                    if (x > chars.length || chars[x] == ' ') continue;
                    rowMap.put(getSlot(x, row), chars[x]);
                }
            }

            row++;
        }

        return rowMap;
    }

    public static int getSlot(int cordX, int cordY) {
        return ((cordX + 1) - 1) + (cordY * 9);
    }
}
