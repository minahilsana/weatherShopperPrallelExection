package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import listeners.TestListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import pages.HomePage;

@ExtendWith(TestListener.class)
public class BaseTest {

    // ThreadLocal for parallel-safe WebDriver
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    protected HomePage homePage;

    protected static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    private static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    @BeforeEach
    public void setUp() {
        WebDriver driver = createDriver(System.getProperty("browser", "chrome"));
        setDriver(driver);
        getDriver().manage().window().maximize();
        getDriver().get("https://weathershopper.pythonanywhere.com/");
        homePage = new HomePage(getDriver());
    }

    private WebDriver createDriver(String browser) {
        if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver();
        }
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driverThreadLocal.remove(); // Important: clean up thread-local storage
        }
    }
}
