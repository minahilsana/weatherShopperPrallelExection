package tests;

import base.BaseTest;
import flows.PurchaseFlow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class PurchaseFlowTest extends BaseTest {

    private final PurchaseFlow purchaseFlow = new PurchaseFlow();

    @Test
    @DisplayName("Verify temperature is within realistic range")
    public void verifyTemperatureIsRealistic() {
        int temp = homePage.getTemperature();
        log.info("Current temperature: {}°C", temp);
        assertTrue(temp > -10 && temp < 60, "Temperature should be between -10°C and 60°C");
    }

    @Test
    @DisplayName("Buying Moisturizer")
    public void verifyMoisturizersPurchaseFlow() {
        purchaseFlow.completePurchase(homePage.clickMoisturizerButton());
    }

    @Test
    @DisplayName("Buying Sunscreen")
    public void verifySunscreensPurchaseFlow() {
        purchaseFlow.completePurchase(homePage.clickSunscreenButton());
    }

    @Test
    @DisplayName("Complete Flow")
    public void purchaseBasedOnTemperature() {
        int temp = homePage.getTemperature();
        log.info("Current temperature: {}°C", temp);

        if (temp < 19) {
            log.info("Temperature < 19°C → Buying moisturizers");
            purchaseFlow.completePurchase(homePage.clickMoisturizerButton());

        } else if (temp > 34) {
            log.info("Temperature > 34°C → Buying sunscreens");
            purchaseFlow.completePurchase(homePage.clickSunscreenButton());

        } else {
            log.info("Temperature between 19°C and 34°C → No purchase made");
            assertTrue(temp >= 19 && temp <= 34, "Temperature is in no-purchase range");
        }
    }
}
