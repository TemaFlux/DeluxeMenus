import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class ChestCommands {
    @Data
    @RequiredArgsConstructor
    public static class Menu {
        private final Settings settings;
        private final Map<Integer, Slot> slotMap = new LinkedHashMap<>();

        public Menu(@NonNull ConfigurationSection section) {
            settings = new Settings(Objects.requireNonNull(section.getConfigurationSection("menu-settings")));

            for (String sectionId : section.getKeys(false)) {
                if (sectionId.equals("menu-settings")) continue;

                ConfigurationSection slotSection = section.getConfigurationSection(sectionId);
                if (slotSection == null) continue;

                Slot slot = new Slot(slotSection);
                int position = slot.getSlot();

                if (slotMap.containsKey(position)) {
                    System.err.println("Detected duplicate slot: " + position + ", this slot has been overwritten");
                }

                slotMap.put(position, slot);
            }
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class Settings {
        // menu-settings:
        //  name: '&eMenu'
        //  rows: 3
        //  commands:
        //  - menu
        //  open-actions:
        //  - 'tell: &eMenu opened'
        //  - 'sound: block note pling'
        //  open-with-item:
        //    material: 1 or stone
        //    left-click: false
        //    right-click: false

        private final String name;
        private final int rowSize;

        private final List<String> commands = new ArrayList<>();
        private final List<Action> openActions = new ArrayList<>();

        public Settings(@NonNull ConfigurationSection section) {
            name = section.getString("name");
            rowSize = section.getInt("rows", 1);
            commands.addAll(section.getStringList("commands"));
            openActions.addAll(Action.from(section.getStringList("open-actions")));
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class Slot {
        private static final int ROW_WIDTH = 9;
        private final int x, y;

        private String material;
        private int durability;
        private int amount = 1;

        // Display
        private String name;
        private List<String> lore = new ArrayList<>();

        // NBT
        private String nbtData;
        private Map<String, Integer> enchantments = new HashMap<>(); // {enchantment},{level}
        private Color color; // {r},{g},{b}
        private String skullOwner;

        private String bannerColor;
        private Map<String, String> bannerPatterns = new HashMap<>(); // {pattern}:{color}

        // Requirements
        private double price;
        private int levels;
        private final Map<String, Integer> requiredItems = new HashMap<>(); // {material}:{?durability},{?amount}
        private String permission, viewPermission;
        private String permissionMessage;

        private boolean keepOpen;
        private final List<Action> actions = new ArrayList<>();

        public Slot(@NonNull ConfigurationSection section) {
            x = section.getInt("POSITION-X");
            y = section.getInt("POSITION-Y");
            material = section.getString("MATERIAL");

            name = section.getString("NAME");
            lore.addAll(section.getStringList("LORE"));

            nbtData = section.getString("NBT-DATA");

            for (String value : section.getStringList("ENCHANTMENTS")) {
                if (StringUtil.isBlank(value)) continue;
                String[] data = value.split(";");

                String enchantName = data[0].trim();
                int level = 0;

                if (data.length > 1) try {
                    level = Integer.parseInt(data[1].trim());
                } catch (Throwable e) {
                    System.err.println("[Enchantments] Exception on try parse to number: '" + data[1] + "'");
                }

                enchantments.put(enchantName, level);
            }

            if (section.contains("COLOR", true)) {
                String value = section.getString("COLOR", "");
                String[] data = value.split(",");

                int red = 0, green = 0, blue = 0;

                for (int index = 0; index < data.length; index++) {
                    String color = data[index].trim();
                    if (StringUtil.isBlank(value)) continue;

                    try {
                        int colorValue = Integer.parseInt(color);

                        switch (index) {
                            case 0: {
                                red = colorValue;
                                break;
                            }

                            case 1: {
                                green = colorValue;
                                break;
                            }

                            case 2: {
                                blue = colorValue;
                                break;
                            }
                        }
                    } catch (Throwable e) {
                        System.err.println("[Color] Exception on try parse to number: '" + color + "'");
                    }
                }

                color = new Color(red, green, blue);
            }

            skullOwner = section.getString("SKULL-OWNER");
            bannerColor = section.getString("BANNER-COLOR");

            for (String value : section.getStringList("BANNER-PATTERNS")) {
                if (StringUtil.isBlank(value)) continue;
                String[] data = value.split(":");

                if (data.length < 2) {
                    System.err.println("[BannerPattern] Not enough arguments: '" + value + "'");
                    continue;
                }

                bannerPatterns.put(data[0].trim(), data[1].trim());
            }

            price = section.getDouble("PRICE");
            levels = section.getInt("LEVELS");

            for (String value : section.getStringList("REQUIRED-ITEMS")) {
                if (StringUtil.isBlank(value)) continue;
                String[] data = value.split(";");

                String material = data[0].trim();
                int amount = 1;

                if (data.length > 1) try {
                    amount = Integer.parseInt(data[1].trim());
                } catch (Throwable e) {
                    System.err.println("[RequiredItems] Exception on try parse to number: '" + data[1] + "'");
                }

                if (amount > 0) requiredItems.put(material, amount);
            }

            keepOpen = section.getBoolean("KEEP-OPEN", false);
            permission = section.getString("PERMISSION");
            viewPermission = section.getString("VIEW-PERMISSION");
            permissionMessage = section.getString("PERMISSION-MESSAGE");
        }

        public int getSlot() {
            return (y * ROW_WIDTH) + x;
        }
    }

    @Data
    public static class Action {
        private final Type type;
        private final String value;

        public Action(@NonNull String value) {
            String typeName = value.split(":")[0];

            if (typeName.equalsIgnoreCase("console")) {
                type = Type.CONSOLE;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim();
            } else if (typeName.equalsIgnoreCase("open")) {
                type = Type.OPEN;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim();
            } else if (typeName.equalsIgnoreCase("tell")) {
                type = Type.TELL;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim();
            } else if (typeName.equalsIgnoreCase("sound")) {
                type = Type.SOUND;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim();
            } else {
                type = Type.PLAYER;
                this.value = typeName.equalsIgnoreCase("player") ? value.replaceFirst(Pattern.quote(typeName), "").trim() : value;
            }
        }

        public static @NonNull List<Action> from(List<String> actionList) {
            List<Action> actions = new ArrayList<>();
            if (actionList == null || actionList.isEmpty()) return actions;

            for (String actionLine : actionList) {
                if (StringUtil.isBlank(actionLine)) continue;
                actions.add(new Action(actionLine));
            }

            return actions;
        }

        public enum Type {
            PLAYER, CONSOLE, OPEN, TELL, SOUND
        }
    }
}
