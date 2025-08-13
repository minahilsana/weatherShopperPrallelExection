package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmationPage {

    private WebDriver driver;

    // Assuming this is the locator for the payment confirmation text
    private By confirmationMessage = By.xpath("(//div//p)[1]");

    public ConfirmationPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getConfirmationMessage() {
        return driver.findElement(confirmationMessage).getText().trim();
    }
}
