package flows;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import pages.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PurchaseFlow {

    private static final String SUCCESS_MESSAGE =
            "Your payment was successful. You should receive a follow-up call from our sales team.";

    public <T> void completePurchase(T productPage) {
        CartPage cartPage;

        if (productPage instanceof MoisturizersPage) {
            MoisturizersPage page = (MoisturizersPage) productPage;
            assertEquals("Empty", page.getCartStatusText(), "Cart should be empty initially");
            page.addLeastExpensiveAloeAndAlmondProductsToCart();
            assertTrue(page.getCartStatusText().contains("2 item(s)"), "Cart should have 2 items");
            cartPage = page.clickCartButton();

        } else if (productPage instanceof SunscreensPage) {
            SunscreensPage page = (SunscreensPage) productPage;
            assertEquals("Empty", page.getCartStatusText(), "Cart should be empty initially");
            page.addLeastExpensiveSPF50AndSPF30ToCart();
            assertTrue(page.getCartStatusText().contains("2 item(s)"), "Cart should have 2 items");
            cartPage = page.clickCartButton();

        } else {
            throw new IllegalArgumentException("Unsupported product page type: " + productPage.getClass());
        }

        // Verify total price
        assertTrue(cartPage.isTotalPriceCorrect(), "Total price should match sum of product prices");

        // Payment
        cartPage.clickPayWithCard();
        ConfirmationPage confirmationPage = cartPage.completeStripePayment(
                "test@example.com",
                "4242424242424242",
                "12/30",
                "123",
                "12345"
        );

        String confirmationText = confirmationPage.getConfirmationMessage();
        String expectedFailMsg = "Oh, oh! Your payment did not go through. Please bang your head against a wall, curse the software gods and then try again.";

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(confirmationText)
                .as("Either success or fail message should match expected")
                .isIn(SUCCESS_MESSAGE, expectedFailMsg);

        if (confirmationText.equals(SUCCESS_MESSAGE)) {
            log.info("Payment succeeded: {}", confirmationText);
        } else {
            log.warn("Payment failed: {}", confirmationText);
        }

        softly.assertAll();
    }
}
