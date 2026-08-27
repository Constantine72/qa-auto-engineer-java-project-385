package hexlet.code.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.time.Duration;

public class WebDriverFactory {
    private static final int MIN_WAIT_TIME_SECS = 20;
    private static final int WIDTH_IN_PIXELS = 1920;
    private static final int HEIGHT_IN_PIXELS = 1080;

    public static WebDriver createDriver() {


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        driver.manage().window().setSize(new Dimension(WIDTH_IN_PIXELS, HEIGHT_IN_PIXELS));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(MIN_WAIT_TIME_SECS));

        return driver;
    }
}
