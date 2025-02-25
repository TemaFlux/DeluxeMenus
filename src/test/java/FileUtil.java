import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static @NonNull List<File> collectYmlFiles(File directory) {
        List<File> ymlFiles = new ArrayList<>();
        if (directory != null && directory.isDirectory()) collectYmlFilesRecursively(directory, ymlFiles);
        return ymlFiles;
    }

    public static void collectYmlFilesRecursively(@NotNull File directory, List<File> ymlFiles) {
        File[] files = directory.listFiles();

        if (files != null) for (File file : files) {
            if (file.isDirectory()) collectYmlFilesRecursively(file, ymlFiles);
            else if (file.getName().endsWith(".yml")) ymlFiles.add(file);
        }
    }

    public static @NotNull YamlConfiguration loadYaml(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(file);

        return configuration;
    }
}
