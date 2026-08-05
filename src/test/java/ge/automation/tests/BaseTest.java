package ge.automation.tests;

import ge.automation.config.ConfigReader;
import ge.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * BaseTest — ყველა ვებ ტესტის მშობელი კლასი.
 *
 * <p><b>სილაბუსი, მე-14 ლექცია:</b> TestNG-ის ანოტაციები.</p>
 *
 * <p><b>რატომ გვჭირდება?</b><br>
 * ყოველ ტესტს სჭირდება: ბრაუზერის გახსნა → ტესტის შესრულება → ბრაუზერის დახურვა.
 * თუ ამას ყოველ ტესტ-კლასში დავწერთ, კოდი გამეორდება. ამიტომ ერთხელ ვწერთ აქ
 * და ყველა ტესტ-კლასი ამ კლასს <code>extends</code>-ით იმემკვიდრებს.</p>
 *
 * <p><b>ანოტაციების თანმიმდევრობა:</b><br>
 * @BeforeSuite → (თითო ტესტზე) @BeforeMethod → ტესტი → @AfterMethod → ...
 * </p>
 */
public abstract class BaseTest {

    /** მიმდინარე ტესტის ბრაუზერი. */
    protected WebDriver driver;

    /**
     * ერთხელ სრულდება მთელ სუიტაზე — ტესტების დაწყებამდე.
     * აქ მხოლოდ კონფიგურაციას ვბეჭდავთ, რომ ლოგში ჩანდეს რა პარამეტრებით გავუშვით.
     */
    @BeforeSuite(alwaysRun = true)
    public void printConfiguration() {
        System.out.println("=".repeat(70));
        System.out.println("🚀  ვიკიპედიის ავტომატიზაციის პროექტი");
        System.out.println("=".repeat(70));
        System.out.println("   ბრაუზერი:      " + ConfigReader.get("browser"));
        System.out.println("   Headless:      " + ConfigReader.getBoolean("headless"));
        System.out.println("   Implicit wait: " + ConfigReader.getInt("implicit.wait") + "s");
        System.out.println("   Explicit wait: " + ConfigReader.getInt("explicit.wait") + "s");
        System.out.println("   API:           " + ConfigReader.get("api.base.url"));
        System.out.println("=".repeat(70));
    }

    /**
     * ყოველი ტესტის <b>წინ</b> სრულდება — ხსნის ახალ, სუფთა ბრაუზერს.
     *
     * <p>რატომ ყოველ ტესტზე ახალი? იმიტომ, რომ ტესტები ერთმანეთზე არ იმოქმედონ.
     * ეს ავტომატიზაციის ერთ-ერთი მთავარი პრინციპია — <b>ტესტების დამოუკიდებლობა</b>.</p>
     *
     * @param method TestNG თვითონ გადმოგვცემს, რომელი ტესტი იწყება
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        driver = DriverFactory.createDriver();
    }

    /**
     * ყოველი ტესტის <b>შემდეგ</b> სრულდება — ხურავს ბრაუზერს.
     *
     * <p><code>alwaysRun = true</code> ნიშნავს: შესრულდი მაშინაც კი,
     * თუ ტესტი ჩავარდა. ამის გარეშე ჩავარდნისას ბრაუზერი ღია დარჩებოდა.</p>
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    // ---------------------------------------------------------------
    //  დამხმარე მეთოდები, რომლებიც ყველა ტესტს გამოადგება
    // ---------------------------------------------------------------

    /** ხსნის მისამართს config.properties-ის გასაღებით. */
    protected void openUrlFromConfig(String configKey) {
        driver.get(ConfigReader.get(configKey));
    }
}
