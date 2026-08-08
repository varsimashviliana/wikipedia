package ge.automation.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public final class RandomDataGenerator {

    private static final Random RANDOM = new Random();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    private RandomDataGenerator() {
    }

    public static String username() {
        return "AnaVarsimashvili" + randomNumber(5);
    }

    public static String email() {
        return "ana.varsimashvili." + randomNumber(6) + "@example.com";
    }

    public static String password() {
        return "Qa!" + randomLetters(6) + randomNumber(4);
    }

    public static String firstName() {
        String[] names = {"Ana", "Nika", "Giorgi", "Mariam", "Luka", "Elene", "Saba", "Tamar"};
        return names[RANDOM.nextInt(names.length)];
    }

    public static String lastName() {
        String[] names = {"Varsimashvili", "Beridze", "Kapanadze", "Lomidze", "Gelashvili"};
        return names[RANDOM.nextInt(names.length)];
    }

    public static int price() {
        return 50 + RANDOM.nextInt(451);
    }

    public static String today() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String daysFromNow(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private static String randomLetters(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    private static String randomNumber(int digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
