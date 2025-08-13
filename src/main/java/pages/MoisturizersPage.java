package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.List;

public class MoisturizersPage {

    private WebDriver driver;
    private By productBlocks = By.cssSelector("div.text-center.col-4");
    private By cartButton = By.xpath("//button[@onclick='goToCart()']");
    private By cartStatusSpan = By.xpath("//button[@onclick='goToCart()']//span");


    public MoisturizersPage(WebDriver driver){
        this.driver=driver;
    }

    public void addLeastExpensiveAloeAndAlmondProductsToCart() {
        List<WebElement> products = driver.findElements(productBlocks);

        WebElement cheapestAloe = null;
        WebElement cheapestAlmond = null;
        int aloeMinPrice = Integer.MAX_VALUE;
        int almondMinPrice = Integer.MAX_VALUE;

        for (WebElement product : products) {
            String name = product.findElement(By.cssSelector("p.font-weight-bold")).getText().toLowerCase();
            String priceText = product.findElement(By.xpath(".//p[contains(text(),'Price')]")).getText();
            int price = Integer.parseInt(priceText.replaceAll("[^0-9]", "")); // extract digits

            if (name.contains("aloe") && price < aloeMinPrice) {
                aloeMinPrice = price;
                cheapestAloe = product;
            }

            if (name.contains("almond") && price < almondMinPrice) {
                almondMinPrice = price;
                cheapestAlmond = product;
            }
        }

        if (cheapestAloe != null) {
            cheapestAloe.findElement(By.cssSelector("button.btn")).click();
        }

        if (cheapestAlmond != null) {
            cheapestAlmond.findElement(By.cssSelector("button.btn")).click();
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
