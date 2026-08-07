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

public final class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "target/screenshots";

    private ScreenshotUtil() {
    }

    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            Path dir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(dir);

            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = dir.resolve(safeName + "_" + timestamp + ".png");

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), target);

            return target.toAbsolutePath().toString();

        } catch (IOException | ClassCastException e) {
            System.err.println("სქრინშოტის აღება ვერ მოხერხდა: " + e.getMessage());
            return null;
        }
    }
}
