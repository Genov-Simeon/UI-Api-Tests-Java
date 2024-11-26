package com.amazon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By cartItems = By.cssSelector(".sc-list-item-content");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<String> getCartProductTitles() {
        List<String> cartProducts = new ArrayList<>();
        List<WebElement> items = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartItems));

        for (WebElement item : items) {
            cartProducts.add(item.findElement(By.cssSelector("span.sc-product-title")).getText());
        }
        return cartProducts;
    }

    public void compareProductTitles(List<String> addedProducts, List<String> cartProducts) {
        // Process cartProducts
        List<String> updatedCartProducts = processCartProducts(addedProducts, cartProducts);

        for (int i = 0; i < addedProducts.size(); i++) {
            String addedTitle = addedProducts.get(i).trim().toLowerCase();
            String cartTitle = updatedCartProducts.get(i).trim().toLowerCase();

            // Assert containment
            Assert.assertTrue(areTitlesMatching(addedTitle, cartTitle),
                    "Mismatch at index " + i + ": " + addedTitle + " does not match " + cartTitle);
        }
    }

    public boolean areTitlesMatching(String addedTitle, String cartTitle) {
        // Handle truncation: If cartTitle ends with an ellipsis (…), truncate it
        if (cartTitle.endsWith("…")) {
            cartTitle = cartTitle.substring(0, cartTitle.length() - 1).trim(); // Remove the ellipsis
        }

        // Compare truncated cartTitle with addedTitle
        return addedTitle.startsWith(cartTitle) || cartTitle.startsWith(addedTitle);
    }

    private List<String> processCartProducts(List<String> addedProducts, List<String> cartProducts) {
        // Create a new list to store the updated cartProducts
        List<String> updatedCartProducts = new ArrayList<>();

        for (int i = 0; i < cartProducts.size(); i++) {
            String addedTitle = addedProducts.get(i);
            String cartTitle = cartProducts.get(i);

            // Check if the added title starts with a number
            if (startsWithNumber(addedTitle)) {
                // Remove the prefix from the cart title
                cartTitle = removePrefix(cartTitle);
            }

            // Add the processed cart title to the updated list
            updatedCartProducts.add(cartTitle);
        }

        return updatedCartProducts;
    }

    private boolean startsWithNumber(String title) {
        // Check if the title starts with a number
        return title.trim().matches("^\\d.*");
    }

    private String removePrefix(String title) {
        // Remove the first word (prefix) and the following space
        return title.replaceFirst("^[^\\s]+\\s", "").trim();
    }
}