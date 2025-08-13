package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SunscreensPage {
    private WebDriver driver;
    private By productBlocks = By.cssSelector("div.text-center.col-4");
    private By cartButton = By.xpath("//button[@onclick='goToCart()']");
    private By cartStatusSpan = By.xpath("//button[@onclick='goToCart()']//span");


    public SunscreensPage(WebDriver driver){
        this.driver=driver;
    }
    public void addLeastExpensiveSPF50AndSPF30ToCart() {
        List<WebElement> products = driver.findElements(productBlocks);

        WebElement cheapestSPF50 = null;
        WebElement cheapestSPF30 = null;
        int spf50MinPrice = Integer.MAX_VALUE;
        int spf30MinPrice = Integer.MAX_VALUE;

        for (WebElement product : products) {
            String name = product.findElement(By.cssSelector("p.font-weight-bold")).getText().toLowerCase();
            String priceText = product.findElement(By.xpath(".//p[contains(text(),'Price')]")).getText();
            int price = Integer.parseInt(priceText.replaceAll("[^0-9]", "")); // extract digits

            if (name.contains("spf-50") && price < spf50MinPrice) {
                spf50MinPrice = price;
                cheapestSPF50 = product;
            }

            if (name.contains("spf-30") && price < spf30MinPrice) {
                spf30MinPrice = price;
                cheapestSPF30 = product;
            }
        }

        if (cheapestSPF50 != null) {
            cheapestSPF50.findElement(By.cssSelector("button.btn")).click();
        }

        if (cheapestSPF30 != null) {
            cheapestSPF30.findElement(By.cssSelector("button.btn")).click();
        }

    }
    public String getCartStatusText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement cartStatus = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cartStatusSpan)
        );
        return cartStatus.getText().trim();
    }
    public CartPage clickCartButton() {
        driver.findElement(cartButton).click();
        return new CartPage(driver);
    }

}
