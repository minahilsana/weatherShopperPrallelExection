package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class ScreenshotUtil {
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) return null;
        if (!(driver instanceof TakesScreenshot)) return null;

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Save inside test-output/screenshots so report can access them easily
            Path screenshotsDir = Paths.get(System.getProperty("user.dir"), "test-output", "screenshots");
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }

            String fileName = testName + "_" + System.currentTimeMillis() + ".png";
            Path dest = screenshotsDir.resolve(fileName);
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            // Return RELATIVE path for Extent (relative to report file location)
            return "screenshots/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
