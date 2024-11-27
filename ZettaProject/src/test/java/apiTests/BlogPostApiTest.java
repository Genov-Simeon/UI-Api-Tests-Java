package apiTests;

import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class BlogPostApiTest {
    private static final String BASE_URL = System.getenv("BASE_URL");
    private static final String DATA_PROVIDER_NAME = "userPostCounts";

    @DataProvider(name = DATA_PROVIDER_NAME)
    public Object[][] getUserPostCounts() {
        return new Object[][] {
            {5, 10},
            {7, 10},
            {9, 10}
        };
    }

    @Test(dataProvider = DATA_PROVIDER_NAME, description = "Verify post count for specific users")
    public void testUserPostCount(int userId, int expectedPosts) {
        Response response = given()
            .baseUri(BASE_URL)
            .when()
            .get("/posts")
            .then()
            .statusCode(200)
            .extract()
            .response();

        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        
        long actualPostCount = posts.stream()
            .filter(post -> (int) post.get("userId") == userId)
            .count();

        assertEquals(actualPostCount, expectedPosts, 
            String.format("User %d should have %d posts but found %d", 
                userId, expectedPosts, actualPostCount));
    }

    @Test(description = "Verify all post IDs are unique")
    public void testUniquePostIds() {
        Response response = given()
            .baseUri(BASE_URL)
            .when()
            .get("/posts")
            .then()
            .statusCode(200)
            .extract()
            .response();

        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        
        // Extract all post IDs
        List<Integer> allIds = posts.stream()
            .map(post -> (Integer) post.get("id"))
            .collect(Collectors.toList());
        
        // Convert to Set to get unique IDs
        Set<Integer> uniqueIds = allIds.stream()
            .collect(Collectors.toSet());
        
        // Compare sizes to verify uniqueness
        assertEquals(allIds.size(), uniqueIds.size(), 
            "All post IDs should be unique");
        
        // Additional verification that we have the expected number of posts
        assertEquals(allIds.size(), 100, 
            "Expected total number of posts should be 100");
    }
} 