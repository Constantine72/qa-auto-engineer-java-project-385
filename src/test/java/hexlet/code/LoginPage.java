package hexlet.code;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;

    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By signInButton = By.cssSelector("button");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
//        driver.findElement(usernameField).clear();
//        driver.findElement(usernameField).sendKeys(username);
//        driver.findElement(passwordField).clear();
//        driver.findElement(passwordField).sendKeys(password);
//        driver.findElement(signInButton).click();

        //removing old clicks for waits 30-41
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement usernameEl = wait.until(ExpectedConditions.elementToBeClickable(usernameField));
        usernameEl.clear();
        usernameEl.sendKeys(username);

        WebElement passwordEl = wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passwordEl.clear();
        passwordEl.sendKeys(password);

        WebElement signInBtnEl = wait.until(ExpectedConditions.elementToBeClickable(signInButton));
        signInBtnEl.click();


    }

    public boolean isUsernameFieldDisplayed() {
        try {
            return driver.findElement(usernameField).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}


