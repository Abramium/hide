package ru.abramium.hide;

import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

    private static final Path configPath = FabricLoader.getInstance().getConfigDir();

    @SneakyThrows
    public static Properties getProperties() {
        Properties properties = new Properties();
        var file = configPath.resolve("hide.properties");
        if (Files.exists(file)) {
            properties.load(new FileInputStream(file.toFile()));
        } else {
            properties.setProperty("hideParticlesWhenSneaking", "true");
            properties.setProperty("showNameTagOnClick", "true");
            properties.store(new FileOutputStream(file.toFile()), null);
        }
        return properties;
    }
}
