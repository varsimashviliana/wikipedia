package ge.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "config.properties ვერ მოიძებნა src/test/resources საქაღალდეში!");
            }
            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new IllegalStateException("config.properties-ის წაკითხვა ვერ მოხერხდა", e);
        }
    }

    private ConfigReader() {
    }

    private static String lookup(String key) {
        String fromCommandLine = System.getProperty(key);
        if (fromCommandLine != null && !fromCommandLine.isBlank()) {
            return fromCommandLine.trim();
        }
        String fromFile = PROPERTIES.getProperty(key);
        return (fromFile == null || fromFile.isBlank()) ? null : fromFile.trim();
    }

    public static String get(String key) {
        String value = lookup(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    "config.properties-ში ვერ ვიპოვე გასაღები: '" + key + "'");
        }
        return value;
    }

    public static boolean has(String key) {
        return lookup(key) != null;
    }

    public static int getInt(String key) {
        String raw = get(key);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "'" + key + "' რიცხვი უნდა იყოს, მაგრამ არის: '" + raw + "'", e);
        }
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
