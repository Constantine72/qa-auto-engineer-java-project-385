package hexlet.code.tests;

import hexlet.code.utils.WebDriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

public abstract class BaseTest {

    private WebDriver driver;
    private String baseurl;

    @BeforeEach
    public final void setUp() {
        baseurl = System.getenv("APP_BASE_URL");
        if (baseurl == null || baseurl.trim().isEmpty()) {
            baseurl = "http://localhost:5173";
        }

        driver = new WebDriverFactory().createDriver();

        driver.get(baseurl);
    }

    @AfterEach
    public final void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected final WebDriver getDriver() {
        return driver;
    }
}
