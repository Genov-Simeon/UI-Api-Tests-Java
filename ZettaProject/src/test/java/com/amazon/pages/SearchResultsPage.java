package com.amazon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By productList = By.cssSelector("[data-component-type='s-search-result']");
    private By priceWhole = By.cssSelector(".a-price-whole");
    private By addToCartButton = By.id("add-to-cart-button");
    private By cartLink = By.id("nav-cart");

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<String> addNonDiscountedProductsToCart() {
        List<String> addedProducts = new ArrayList<>();

        // Locate the product list initially
        List<WebElement> products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productList));

        for (int i = 0; i < products.size(); i++) {
            try {
                // Re-locate the product to avoid StaleElementReferenceException
                WebElement product = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productList)).get(i);

                if (isProductInStock(product) && !isProductDiscounted(product)) {
                    String productTitle = getProductTitle(product);
                    clickProduct(product);
                    addToCart();
                    addedProducts.add(productTitle);

                    // Navigate back and wait for the product list to reload
                    driver.navigate().back();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
                    driver.navigate().back();
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productList));
                }
            } catch (StaleElementReferenceException e) {
                // Re-locate the products list if stale element exception occurs and retry
                products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productList));
                i--; // Decrement index to retry the same product
            }
        }

        return addedProducts;
    }

    private boolean isProductInStock(WebElement product) {
        try {
            return !product.findElement(By.cssSelector("a.a-button-text")).isDisplayed();
        } catch (Exception e) {
            return true;
        }
    }

    private boolean isProductDiscounted(WebElement product) {
        try {
            return product.findElement(By.cssSelector(".a-text-price")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private String getProductTitle(WebElement product) {
        return product.findElement(By.cssSelector("h2")).getText();
    }

    private void clickProduct(WebElement product) {
        product.findElement(By.cssSelector("h2 a")).click();
    }

    private void addToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
    }

    public void goToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
    }
} 