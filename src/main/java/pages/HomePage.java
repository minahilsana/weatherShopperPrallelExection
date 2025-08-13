package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {
    private WebDriver driver;

    private By temperature = By.xpath("//*[@id=\"temperature\"]");
    private By moisturizerButton = By.xpath("//button[text()='Buy moisturizers']");
    private By sunscreenButton = By.xpath("//button[text()='Buy sunscreens']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    // Only get the temperature value
    public int getTemperature() {
        String tempText = driver.findElement(temperature).getText().trim();
        String numericPart = tempText.replaceAll("[^\\d-]", "");
        return Integer.parseInt(numericPart);
    }

    public MoisturizersPage clickMoisturizerButton() {
        driver.findElement(moisturizerButton).click();
        return new MoisturizersPage(driver);
    }

    public SunscreensPage clickSunscreenButton() {
        driver.findElement(sunscreenButton).click();
        return new SunscreensPage(driver);
    }
}
