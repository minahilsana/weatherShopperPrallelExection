package pages;

import org.openqa.selenium.By;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CartPage {

    private WebDriver driver;

    private By productNames = By.xpath("//table[@class='table table-striped']/tbody/tr/td[1]");
    private By productPrices = By.xpath("//table[@class='table table-striped']/tbody/tr/td[2]");
    private By totalPrice = By.xpath("//p[@id='total']");

    private By payWithCardButton = By.xpath("//button[contains(@class, 'stripe-button-el')]");

    private By stripeIframe = By.xpath("//iframe[@name='stripe_checkout_app']");
    private By emailField = By.xpath("//input[@type='email']");
    private By cardNumberField = By.xpath("//input[@placeholder='Card number']");
    private By expiryField = By.xpath("//input[@placeholder='MM / YY']");
    private By cvcField = By.xpath("//input[@placeholder='CVC']");
    private By zipField = By.xpath("//input[@placeholder='ZIP Code']");
    private By payButtonInIframe = By.xpath("//button[@type='submit' and @id='submitButton']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // --- Product info ---
    public List<String> getProductNames() {
        return driver.findElements(productNames)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Integer> getProductPrices() {
        return driver.findElements(productPrices)
                .stream()
                .map(e -> Integer.parseInt(e.getText().trim()))
                .collect(Collectors.toList());
    }

    public String getTotalPriceText() {
        return driver.findElement(totalPrice).getText().trim();
    }

    // --- Payment flow ---
    public void clickPayWithCard() {
        driver.findElement(payWithCardButton).click();
    }

    private void typeSlowly(WebElement element, String text, long delayMillis) {
        for (char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public ConfirmationPage completeStripePayment(String email, String cardNumber, String expiry, String cvc, String zip) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Wait for iframe to be ready and switch into it
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(stripeIframe));

        WebElement emailElement=wait.until(ExpectedConditions.elementToBeClickable(emailField));
        typeSlowly(emailElement,email,100);

        //  Type card number slowly to avoid Stripe field input issues
        WebElement cardNumberElement = wait.until(ExpectedConditions.elementToBeClickable(cardNumberField));
        typeSlowly(cardNumberElement, cardNumber, 100);

        //  Type expiry slowly
        WebElement expiryElement = wait.until(ExpectedConditions.elementToBeClickable(expiryField));
        typeSlowly(expiryElement, expiry, 100);

        //  CVC entry
        wait.until(ExpectedConditions.elementToBeClickable(cvcField)).sendKeys(cvc);

        //  ZIP entry
        WebElement zipElement = wait.until(ExpectedConditions.elementToBeClickable(zipField));
        zipElement.sendKeys(zip);

        //  Click pay button
        wait.until(ExpectedConditions.elementToBeClickable(payButtonInIframe)).click();

        driver.switchTo().defaultContent();

        wait.until(ExpectedConditions.urlContains("/confirmation"));

        return new ConfirmationPage(driver);
    }

    public boolean isTotalPriceCorrect() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for at least one price cell to have non-empty text
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(productPrices, 0));
        wait.until(driver -> driver.findElements(productPrices)
                .stream()
                .allMatch(e -> !e.getText().trim().isEmpty()));

        int sum = getProductPrices().stream().mapToInt(Integer::intValue).sum();
        int total = Integer.parseInt(getTotalPriceText().replaceAll("\\D+", ""));

        log.info("Sum of product prices = {}", sum);
        log.info("Total from UI         = {}", total);

        return sum == total;
    }

}
