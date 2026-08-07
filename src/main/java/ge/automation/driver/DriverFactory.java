package ge.automation.driver;

import ge.automation.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    private static void setupDriverBinary(String browser) {

        String manualPath = ConfigReader.getOrDefault("driver.path", "");
        if (!manualPath.isBlank()) {
            String property = switch (browser) {
                case "chrome" -> "webdriver.chrome.driver";
                case "firefox" -> "webdriver.gecko.driver";
                case "edge" -> "webdriver.edge.driver";
                default -> null;
            };
            if (property != null) {
                System.setProperty(property, manualPath);
                System.out.println("ℹ️  გამოიყენება ხელით მითითებული დრაივერი: " + manualPath);
                return;
            }
        }

        try {
            switch (browser) {
                case "chrome" -> WebDriverManager.chromedriver().setup();
                case "firefox" -> WebDriverManager.firefoxdriver().setup();
                case "edge" -> WebDriverManager.edgedriver().setup();
                default -> {  }
            }
        } catch (RuntimeException e) {
            System.out.println("ℹ️  WebDriverManager ვერ ჩამოტვირთა დრაივერი ("
                    + e.getClass().getSimpleName() + ").");
            System.out.println("   გადავდივართ Selenium Manager-ზე (ჩაშენებული).");
        }
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver;

        switch (browser) {
            case "chrome" -> {
                setupDriverBinary("chrome");
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--disable-notifications");
                options.addArguments("--lang=en-US");
                driver = new org.openqa.selenium.chrome.ChromeDriver(options);
            }
            case "firefox" -> {
                setupDriverBinary("firefox");
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new org.openqa.selenium.firefox.FirefoxDriver(options);
            }
            case "edge" -> {
                setupDriverBinary("edge");
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new org.openqa.selenium.edge.EdgeDriver(options);
            }
            default -> throw new IllegalArgumentException(
                    "უცნობი ბრაუზერი: '" + browser + "'. დასაშვებია: chrome, firefox, edge");
        }

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));

        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        if (ConfigReader.getBoolean("maximize")) {
            driver.manage().window().maximize();
        }

        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver არ არის შექმნილი. ჯერ createDriver() უნდა გამოიძახო.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
