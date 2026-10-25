package de.bauloo.stylestudio;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Speichert pro Kategorie den gewaehlten Style (config/stylestudio.properties). */
public final class StyleConfig {
    private static final Properties DATA = new Properties();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("stylestudio.properties");
    }

    public static void load() {
        try (Reader r = Files.newBufferedReader(file())) {
            DATA.load(r);
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            DATA.store(w, "Style Studio");
        } catch (IOException ignored) {
        }
    }

    public static String get(String category) {
        return DATA.getProperty(category, "Vanilla");
    }

    public static void set(String category, String style) {
        DATA.setProperty(category, style);
        save();
    }

    public static void reset() {
        DATA.clear();
        save();
    }
}
