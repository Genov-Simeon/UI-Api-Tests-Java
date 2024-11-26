package com.amazon.tests;

import com.amazon.pages.CartPage;
import com.amazon.pages.HomePage;
import com.amazon.pages.SearchResultsPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class ProductSearchTest {
    private WebDriver driver;
    private HomePage homePage;
    private SearchResultsPage searchResultsPage;
    private CartPage cartPage;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        homePage = new HomePage(driver);
        searchResultsPage = new SearchResultsPage(driver);
        cartPage = new CartPage(driver);
    }

    @Test
    public void itemsAddedToCartCorrectly_When_AddNonDiscountedItems() {
        // Navigate to Amazon homepage and search for "laptop"
        homePage.openAndsearchProduct("laptop");

        // Add non-discounted products to cart
        List<String> addedProducts = searchResultsPage.addNonDiscountedProductsToCart();
        // Go to cart
        searchResultsPage.goToCart();

        // Verify cart contents
        List<String> cartProducts = cartPage.getCartProductTitles();
        Collections.reverse(cartProducts);
        cartPage.compareProductTitles(addedProducts, cartProducts);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 