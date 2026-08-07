package ge.automation.tests;

import ge.automation.config.ConfigReader;
import ge.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

public abstract class BaseTest {

    protected WebDriver driver;

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

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected void openUrlFromConfig(String configKey) {
        driver.get(ConfigReader.get(configKey));
    }
}
