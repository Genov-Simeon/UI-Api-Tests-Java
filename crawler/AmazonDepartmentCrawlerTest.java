package crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.cdimascio.dotenv.Dotenv;

public class AmazonDepartmentCrawlerTest {
    private WebDriver driver;
    private Set<String> visitedLinks;
    private List<String> results;
    private String baseUrl;

    @BeforeTest
    public void setup() {
        driver = new ChromeDriver();
        visitedLinks = new HashSet<>();
        results = new ArrayList<>();
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .filename(".env.example")
                .load();

        // Fetch environment variables
        baseUrl = dotenv.get("BASE_URL");
    }

    @Test
    public void testDepartmentLinks() {
        driver.get(baseUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "All" menu button to open departments
        WebElement allMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu")));
        allMenu.click();

        // Get all department links
        List<WebElement> departmentLinks = new ArrayList<>();

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='hmenu-content']/ul[@data-menu-id='26']")));
        for (int i = 5; i <= 26; i++) {
            departmentLinks.addAll(driver.findElements(
                    By.xpath(String.format("//div[@id='hmenu-content']/ul[@data-menu-id='5'][1]//div[@role='heading']/../following-sibling::li/a", i))));
        }

        // Store original links before visiting them
        List<String> links = new ArrayList<>();
        for (WebElement link : departmentLinks) {
            links.add(link.getAttribute("href"));
        }

        // Visit each link and check its status
        for (String link : links) {
            if (!visitedLinks.contains(link)) {
                checkLink(link);
            }
        }

        // Write results to file
        writeResultsToFile();
    }

    private void checkLink(String link) {
        if (link == null || link.isEmpty())
            return;

        try {
            driver.get(link);
            String title = driver.getTitle();
            String status = "OK";

            // Add to visited links
            visitedLinks.add(link);

            // Store result
            results.add(String.format("%s, %s, %s", link, title, status));

        } catch (Exception e) {
            results.add(String.format("%s, N/A, Dead link", link));
        }
    }

    private void writeResultsToFile() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = timestamp + "_results.txt";

        try {
            try (FileWriter writer = new FileWriter(fileName)) {
                for (String result : results) {
                    writer.write(result + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}