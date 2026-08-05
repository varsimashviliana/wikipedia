package ge.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader — კითხულობს config.properties ფაილს.
 *
 * <p>დავალების მე-6 პუნქტი: "გამოიყენეთ კონფიგურაცია რიდერი".</p>
 *
 * <p><b>რატომ გვჭირდება?</b><br>
 * თუ URL-ს ან ბრაუზერს პირდაპირ კოდში ჩავწერთ, შეცვლისას ყველა ფაილში მოგვიწევს ძებნა.
 * ასე კი ერთ ფაილს ვცვლით და მთელი პროექტი ახლებურად მუშაობს.</p>
 *
 * <p><b>სილაბუსის თემები, რომლებიც აქ გამოიყენება:</b>
 * static ბლოკი, ინკაფსულაცია (private კონსტრუქტორი), Exceptions (try/catch).</p>
 */
public final class ConfigReader {

    /** აქ ჩაიტვირთება ფაილის ყველა key=value წყვილი. */
    private static final Properties PROPERTIES = new Properties();

    /**
     * static ბლოკი — სრულდება ერთხელ, კლასის პირველად გამოყენებისას.
     * ანუ ფაილი მხოლოდ ერთხელ იკითხება, არა ყოველ გამოძახებაზე.
     */
    static {
        // try-with-resources: ავტომატურად დახურავს ნაკადს (stream) ბოლოს
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "config.properties ვერ მოიძებნა src/test/resources საქაღალდეში!");
            }
            PROPERTIES.load(input);

        } catch (IOException e) {
            // Exceptions — სილაბუსის მე-8 ლექცია (Try/Catch)
            throw new IllegalStateException("config.properties-ის წაკითხვა ვერ მოხერხდა", e);
        }
    }

    /** private კონსტრუქტორი — ამ კლასის ობიექტის შექმნა არავის სჭირდება (utility class). */
    private ConfigReader() {
    }

    /**
     * შიდა მეთოდი — ეძებს მნიშვნელობას ორ ადგილას, პრიორიტეტით:
     * <ol>
     *   <li>ბრძანების ხაზი (-Dbrowser=firefox)</li>
     *   <li>config.properties ფაილი</li>
     * </ol>
     *
     * <p>ასე შეგვიძლია ფაილის შეცვლის გარეშე გავუშვათ ტესტები
     * სხვა პარამეტრებით, მაგალითად:
     * <code>mvn test -Dheadless=true -Dbrowser=firefox</code></p>
     */
    private static String lookup(String key) {
        String fromCommandLine = System.getProperty(key);
        if (fromCommandLine != null && !fromCommandLine.isBlank()) {
            return fromCommandLine.trim();
        }
        String fromFile = PROPERTIES.getProperty(key);
        return (fromFile == null || fromFile.isBlank()) ? null : fromFile.trim();
    }

    /**
     * აბრუნებს ტექსტურ მნიშვნელობას.
     *
     * @param key გასაღები, მაგ. "browser"
     * @return მნიშვნელობა, მაგ. "chrome"
     * @throws IllegalArgumentException თუ ასეთი გასაღები ვერსად მოიძებნა
     */
    public static String get(String key) {
        String value = lookup(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    "config.properties-ში ვერ ვიპოვე გასაღები: '" + key + "'");
        }
        return value;
    }

    /**
     * აბრუნებს მნიშვნელობას, ან ნაგულისხმევს — თუ გასაღები ფაილში არ არის
     * ან ცარიელია.
     *
     * <p>ამას ვიყენებთ არასავალდებულო პარამეტრებისთვის, მაგალითად
     * რეალური ლოგინის მონაცემები — თუ არ არის შევსებული,
     * შესაბამისი ტესტი უბრალოდ გამოტოვდება, ჩავარდნის ნაცვლად.</p>
     */
    public static String getOrDefault(String key, String defaultValue) {
        String value = lookup(key);
        return (value == null) ? defaultValue : value;
    }

    /** ამოწმებს, შევსებულია თუ არა გასაღები. */
    public static boolean has(String key) {
        return lookup(key) != null;
    }

    /**
     * აბრუნებს რიცხვს (მაგ. ლოდინის წამები).
     * Integer.parseInt-ს შეუძლია გამონაკლისი ისროლოს, ამიტომ try/catch გვაქვს.
     */
    public static int getInt(String key) {
        String raw = get(key);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "'" + key + "' რიცხვი უნდა იყოს, მაგრამ არის: '" + raw + "'", e);
        }
    }

    /** აბრუნებს true/false მნიშვნელობას (მაგ. headless). */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
