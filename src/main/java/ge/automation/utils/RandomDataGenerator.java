package ge.automation.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public final class RandomDataGenerator {

    private static final Random RANDOM = new Random();

    private RandomDataGenerator() {
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
}
