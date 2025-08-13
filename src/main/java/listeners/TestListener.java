package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import utils.ExtentReportManager;
import utils.ScreenshotUtil;

import java.io.File;
import java.util.Optional;

public class TestListener implements BeforeTestExecutionCallback, AfterTestExecutionCallback, BeforeAllCallback, AfterAllCallback {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    // Called from BaseTest.setUp() to register the current thread's driver
    public static void setDriver(WebDriver driver) {
        driverThread.set(driver);
    }
    private void cleanScreenshotFolder() {
        File screenshotDir = new File("test-output/screenshots");
        if (screenshotDir.exists()) {
            for (File file : screenshotDir.listFiles()) {
                file.delete();
            }
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        extent = ExtentReportManager.getReportInstance();
        cleanScreenshotFolder();
    }


    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        String testName = context.getDisplayName();
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
        context.getStore(ExtensionContext.Namespace.GLOBAL).put("startTime", System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Optional<Throwable> executionException = context.getExecutionException();
        String safeFileName = context.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_");

        WebDriver driver = driverThread.get(); // driver set in BaseTest.setUp()

        if (executionException.isPresent()) {
            Throwable t = executionException.get();
            // taking screenshot while driver is still active
            String screenshotPath = null;
            try {
                screenshotPath = ScreenshotUtil.capture(driver, safeFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            extentTest.get().log(Status.FAIL, "Test failed: " + t.getMessage());
            extentTest.get().fail(t);

            if (screenshotPath != null) {
                try {
                    // Pass relative path so it's clickable & visible in HTML
                    extentTest.get().addScreenCaptureFromPath(screenshotPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            extentTest.get().log(Status.PASS, "Test passed");
            String testName = context.getDisplayName();
            long duration = System.currentTimeMillis() - context.getStore(ExtensionContext.Namespace.GLOBAL).get("startTime", Long.class);
            extentTest.get().log(Status.PASS, "Test '" + testName + "' passed in " + duration + " ms.");
            extentTest.get().info("Description: This test validates the " + testName + " flow.");

        }

        extentTest.remove();
        // don't remove driverThread here — driver will be quit in BaseTest.@AfterEach
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void removeDriver() {
        driverThread.remove();
    }

}