import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChestCommands {
    private static final Gson gson = new Gson();

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
        // auto-refresh | deprecated or available in forks

        public Settings(@NonNull ConfigurationSection section) {
            name = section.getString("name");
            rowSize = section.getInt("rows", 1);
            commands.addAll(section.getStringList("commands").stream().filter(line -> !StringUtil.isBlank(line)).collect(Collectors.toList()));
            openActions.addAll(Action.from(section.getStringList("open-actions")));
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class Slot {
        private static final int ROW_WIDTH = 9;
        @EqualsAndHashCode.Exclude private final int x, y;

        private String material;
        private Integer durability;
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
            durability = section.getInt("DURABILITY");

            if (!StringUtil.isBlank(material)) {
                material = LegacyUtil.idMap.getOrDefault(durability > 0 && durability < 256 ? material + ":" + durability : material, material); // < 1.12.2 (convert from id)
                com.cryptomorin.xseries.XMaterial.matchXMaterial(durability > 0 ? material + ":" + durability : material).ifPresent(xMaterial -> {
                    material = xMaterial.name();
                    if (durability == xMaterial.getData()) {
                        durability = null;
                    }
                });
            }

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
            } else if (durability != null && durability >= 0xFFFFFF) {
                color = new Color(durability);
                durability = null;
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

            permission = section.getString("PERMISSION");
            viewPermission = section.getString("VIEW-PERMISSION");
            permissionMessage = section.getString("PERMISSION-MESSAGE");

            keepOpen = section.getBoolean("KEEP-OPEN", false);
            actions.addAll(Action.from(section.getStringList("ACTIONS")));
        }

        public void toDeluxeMenuItem(@NonNull ConfigurationSection section) {
            // section.set("slot", getSlot()); // not need if usage a pattern

            if (!StringUtil.isBlank(material) && material.toLowerCase().contains("head")) {
                if (!StringUtil.isBlank(nbtData)) {
                    try {
                        JsonObject object = gson.fromJson(nbtData, JsonObject.class);

                        if (object.has("SkullOwner")) {
                            JsonObject skullOwner = object.get("SkullOwner").getAsJsonObject();
                            JsonObject properties = skullOwner.get("Properties").getAsJsonObject();
                            JsonArray textures = properties.get("textures").getAsJsonArray();
                            JsonObject texture = textures.get(0).getAsJsonObject();

                            JsonObject value = gson.fromJson(
                                new String(Base64.getDecoder().decode(texture.get("Value").getAsString()), StandardCharsets.UTF_8),
                                JsonObject.class
                            );

                            JsonObject valueTextures = value.get("textures").getAsJsonObject();
                            JsonObject skinValue = valueTextures.get("SKIN").getAsJsonObject();
                            String url = skinValue.get("url").getAsString();

                            if (url.contains("textures.minecraft.net/texture/")) {
                                String[] parts = url.split("/");
                                String textureId = parts[parts.length - 1];

                                material = "texture-" + textureId;
                                durability = null;
                                nbtData = null;
                            }
                        }
                    } catch (Throwable e) {
                        System.err.println("[Head | DM] Exception on parsing nbt for skull");
                        e.printStackTrace();
                    }
                } else if (!StringUtil.isBlank(skullOwner)) {
                    material = "head-" + skullOwner;
                    durability = null;
                    nbtData = null;
                }
            }

            section.set("material", material);

            if (durability != null && durability > 0) {
                section.set("damage", durability);
            }

            if (amount > 1) { // default 1
                section.set("amount", amount);
            }

            if (name != null && !name.isEmpty()) {
                section.set("display_name", name);
            }

            if (lore != null && !lore.isEmpty()) {
                section.set("lore", lore);
            }

            if (!enchantments.isEmpty()) {
                StringBuilder enchantmentsNBT = new StringBuilder("Enchantments:[");
                int count = 0;

                for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
                    if (count > 0) {
                        enchantmentsNBT.append(",");
                    }

                    enchantmentsNBT.append("{id:\"minecraft:")
                        .append(entry.getKey())
                        .append("\",lvl:")
                        .append(entry.getValue())
                    .append("}");
                    count++;
                }
                enchantmentsNBT.append("]");
                section.set("nbt_string", enchantmentsNBT.toString());
            } else if (nbtData != null && !nbtData.isEmpty()) {
                section.set("nbt_string", nbtData);
            }

            if (color != null) {
                section.set("rgb", color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());
            }

            if (material != null && material.toLowerCase().contains("skull") && skullOwner != null && !skullOwner.isEmpty()) {
                section.set("SkullOwner", skullOwner);
            }

            if (material != null && (
                material.toLowerCase().contains("banner") ||
                material.toLowerCase().contains("shield")
            )) {
                if ((bannerColor != null && !bannerColor.isEmpty()) || !bannerPatterns.isEmpty()) {
                    StringBuilder blockEntityTag = new StringBuilder("{");

                    if (bannerColor != null && !bannerColor.isEmpty()) {
                        blockEntityTag.append("Base:").append(bannerColor);
                    }

                    if (!bannerPatterns.isEmpty()) {
                        if (bannerColor != null && !bannerColor.isEmpty()) {
                            blockEntityTag.append(",");
                        }

                        blockEntityTag.append("Patterns:[");
                        int i = 0;

                        for (Map.Entry<String, String> patternEntry : bannerPatterns.entrySet()) {
                            if (i > 0) {
                                blockEntityTag.append(",");
                            }

                            blockEntityTag.append("{Pattern:\"")
                                .append(patternEntry.getKey())
                                .append("\",Color:")
                                .append(patternEntry.getValue())
                            .append("}");

                            i++;
                        }
                        blockEntityTag.append("]");
                    }

                    blockEntityTag.append("}");
                    section.set("BlockEntityTag", blockEntityTag.toString());
                }
            }

            List<Map<String, Object>> requirements = new ArrayList<>();

            if (permission != null && !permission.isEmpty()) {
                Map<String, Object> permReq = new HashMap<>();

                permReq.put("type", "has permission");
                permReq.put("permission", permission);

                if (permissionMessage != null && !permissionMessage.isEmpty()) {
                    permReq.put("deny_message", permissionMessage);
                }

                requirements.add(permReq);
            }

            if (viewPermission != null && !viewPermission.isEmpty()) {
                ConfigurationSection viewRequirement = section.createSection("view_requirement.requirements.view");
                viewRequirement.set("type", "has permission");
                viewRequirement.set("permission", viewPermission);
            }

            if (price > 0) {
                Map<String, Object> priceReq = new HashMap<>();

                priceReq.put("type", "has money");
                priceReq.put("amount", price);

                requirements.add(priceReq);
            }

            if (levels > 0) {
                Map<String, Object> levelsReq = new HashMap<>();

                levelsReq.put("type", "has exp levels");
                levelsReq.put("amount", levels);

                requirements.add(levelsReq);
            }

            if (!requiredItems.isEmpty()) {
                for (Map.Entry<String, Integer> reqItem : requiredItems.entrySet()) {
                    Map<String, Object> itemReq = new HashMap<>();

                    itemReq.put("type", "has item");
                    itemReq.put("material", reqItem.getKey());
                    itemReq.put("amount", reqItem.getValue());

                    requirements.add(itemReq);
                }
            }

            if (!requirements.isEmpty()) {
                ConfigurationSection clickRequirement = section.createSection("click_requirement");
                List<String> denyCommands = null;

                int index = 0;
                for (Map<String, Object> requirement : requirements) {
                    if (requirement.containsKey("deny_message")) {
                        if (denyCommands == null) denyCommands = new ArrayList<>();
                        denyCommands.add("[message] " + requirement.remove("deny_message"));
                    }

                    ConfigurationSection requirementSection = clickRequirement.createSection(Integer.toString(index++));
                    for (Map.Entry<String, Object> entry : requirement.entrySet()) {
                        requirementSection.set(entry.getKey(), entry.getValue());
                    }
                }

                if (denyCommands != null && !denyCommands.isEmpty()) clickRequirement.set("deny_commands", denyCommands);
            }

            List<String> actionsList = Action.toDeluxeMenu(actions);

            if (!keepOpen) {
                actionsList.add("[close]");
            }

            if (!actionsList.isEmpty()) {
                section.set("click_commands", actionsList);
            }
        }

        public int getSlot() {
            return (y - 1) * ROW_WIDTH + (x - 1);
        }
    }

    @Data
    public static class Action {
        private final Type type;
        private final String value;

        public Action(@NonNull String value) {
            String typeName = value.indexOf(':') == -1 ? "" : value.split(":")[0] + ":";

            if (typeName.equalsIgnoreCase("console:")) {
                type = Type.CONSOLE;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim();
            } else if (typeName.equalsIgnoreCase("open:")) {
                type = Type.OPEN;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim().replace(".yml", "");
            } else if (typeName.equalsIgnoreCase("tell:")) {
                type = Type.TELL;
                this.value = value.replaceFirst(Pattern.quote(typeName), "").trim();
            } else if (typeName.equalsIgnoreCase("sound:")) {
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

                if (actionLine.indexOf(';') != -1) {
                    actions.addAll(from(Arrays.asList(actionLine.split(";"))));
                } else {
                    actions.add(new Action(actionLine));
                }
            }

            return actions;
        }

        public static @NonNull List<String> toDeluxeMenu(List<Action> actions) {
            List<String> actionsList = new ArrayList<>();
            if (actions == null || actions.isEmpty()) return actionsList;

            for (Action action : actions) {
                switch (action.getType()) {
                    case PLAYER: {
                        actionsList.add("[player] " + action.getValue());
                        break;
                    }

                    case CONSOLE: {
                        actionsList.add("[console] " + action.getValue());
                        break;
                    }

                    case OPEN: {
                        actionsList.add("[open] " + action.getValue());
                        break;
                    }

                    case TELL: {
                        actionsList.add("[message] " + action.getValue());
                        break;
                    }

                    case SOUND: {
                        String value = action.getValue();
                        String[] data = value.split(",");

                        String soundName = data[0].trim().replace(" ", "_");
                        double pitch = 1;
                        double volume = 1;

                        if (data.length > 1) try {
                            pitch = Double.parseDouble(data[1]);
                        } catch (Throwable e) {
                            System.err.println("[Sound | DM] Exception on parsing pitch: " + data[1]);
                        }

                        if (data.length > 2) try {
                            volume = Double.parseDouble(data[2]);
                        } catch (Throwable e) {
                            System.err.println("[Sound | DM] Exception on parsing volume: " + data[2]);
                        }

                        actionsList.add("[sound] " + soundName + " " + volume + " " + pitch);
                        break;
                    }
                }
            }

            return actionsList;
        }

        public enum Type {
            PLAYER, CONSOLE, OPEN, TELL, SOUND
        }
    }

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

        List<File> files = FileUtil.collectYmlFiles(inputFolder);
        if (files.isEmpty()) {
            throw new IOException("Failed to list files in directory: " + inputFolderPath);
        }

        for (File file : files) {
            String outFilePath = Paths.get(outputFolderPath, file.getPath().replaceFirst(inputFolder.toPath().toString(), "")).toString();
            convert(file.getPath(), outFilePath);
        }
    }

    public static void convert(String filePath, String outPath) throws Throwable {
        Menu menu = new Menu(FileUtil.loadYaml(new File(filePath)));

        Map<String, List<Slot>> patternSlot = new LinkedHashMap<>();
        List<String> uniqueIds = new LinkedList<>(Arrays.asList(StringUtil.CHARS));
        List<Integer> needRemove = new ArrayList<>();

        menu.getSlotMap().entrySet().removeIf(entry -> {
            if (needRemove.contains(entry.getKey())) {
                return true;
            }

            int slotId = entry.getKey();
            Slot slot = entry.getValue();

            if (uniqueIds.isEmpty()) {
                System.err.println("[" + slotId + "] All uniqueIds occupied, collecting to pattern cannot be real!");
                return false;
            }

            String uniqueId = uniqueIds.remove(0);
            List<Slot> slots = new ArrayList<>();
            slots.add(slot);

            for (Map.Entry<Integer, Slot> value : menu.getSlotMap().entrySet()) {
                if (value.getKey() == slotId || needRemove.contains(value.getKey())) continue;

                if (Objects.equals(value.getValue(), slot)) {
                    slots.add(value.getValue());
                    needRemove.add(value.getKey());
                }
            }

            patternSlot.put(uniqueId, slots);
            return true;
        });

        YamlConfiguration deluxeMenu = new YamlConfiguration();
        deluxeMenu.set("menu_title", menu.getSettings().getName());

        List<String> commands = menu.getSettings().getCommands();
        if (commands != null && !commands.isEmpty()) deluxeMenu.set("open_command", commands);

        List<String> openActions = Action.toDeluxeMenu(menu.getSettings().getOpenActions());
        if (!openActions.isEmpty()) deluxeMenu.set("open_commands", commands);

        List<String> pattern = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < menu.getSettings().getRowSize(); rowIndex++) {
            int rowStart = rowIndex * 9;
            String line = IntStream.range(0, 9)
                .mapToObj(slot -> rowStart + slot)
                .map(slotId -> patternSlot.entrySet().stream()
                    .filter(entry -> entry.getValue().stream().filter(value -> value.getSlot() == slotId).findAny().orElse(null) != null)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("-")
                ).collect(Collectors.joining());

            pattern.add(line);
        }

        deluxeMenu.set("pattern", pattern);

        ConfigurationSection itemSection = deluxeMenu.createSection("items");
        for (Map.Entry<String, List<Slot>> entry : patternSlot.entrySet()) {
            String id = entry.getKey();
            Slot slot = entry.getValue().get(0);

            ConfigurationSection section = itemSection.createSection(id);
            slot.toDeluxeMenuItem(section);
        }

        deluxeMenu.save(new File(outPath));
    }
}
