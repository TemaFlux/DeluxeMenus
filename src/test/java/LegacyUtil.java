import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class LegacyUtil {
    public static final Map<String, String> idMap = new LinkedHashMap<>();

    static {
        idMap.put("0", "air");

        idMap.put("1", "stone");
        idMap.put("1:1", "stone");
        idMap.put("1:2", "stone");
        idMap.put("1:3", "stone");
        idMap.put("1:4", "stone");
        idMap.put("1:5", "stone");
        idMap.put("1:6", "stone");

        idMap.put("2", "grass");

        idMap.put("3", "dirt");
        idMap.put("3:1", "dirt");
        idMap.put("3:2", "dirt");

        idMap.put("4", "cobblestone");

        idMap.put("5", "planks");
        idMap.put("5:1", "planks");
        idMap.put("5:2", "planks");
        idMap.put("5:3", "planks");
        idMap.put("5:4", "planks");
        idMap.put("5:5", "planks");

        idMap.put("6", "sapling");
        idMap.put("6:1", "sapling");
        idMap.put("6:2", "sapling");
        idMap.put("6:3", "sapling");
        idMap.put("6:4", "sapling");
        idMap.put("6:5", "sapling");

        idMap.put("7", "bedrock");
        idMap.put("8", "flowing_water");
        idMap.put("9", "water");
        idMap.put("10", "flowing_lava");
        idMap.put("11", "lava");

        idMap.put("12", "sand");
        idMap.put("12:1", "sand");

        idMap.put("13", "gravel");
        idMap.put("14", "gold_ore");
        idMap.put("15", "iron_ore");
        idMap.put("16", "coal_ore");

        idMap.put("17", "log");
        idMap.put("17:1", "log");
        idMap.put("17:2", "log");
        idMap.put("17:3", "log");

        idMap.put("18", "leaves");
        idMap.put("18:1", "leaves");
        idMap.put("18:2", "leaves");
        idMap.put("18:3", "leaves");

        idMap.put("19", "sponge");
        idMap.put("19:1", "sponge");
        idMap.put("20", "glass");
        idMap.put("21", "lapis_ore");
        idMap.put("22", "lapis_block");
        idMap.put("23", "dispenser");
        idMap.put("24", "sandstone");

        idMap.put("24:1", "sandstone");
        idMap.put("24:2", "sandstone");

        idMap.put("25", "noteblock");
        idMap.put("26", "bed");
        idMap.put("27", "golden_rail");
        idMap.put("28", "detector_rail");
        idMap.put("29", "sticky_piston");
        idMap.put("30", "web");

        idMap.put("31", "tallgrass");
        idMap.put("31:1", "tallgrass");
        idMap.put("31:2", "tallgrass");

        idMap.put("32", "deadbush");
        idMap.put("33", "piston");
        idMap.put("34", "piston_head");

        idMap.put("35", "wool");
        IntStream.range(1, 16).forEach(value -> idMap.put("35:" + value, "wool"));

        idMap.put("37", "yellow_flower");

        idMap.put("38", "red_flower");
        idMap.put("38:1", "red_flower");
        idMap.put("38:2", "ALLIUM");
        idMap.put("38:3", "red_flower");
        idMap.put("38:4", "red_flower");
        idMap.put("38:5", "red_flower");
        idMap.put("38:6", "red_flower");
        idMap.put("38:7", "red_flower");
        idMap.put("38:8", "red_flower");

        idMap.put("39", "brown_mushroom");
        idMap.put("40", "red_mushroom");
        idMap.put("41", "gold_block");
        idMap.put("42", "iron_block");

        idMap.put("43", "double_stone_slab");
        idMap.put("43:1", "double_stone_slab");
        idMap.put("43:2", "double_stone_slab");
        idMap.put("43:3", "double_stone_slab");
        idMap.put("43:4", "double_stone_slab");
        idMap.put("43:5", "double_stone_slab");
        idMap.put("43:6", "double_stone_slab");
        idMap.put("43:7", "double_stone_slab");

        idMap.put("44", "stone_slab");
        idMap.put("44:1", "stone_slab");
        idMap.put("44:2", "stone_slab");
        idMap.put("44:3", "stone_slab");
        idMap.put("44:4", "stone_slab");
        idMap.put("44:5", "stone_slab");
        idMap.put("44:6", "stone_slab");
        idMap.put("44:7", "stone_slab");

        idMap.put("45", "brick");
        idMap.put("46", "tnt");
        idMap.put("47", "bookshelf");
        idMap.put("48", "mossy_cobblestone");
        idMap.put("49", "obsidian");
        idMap.put("50", "torch");
        idMap.put("51", "fire");
        idMap.put("52", "mob_spawner");
        idMap.put("53", "oak_stairs");
        idMap.put("54", "chest");
        idMap.put("55", "redstone_wire");
        idMap.put("56", "diamond_ore");
        idMap.put("57", "diamond_block");
        idMap.put("58", "crafting_table");
        idMap.put("59", "wheat");
        idMap.put("60", "farmland");
        idMap.put("61", "furnace");
        idMap.put("62", "lit_furnace");
        idMap.put("63", "standing_sign");
        idMap.put("64", "wooden_door");
        idMap.put("65", "ladder");
        idMap.put("66", "rail");
        idMap.put("67", "stone_stairs");
        idMap.put("68", "wall_sign");
        idMap.put("69", "lever");
        idMap.put("70", "stone_pressure_plate");
        idMap.put("71", "iron_door");
        idMap.put("72", "wooden_pressure_plate");
        idMap.put("73", "redstone_ore");
        idMap.put("74", "lit_redstone_ore");
        idMap.put("75", "unlit_redstone_torch");
        idMap.put("76", "redstone_torch");
        idMap.put("77", "stone_button");
        idMap.put("78", "snow_layer");
        idMap.put("79", "ice");
        idMap.put("80", "snow");
        idMap.put("81", "cactus");
        idMap.put("82", "clay");
        idMap.put("83", "reeds");
        idMap.put("84", "jukebox");
        idMap.put("85", "fence");
        idMap.put("86", "pumpkin");
        idMap.put("87", "netherrack");
        idMap.put("88", "soul_sand");
        idMap.put("89", "glowstone");
        idMap.put("90", "portal");
        idMap.put("91", "lit_pumpkin");
        idMap.put("92", "cake");
        idMap.put("93", "unpowered_repeater");
        idMap.put("94", "powered_repeater");

        idMap.put("95", "stained_glass");
        IntStream.range(1, 16).forEach(value -> idMap.put("95:" + value, "stained_glass"));

        idMap.put("96", "trapdoor");

        idMap.put("97", "monster_egg");
        idMap.put("97:1", "monster_egg");
        idMap.put("97:2", "monster_egg");
        idMap.put("97:3", "monster_egg");
        idMap.put("97:4", "monster_egg");
        idMap.put("97:5", "monster_egg");

        idMap.put("98", "stonebrick");
        idMap.put("98:1", "stonebrick");
        idMap.put("98:2", "stonebrick");
        idMap.put("98:3", "stonebrick");

        idMap.put("99", "brown_mushroom_block");
        idMap.put("100", "red_mushroom_block");
        idMap.put("101", "iron_bars");
        idMap.put("102", "glass_pane");
        idMap.put("103", "melon_block");
        idMap.put("104", "pumpkin_stem");
        idMap.put("105", "melon_stem");
        idMap.put("106", "vine");
        idMap.put("107", "fence_gate");
        idMap.put("108", "brick_stairs");
        idMap.put("109", "stone_brick_stairs");
        idMap.put("110", "mycelium");
        idMap.put("111", "LILY_PAD");
        idMap.put("112", "nether_brick");
        idMap.put("113", "nether_brick_fence");
        idMap.put("114", "nether_brick_stairs");
        idMap.put("115", "nether_wart");
        idMap.put("116", "enchanting_table");
        idMap.put("117", "brewing_stand");
        idMap.put("118", "cauldron");
        idMap.put("119", "end_portal");
        idMap.put("120", "end_portal_frame");
        idMap.put("121", "end_stone");
        idMap.put("122", "dragon_egg");
        idMap.put("123", "redstone_lamp");
        idMap.put("124", "lit_redstone_lamp");

        idMap.put("125", "double_wooden_slab");
        IntStream.range(1, 6).forEach(value -> idMap.put("125:" + value, "double_wooden_slab"));

        idMap.put("126", "wooden_slab");
        IntStream.range(1, 6).forEach(value -> idMap.put("126:" + value, "wooden_slab"));

        idMap.put("127", "cocoa");
        idMap.put("128", "sandstone_stairs");
        idMap.put("129", "emerald_ore");
        idMap.put("130", "ender_chest");
        idMap.put("131", "tripwire_hook");
        idMap.put("132", "tripwire_hook");
        idMap.put("133", "emerald_block");
        idMap.put("134", "spruce_stairs");
        idMap.put("135", "birch_stairs");
        idMap.put("136", "jungle_stairs");
        idMap.put("137", "command_block");
        idMap.put("138", "beacon");

        idMap.put("139", "cobblestone_wall");
        idMap.put("139:1", "cobblestone_wall");

        idMap.put("140", "flower_pot");
        idMap.put("141", "carrots");
        idMap.put("142", "potatoes");
        idMap.put("143", "wooden_button");
        idMap.put("144", "skull");
        idMap.put("145", "anvil");
        idMap.put("146", "trapped_chest");
        idMap.put("147", "light_weighted_pressure_plate");
        idMap.put("148", "heavy_weighted_pressure_plate");
        idMap.put("149", "unpowered_comparator");
        idMap.put("150", "powered_comparator");
        idMap.put("151", "daylight_detector");
        idMap.put("152", "redstone_block");
        idMap.put("153", "quartz_ore");
        idMap.put("154", "hopper");

        idMap.put("155", "quartz_block");
        idMap.put("155:1", "quartz_block");
        idMap.put("155:2", "quartz_block");

        idMap.put("156", "quartz_stairs");
        idMap.put("157", "activator_rail");
        idMap.put("158", "dropper");

        idMap.put("159", "carpet");
        IntStream.range(1, 16).forEach(value -> idMap.put("159:" + value, "stained_hardened_clay"));

        idMap.put("160", "stained_glass_pane");
        IntStream.range(1, 16).forEach(value -> idMap.put("160:" + value, "stained_glass_pane"));

        idMap.put("161", "ACACIA_LEAVES");
        idMap.put("161:1", "DARK_OAK_LEAVES");

        idMap.put("162", "ACACIA_WOOD");
        idMap.put("162:1", "DARK_OAK_WOOD");

        idMap.put("163", "acacia_stairs");
        idMap.put("164", "dark_oak_stairs");
        idMap.put("165", "slime_block");
        idMap.put("166", "barrier");
        idMap.put("167", "iron_trapdoor");

        idMap.put("168", "prismarine");
        idMap.put("168:1", "prismarine");
        idMap.put("168:2", "prismarine");

        idMap.put("169", "sea_lantern");
        idMap.put("170", "hay_block");

        idMap.put("171", "carpet");
        IntStream.range(1, 16).forEach(value -> idMap.put("171:" + value, "carpet"));

        idMap.put("172", "hardened_clay");
        idMap.put("173", "coal_block");
        idMap.put("174", "packed_ice");

        idMap.put("175", "double_plant");
        idMap.put("175:1", "double_plant");
        idMap.put("175:2", "double_plant");
        idMap.put("175:3", "double_plant");
        idMap.put("175:4", "double_plant");
        idMap.put("175:5", "double_plant");

        idMap.put("176", "standing_banner");
        idMap.put("177", "wall_banner");
        idMap.put("178", "daylight_detector_inverted");

        idMap.put("179", "red_sandstone");
        idMap.put("179:1", "red_sandstone");
        idMap.put("179:2", "red_sandstone");

        idMap.put("180", "red_sandstone_stairs");
        idMap.put("181", "double_stone_slab2");
        idMap.put("182", "stone_slab2");
        idMap.put("183", "spruce_fence_gate");
        idMap.put("184", "birch_fence_gate");
        idMap.put("185", "jungle_fence_gate");
        idMap.put("186", "dark_oak_fence_gate");
        idMap.put("187", "acacia_fence_gate");
        idMap.put("188", "spruce_fence");
        idMap.put("189", "birch_fence");
        idMap.put("190", "jungle_fence");
        idMap.put("191", "dark_oak_fence");
        idMap.put("192", "acacia_fence");
        idMap.put("193", "spruce_door");
        idMap.put("194", "birch_door");
        idMap.put("195", "jungle_door");
        idMap.put("196", "acacia_door");
        idMap.put("197", "dark_oak_door");
        idMap.put("198", "end_rod");
        idMap.put("199", "chorus_plant");
        idMap.put("200", "chorus_flower");
        idMap.put("201", "purpur_block");
        idMap.put("202", "purpur_pillar");
        idMap.put("203", "purpur_stairs");
        idMap.put("204", "purpur_double_slab");
        idMap.put("205", "purpur_slab");
        idMap.put("206", "end_bricks");
        idMap.put("207", "beetroots");
        idMap.put("208", "grass_path");
        idMap.put("209", "end_gateway");
        idMap.put("210", "repeating_command_block");
        idMap.put("211", "chain_command_block");
        idMap.put("212", "frosted_ice");
        idMap.put("213", "magma");
        idMap.put("214", "nether_wart_block");
        idMap.put("215", "red_nether_brick");
        idMap.put("216", "bone_block");
        idMap.put("217", "structure_void");
        idMap.put("218", "observer");
        idMap.put("219", "white_shulker_box");
        idMap.put("220", "orange_shulker_box");
        idMap.put("221", "magenta_shulker_box");
        idMap.put("222", "light_blue_shulker_box");
        idMap.put("223", "yellow_shulker_box");
        idMap.put("224", "lime_shulker_box");
        idMap.put("225", "pink_shulker_box");
        idMap.put("226", "gray_shulker_box");
        idMap.put("227", "silver_shulker_box");
        idMap.put("228", "cyan_shulker_box");
        idMap.put("229", "purple_shulker_box");
        idMap.put("230", "blue_shulker_box");
        idMap.put("231", "brown_shulker_box");
        idMap.put("232", "green_shulker_box");
        idMap.put("233", "red_shulker_box");
        idMap.put("234", "black_shulker_box");
        idMap.put("235", "white_glazed_terracotta");
        idMap.put("236", "orange_glazed_terracotta");
        idMap.put("237", "magenta_glazed_terracotta");
        idMap.put("238", "light_blue_glazed_terracotta");
        idMap.put("239", "yellow_glazed_terracotta");
        idMap.put("240", "lime_glazed_terracotta");
        idMap.put("241", "pink_glazed_terracotta");
        idMap.put("242", "gray_glazed_terracotta");
        idMap.put("243", "light_gray_glazed_terracotta");
        idMap.put("244", "cyan_glazed_terracotta");
        idMap.put("245", "purple_glazed_terracotta");
        idMap.put("246", "blue_glazed_terracotta");
        idMap.put("247", "brown_glazed_terracotta");
        idMap.put("248", "green_glazed_terracotta");
        idMap.put("249", "red_glazed_terracotta");
        idMap.put("250", "black_glazed_terracotta");

        idMap.put("251", "concrete_powder");
        IntStream.range(1, 16).forEach(value -> idMap.put("251:" + value, "concrete_powder"));

        idMap.put("252", "concrete_powder");
        IntStream.range(1, 16).forEach(value -> idMap.put("252:" + value, "concrete_powder"));

        idMap.put("255", "structure_block");
        idMap.put("256", "iron_shovel");
        idMap.put("257", "iron_pickaxe");
        idMap.put("258", "iron_axe");
        idMap.put("259", "flint_and_steel");
        idMap.put("260", "apple");
        idMap.put("261", "bow");
        idMap.put("262", "arrow");
        idMap.put("263", "coal");
        idMap.put("263:1", "coal");
        idMap.put("264", "diamond");
        idMap.put("265", "iron_ingot");
        idMap.put("266", "gold_ingot");
        idMap.put("267", "iron_sword");
        idMap.put("268", "wooden_sword");
        idMap.put("269", "wooden_shovel");
        idMap.put("270", "wooden_pickaxe");
        idMap.put("271", "wooden_axe");
        idMap.put("272", "stone_sword");
        idMap.put("273", "stone_shovel");
        idMap.put("274", "stone_pickaxe");
        idMap.put("275", "stone_axe");
        idMap.put("276", "diamond_sword");
        idMap.put("277", "diamond_shovel");
        idMap.put("278", "diamond_pickaxe");
        idMap.put("279", "diamond_axe");
        idMap.put("280", "stick");
        idMap.put("281", "bowl");
        idMap.put("282", "mushroom_stew");
        idMap.put("283", "golden_sword");
        idMap.put("284", "golden_shovel");
        idMap.put("285", "golden_pickaxe");
        idMap.put("286", "golden_axe");
        idMap.put("287", "string");
        idMap.put("288", "feather");
        idMap.put("289", "gunpowder");
        idMap.put("290", "wooden_hoe");
        idMap.put("291", "stone_hoe");
        idMap.put("292", "iron_hoe");
        idMap.put("293", "diamond_hoe");
        idMap.put("294", "golden_hoe");
        idMap.put("295", "wheat_seeds");
        idMap.put("296", "wheat");
        idMap.put("297", "bread");
        idMap.put("298", "leather_helmet");
        idMap.put("299", "leather_chestplate");
        idMap.put("300", "leather_leggings");
        idMap.put("301", "leather_boots");
        idMap.put("302", "chainmail_helmet");
        idMap.put("303", "chainmail_chestplate");
        idMap.put("304", "chainmail_leggings");
        idMap.put("305", "chainmail_boots");
        idMap.put("306", "iron_helmet");
        idMap.put("307", "iron_chestplate");
        idMap.put("308", "iron_leggings");
        idMap.put("309", "iron_boots");
        idMap.put("310", "diamond_helmet");
        idMap.put("311", "diamond_chestplate");
        idMap.put("312", "diamond_leggings");
        idMap.put("313", "diamond_boots");
        idMap.put("314", "golden_helmet");
        idMap.put("315", "golden_chestplate");
        idMap.put("316", "golden_leggings");
        idMap.put("317", "golden_boots");
        idMap.put("318", "flint");
        idMap.put("319", "porkchop");
        idMap.put("320", "cooked_porkchop");
        idMap.put("321", "painting");

        idMap.put("322", "golden_apple");
        idMap.put("322:1", "golden_apple");

        idMap.put("323", "sign");
        idMap.put("324", "wooden_door");
        idMap.put("325", "bucket");
        idMap.put("326", "water_bucket");
        idMap.put("327", "lava_bucket");
        idMap.put("328", "minecart");
        idMap.put("329", "saddle");
        idMap.put("330", "iron_door");
        idMap.put("331", "redstone");
        idMap.put("332", "snowball");
        idMap.put("333", "boat");
        idMap.put("334", "leather");
        idMap.put("335", "milk_bucket");
        idMap.put("336", "brick");
        idMap.put("337", "clay_ball");
        idMap.put("338", "reeds");
        idMap.put("339", "paper");
        idMap.put("340", "book");
        idMap.put("341", "slime_ball");
        idMap.put("342", "chest_minecart");
        idMap.put("343", "furnace_minecart");
        idMap.put("344", "egg");
        idMap.put("345", "compass");
        idMap.put("346", "fishing_rod");
        idMap.put("347", "clock");
        idMap.put("348", "glowstone_dust");

        idMap.put("349", "fish");
        idMap.put("349:1", "fish");
        idMap.put("349:2", "fish");
        idMap.put("349:3", "fish");

        idMap.put("350", "cooked_fish");
        idMap.put("350:1", "cooked_fish");

        idMap.put("351", "dye");
        IntStream.range(1, 16).forEach(value -> idMap.put("351:" + value, "dye"));

        idMap.put("352", "bone");
        idMap.put("353", "sugar");
        idMap.put("354", "cake");
        idMap.put("355", "bed");
        idMap.put("356", "repeater");
        idMap.put("357", "cookie");
        idMap.put("358", "filled_map");
        idMap.put("359", "shears");
        idMap.put("360", "melon");
        idMap.put("361", "pumpkin_seeds");
        idMap.put("362", "melon_seeds");
        idMap.put("363", "beef");
        idMap.put("364", "cooked_beef");
        idMap.put("365", "chicken");
        idMap.put("366", "cooked_chicken");
        idMap.put("367", "rotten_flesh");
        idMap.put("368", "ender_pearl");
        idMap.put("369", "blaze_rod");
        idMap.put("370", "ghast_tear");
        idMap.put("371", "gold_nugget");
        idMap.put("372", "nether_wart");
        idMap.put("373", "potion");
        idMap.put("374", "glass_bottle");
        idMap.put("375", "spider_eye");
        idMap.put("376", "fermented_spider_eye");
        idMap.put("377", "blaze_powder");
        idMap.put("378", "magma_cream");
        idMap.put("379", "brewing_stand");
        idMap.put("380", "cauldron");
        idMap.put("381", "ender_eye");
        idMap.put("382", "speckled_melon");

        idMap.put("383", "MONSTER_EGG");
        IntStream.range(1, 121).forEach(value -> idMap.put("383:" + value, "MONSTER_EGG"));

        idMap.put("384", "experience_bottle");
        idMap.put("385", "fire_charge");
        idMap.put("386", "writable_book");
        idMap.put("387", "written_book");
        idMap.put("388", "emerald");
        idMap.put("389", "item_frame");
        idMap.put("390", "flower_pot");
        idMap.put("391", "carrot");
        idMap.put("392", "potato");
        idMap.put("393", "baked_potato");
        idMap.put("394", "poisonous_potato");
        idMap.put("395", "map");
        idMap.put("396", "golden_carrot");

        idMap.put("397", "skull");
        idMap.put("397:1", "skull");
        idMap.put("397:2", "skull");
        idMap.put("397:3", "skull");
        idMap.put("397:4", "skull");
        idMap.put("397:5", "skull");

        idMap.put("398", "carrot_on_a_stick");
        idMap.put("399", "nether_star");
        idMap.put("400", "pumpkin_pie");
        idMap.put("401", "fireworks");
        idMap.put("402", "firework_charge");
        idMap.put("403", "enchanted_book");
        idMap.put("404", "comparator");
        idMap.put("405", "netherbrick");
        idMap.put("406", "quartz");
        idMap.put("407", "tnt_minecart");
        idMap.put("408", "hopper_minecart");
        idMap.put("409", "prismarine_shard");
        idMap.put("410", "prismarine_crystals");
        idMap.put("411", "rabbit");
        idMap.put("412", "cooked_rabbit");
        idMap.put("413", "rabbit_stew");
        idMap.put("414", "rabbit_foot");
        idMap.put("415", "rabbit_hide");
        idMap.put("416", "armor_stand");
        idMap.put("417", "iron_horse_armor");
        idMap.put("418", "golden_horse_armor");
        idMap.put("419", "diamond_horse_armor");
        idMap.put("420", "lead");
        idMap.put("421", "name_tag");
        idMap.put("422", "command_block_minecart");
        idMap.put("423", "mutton");
        idMap.put("424", "cooked_mutton");
        idMap.put("425", "banner");
        idMap.put("426", "end_crystal");
        idMap.put("427", "spruce_door");
        idMap.put("428", "birch_door");
        idMap.put("429", "jungle_door");
        idMap.put("430", "acacia_door");
        idMap.put("431", "dark_oak_door");
        idMap.put("432", "chorus_fruit");
        idMap.put("433", "popped_chorus_fruit");
        idMap.put("434", "beetroot");
        idMap.put("435", "beetroot_seeds");
        idMap.put("436", "beetroot_soup");
        idMap.put("437", "dragon_breath");
        idMap.put("438", "splash_potion");
        idMap.put("439", "spectral_arrow");
        idMap.put("440", "tipped_arrow");
        idMap.put("441", "lingering_potion");
        idMap.put("442", "shield");
        idMap.put("443", "elytra");
        idMap.put("444", "spruce_boat");
        idMap.put("445", "birch_boat");
        idMap.put("446", "jungle_boat");
        idMap.put("447", "acacia_boat");
        idMap.put("448", "dark_oak_boat");
        idMap.put("449", "totem_of_undying");
        idMap.put("450", "shulker_shell");
        idMap.put("452", "iron_nugget");
        idMap.put("453", "knowledge_book");

        idMap.put("2256", "record_13");
        idMap.put("2257", "record_cat");
        idMap.put("2258", "record_blocks");
        idMap.put("2259", "record_chirp");
        idMap.put("2260", "record_far");
        idMap.put("2261", "record_mall");
        idMap.put("2262", "record_mellohi");
        idMap.put("2263", "record_stal");
        idMap.put("2264", "record_strad");
        idMap.put("2265", "record_ward");
        idMap.put("2266", "record_11");
        idMap.put("2267", "record_wait");
    }
}
