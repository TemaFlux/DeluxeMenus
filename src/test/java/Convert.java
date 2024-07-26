import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Convert {
    private static final String[] CHARS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9".split(" ");

    public static void main(String[] args) throws Throwable {
        convertAll("input", "output_" + System.currentTimeMillis());
    }

    public static void convertAll(String inputFolderPath, String outputFolderPath) throws Throwable {
        File inputFolder = new File(inputFolderPath);
        File outputFolder = new File(outputFolderPath);

        if (!inputFolder.isDirectory()) {
            throw new IllegalArgumentException("Input path must be a directory");
        }

        if (!outputFolder.exists() && !outputFolder.mkdirs()) {
            throw new IOException("Failed create directory: " + inputFolderPath);
        }

        List<File> files = collectYmlFiles(inputFolder);
        if (files.isEmpty()) {
            throw new IOException("Failed to list files in directory: " + inputFolderPath);
        }

        for (File file : files) {
            String outFilePath = Paths.get(outputFolderPath, file.getPath().replaceFirst(inputFolder.toPath().toString(), "")).toString();
            convert(file.getPath(), outFilePath);
        }
    }

    private static List<File> collectYmlFiles(File directory) {
        List<File> ymlFiles = new ArrayList<>();
        if (directory.isDirectory()) collectYmlFilesRecursively(directory, ymlFiles);
        return ymlFiles;
    }

    private static void collectYmlFilesRecursively(@NotNull File directory, List<File> ymlFiles) {
        File[] files = directory.listFiles();

        if (files != null) for (File file : files) {
            if (file.isDirectory()) collectYmlFilesRecursively(file, ymlFiles);
            else if (file.getName().endsWith(".yml")) ymlFiles.add(file);
        }
    }

    public static void convert(String filePath, String outPath) throws Throwable {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(new File(filePath));

        boolean hasPattern = configuration.contains("pattern") && configuration.isList("pattern");
        int rows = 0;

        if (hasPattern) {
            rows = configuration.getStringList("pattern").size();
        } else {
            int size = configuration.getInt("size");
            rows = Math.round(size / 9f);
        }

        Map<String, ConfigurationSection> items = new LinkedHashMap<>();
        ConfigurationSection itemsSection = configuration.getConfigurationSection("items");

        if (itemsSection != null) {
            for (String id : itemsSection.getKeys(false)) {
                ConfigurationSection section = itemsSection.getConfigurationSection(id);
                if (section == null) continue;

                if (!hasPattern) {
                    String key = CHARS[items.size()];
                    List<Integer> slots = new ArrayList<>();

                    if (section.contains("slot")) slots.add(section.getInt("slot"));
                    if (section.contains("slots")) {
                        slots.addAll(section.getStringList("slots").stream().flatMap(range -> {
                            String[] values = range.split("-");

                            int start = Integer.parseInt(values[0]);
                            int end = values.length > 1 ? Integer.parseInt(values[1]) : start;

                            return IntStream.range(start, end + 1).boxed();
                        }).collect(Collectors.toList()));
                    }

                    section.set("slot", null);
                    section.set("slots", slots);

                    items.put(key, section);
                }

                Consumer<String> add = value -> {
                    List<String> list = section.getStringList("item_flags");
                    list.add(value);
                    section.set("item_flags", list);
                };

                if (section.contains("hide_attributes")) {
                    section.set("hide_attributes", null);
                    add.accept("HIDE_ATTRIBUTES");
                }

                if (section.contains("hide_enchantments")) {
                    section.set("hide_enchantments", null);
                    add.accept("HIDE_ENCHANTS");
                }

                if (section.contains("hide_unbreakable")) {
                    section.set("hide_unbreakable", null);
                    add.accept("HIDE_UNBREAKABLE");
                }

                if (section.contains("hide_potion_effects")) {
                    section.set("hide_potion_effects", null);
                    add.accept("HIDE_POTION_EFFECTS");
                }
            }
        }

        if (!hasPattern) {
            List<String> pattern = new ArrayList<>();
            for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
                int rowStart = rowIndex * 9;
                String line = IntStream.range(0, 9)
                    .mapToObj(slot -> rowStart + slot)
                    .map(slotId -> items.entrySet().stream()
                        .filter(entry -> entry.getValue().getIntegerList("slots").contains(slotId))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("-"))
                    .collect(Collectors.joining());
                pattern.add(line);
            }

            configuration.set("size", null);
            configuration.set("pattern", pattern);
            configuration.set("items", items);
        }

        items.values().forEach(section -> {
            section.set("slot", null);
            section.set("slots", null);
        });

        configuration.save(new File(outPath));
    }
}
