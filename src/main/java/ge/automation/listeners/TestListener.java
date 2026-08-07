package ge.automation.listeners;

import ge.automation.driver.DriverFactory;
import ge.automation.utils.ScreenshotUtil;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final String LINE = "-".repeat(70);

    @Override
    public void onStart(ITestContext context) {
        System.out.println(LINE);
        System.out.println("▶  იწყება: " + context.getName());
        System.out.println(LINE);
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("   ⏳  " + testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("   ✅  გავიდა: " + testName(result)
                + "  (" + duration(result) + ")");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("   ❌  ჩავარდა: " + testName(result)
                + "  (" + duration(result) + ")");

        if (result.getThrowable() != null) {
            System.out.println("       მიზეზი: " + result.getThrowable().getMessage());
        }

        try {
            String path = ScreenshotUtil.capture(DriverFactory.getDriver(), testName(result));
            if (path != null) {
                System.out.println("       📸 სქრინშოტი: " + path);
            }
        } catch (IllegalStateException e) {
            System.out.println("       (სქრინშოტი არ არის — ეს API ტესტია)");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("   ⏭️  გამოტოვდა: " + testName(result));
        if (result.getThrowable() != null) {
            System.out.println("       მიზეზი: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;

        System.out.println(LINE);
        System.out.println("📊  შედეგი — " + context.getName());
        System.out.println("    სულ:       " + total);
        System.out.println("    ✅ გავიდა:  " + passed);
        System.out.println("    ❌ ჩავარდა: " + failed);
        System.out.println("    ⏭️  გამოტოვდა: " + skipped);
        if (total > 0) {
            System.out.printf("    წარმატება: %.1f%%%n", (passed * 100.0) / total);
        }
        System.out.println(LINE);
    }

    private String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private String duration(ITestResult result) {
        long ms = result.getEndMillis() - result.getStartMillis();
        return ms + " ms";
    }
}
