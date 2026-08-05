package ge.automation.driver;

import ge.automation.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory — ქმნის და მართავს WebDriver-ს.
 *
 * <p><b>დავალების 1-ლი პუნქტი:</b> "შერჩეული დრაივერი რომელსაც იმუშავებს ყველასთან".</p>
 *
 * <p><b>როგორ ვაგვარებთ?</b><br>
 * ჩვეულებრივ chromedriver.exe ხელით უნდა ჩამოტვირთო და კომპიუტერში ჩასვა.
 * ეს პრობლემაა — სხვა კომპიუტერზე ვერსია არ დაემთხვევა და ტესტი ჩავარდება.
 * <b>WebDriverManager</b> ამას ავტომატურად აკეთებს: ხედავს რომელი Chrome გაქვს
 * და შესაბამის დრაივერს თვითონ ჩამოტვირთავს. ამიტომ პროექტი ნებისმიერ
 * კომპიუტერზე მუშაობს — არაფრის ხელით დაყენება არ სჭირდება.</p>
 *
 * <p><b>ThreadLocal რატომ?</b><br>
 * თუ ტესტები პარალელურად გაეშვა, თითოეულს თავისი ბრაუზერი უნდა ჰქონდეს.
 * ThreadLocal ზუსტად ამას აკეთებს — ინახავს ცალკე ასლს თითო ნაკადისთვის.</p>
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    /**
     * ამზადებს ბრაუზერის დრაივერს (მაგ. chromedriver.exe).
     *
     * <p><b>ორი ფენა — რატომ?</b></p>
     * <ol>
     *   <li><b>WebDriverManager</b> — ცდილობს დრაივერის ჩამოტვირთვას.</li>
     *   <li><b>Selenium Manager</b> — თუ პირველი ჩავარდა, Selenium-ის
     *       ჩაშენებული მექანიზმი (4.6 ვერსიიდან) აკეთებს იმავეს.</li>
     * </ol>
     *
     * <p>რატომ გვჭირდება ორივე? ზოგიერთ ქსელში (ოფისი, უნივერსიტეტი)
     * უსაფრთხოების მოწყობილობა HTTPS-ს ამოწმებს და WebDriverManager-ის
     * ჩამოტვირთვას წყვეტს (<code>Connection reset</code>). Selenium Manager
     * კი სისტემის სერტიფიკატებს იყენებს და მუშაობს.</p>
     *
     * <p>ასე პროექტი <b>ნებისმიერ კომპიუტერზე და ნებისმიერ ქსელში</b> გაეშვება —
     * ზუსტად ის, რასაც დავალების 1-ლი პუნქტი ითხოვს.</p>
     */
    private static void setupDriverBinary(String browser) {

        // --- ფენა 0: ხელით მითითებული დრაივერი ---
        // თუ config.properties-ში driver.path შევსებულია, ის გამოიყენება
        // და ინტერნეტიდან არაფერი ჩამოიტვირთება.
        // ეს გვჭირდება ისეთ ქსელებში, სადაც დრაივერების სერვერები დაბლოკილია.
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

        // --- ფენა 1: WebDriverManager ---
        try {
            switch (browser) {
                case "chrome" -> WebDriverManager.chromedriver().setup();
                case "firefox" -> WebDriverManager.firefoxdriver().setup();
                case "edge" -> WebDriverManager.edgedriver().setup();
                default -> { /* სხვა ბრაუზერი — Selenium Manager მოაგვარებს */ }
            }
        } catch (RuntimeException e) {
            // Exceptions — სილაბუსის მე-8 ლექცია.
            // ჩავარდნა არ არის ფატალური: ფენა 2 (Selenium Manager) სცდის.
            System.out.println("ℹ️  WebDriverManager ვერ ჩამოტვირთა დრაივერი ("
                    + e.getClass().getSimpleName() + ").");
            System.out.println("   გადავდივართ Selenium Manager-ზე (ჩაშენებული).");
        }
        // --- ფენა 2: Selenium Manager --- ავტომატურად ირთვება,
        // როცა ChromeDriver-ს ვქმნით და დრაივერი ჯერ არ არის მზად.
    }

    /**
     * ქმნის ბრაუზერს config.properties-ში მითითებული პარამეტრებით.
     */
    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver;

        // switch — სილაბუსის მე-4 ლექცია (Switch Statements)
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

        // --- Implicit Wait (სილაბუსის მე-13 ლექცია) ---
        // Selenium ყოველ ელემენტს ავტომატურად დაელოდება მითითებულ დრომდე
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));

        // გვერდის ჩატვირთვის ლიმიტი
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        if (ConfigReader.getBoolean("maximize")) {
            driver.manage().window().maximize();
        }

        DRIVER.set(driver);
        return driver;
    }

    /** აბრუნებს მიმდინარე ნაკადის ბრაუზერს. */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver არ არის შექმნილი. ჯერ createDriver() უნდა გამოიძახო.");
        }
        return driver;
    }

    /** ხურავს ბრაუზერს და ასუფთავებს ThreadLocal-ს (მეხსიერების გაჟონვის თავიდან ასაცილებლად). */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
