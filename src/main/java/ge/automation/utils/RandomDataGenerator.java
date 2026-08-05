package ge.automation.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * RandomDataGenerator — ქმნის უნიკალურ სატესტო მონაცემებს.
 *
 * <p><b>სილაბუსი, მე-19 ლექცია:</b> "დინამიური მონაცემების გენერაცია".</p>
 *
 * <p><b>რატომ გვჭირდება?</b><br>
 * თუ ტესტში ყოველთვის ერთსა და იმავე მონაცემს ვიყენებთ (მაგ. "testuser1"),
 * მეორედ გაშვებისას სერვერი იტყვის "ასეთი უკვე არსებობს" და ტესტი ჩავარდება.
 * უნიკალური მონაცემი ამ პრობლემას ხსნის.</p>
 */
public final class RandomDataGenerator {

    private static final Random RANDOM = new Random();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    private RandomDataGenerator() {
    }

    /** უნიკალური მომხმარებლის სახელი, მაგ. "QaUser_kfmz8231". */
    public static String username() {
        return "QaUser_" + randomLetters(4) + randomNumber(4);
    }

    /** უნიკალური ელფოსტა, მაგ. "qa.test.9182@example.com". */
    public static String email() {
        return "qa.test." + randomNumber(6) + "@example.com";
    }

    /** საკმაოდ ძლიერი შემთხვევითი პაროლი. */
    public static String password() {
        return "Qa!" + randomLetters(6) + randomNumber(4);
    }

    /** შემთხვევითი სახელი, მაგ. "Nika". */
    public static String firstName() {
        String[] names = {"Nika", "Ana", "Giorgi", "Mariam", "Luka", "Elene", "Saba", "Tamar"};
        return names[RANDOM.nextInt(names.length)];
    }

    /** შემთხვევითი გვარი. */
    public static String lastName() {
        String[] names = {"Beridze", "Kapanadze", "Lomidze", "Gelashvili", "Maisuradze"};
        return names[RANDOM.nextInt(names.length)];
    }

    /** შემთხვევითი ფასი 50-დან 500-მდე. */
    public static int price() {
        return 50 + RANDOM.nextInt(451);
    }

    /** დღევანდელი თარიღი yyyy-MM-dd ფორმატში. */
    public static String today() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /** თარიღი N დღის შემდეგ. */
    public static String daysFromNow(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    // ---------------- დამხმარე ----------------

    /** N შემთხვევითი ასო. */
    private static String randomLetters(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    /** N ციფრიანი შემთხვევითი რიცხვი ტექსტად. */
    private static String randomNumber(int digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
