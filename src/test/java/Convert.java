import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Convert {
    private static final String[] CHARS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9".split(" ");

    public static void main(String[] args) throws Throwable {
        convertAll("input", "output");
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

        File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            throw new IOException("Failed to list files in directory: " + inputFolderPath);
        }

        for (File file : files) {
            String fileName = file.getName();
            String outFilePath = Paths.get(outputFolderPath, fileName).toString();
            convert(file.getPath(), outFilePath);
        }
    }

    public static void convert(String filePath, String outPath) throws Throwable {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(new File(filePath));

        int size = configuration.getInt("size");
        int rows = size / 9;

        Map<String, ConfigurationSection> items = new LinkedHashMap<>();
        ConfigurationSection itemsSection = configuration.getConfigurationSection("items");

        if (itemsSection != null) {
            for (String id : itemsSection.getKeys(false)) {
                ConfigurationSection section = itemsSection.getConfigurationSection(id);
                if (section == null) continue;

                String key = CHARS[items.size()];
                List<Integer> slots = new ArrayList<>();

                if (section.contains("slot")) slots.add(section.getInt("slot"));
                if (section.contains("slots")) {
                    slots.addAll(section.getStringList("slots").stream()
                    .flatMap(range -> {
                        String[] values = range.split("-");

                        int start = Integer.parseInt(values[0]);
                        int end = values.length > 1 ? Integer.parseInt(values[1]) : start;

                        return IntStream.range(start, end + 1).boxed();
                    })
                    .collect(Collectors.toList()));
                }

                section.set("slot", null);
                section.set("slots", slots);

                items.put(key, section);
            }
        }

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

        items.values().forEach(section -> {
            section.set("slot", null);
            section.set("slots", null);
        });

        configuration.set("size", null);
        configuration.set("pattern", pattern);
        configuration.set("items", items);

        configuration.save(new File(outPath));
    }
}
