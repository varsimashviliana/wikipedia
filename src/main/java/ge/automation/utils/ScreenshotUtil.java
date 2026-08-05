package ge.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil — ტესტის ჩავარდნისას ეკრანის სურათს იღებს.
 *
 * <p><b>სილაბუსი, მე-15 და მე-20 ლექცია:</b> რეპორტინგი.<br>
 * როცა ტესტი ჩავარდება, ლოგში მხოლოდ ტექსტი წერია. სქრინშოტი
 * გაცილებით სწრაფად გვაჩვენებს, რა მოხდა ეკრანზე რეალურად.</p>
 */
public final class ScreenshotUtil {

    /** სად შეინახოს სურათები. */
    private static final String SCREENSHOT_DIR = "target/screenshots";

    private ScreenshotUtil() {
    }

    /**
     * იღებს სქრინშოტს და ინახავს ფაილად.
     *
     * @param driver   ბრაუზერი
     * @param testName ტესტის სახელი — ფაილს ასე დაერქმევა
     * @return შენახული ფაილის გზა, ან null თუ ვერ მოხერხდა
     */
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        try {
            // დროის ნიშნული, რომ ფაილები ერთმანეთს არ გადააწერონ
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            Path dir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(dir);

            // სახელიდან ვშლით სიმბოლოებს, რომლებიც ფაილის სახელში არ შეიძლება
            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = dir.resolve(safeName + "_" + timestamp + ".png");

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), target);

            return target.toAbsolutePath().toString();

        } catch (IOException | ClassCastException e) {
            // Exceptions — სქრინშოტის ჩავარდნამ ტესტი არ უნდა გააფუჭოს
            System.err.println("სქრინშოტის აღება ვერ მოხერხდა: " + e.getMessage());
            return null;
        }
    }
}
