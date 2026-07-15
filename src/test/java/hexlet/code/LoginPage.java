package hexlet.code;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import java.time.Duration;


public class LoginPage {
    private final WebDriver driver;

    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By signInButton = By.cssSelector("button");
    private final By usernameErrorLocator = By.xpath("//input[@name='username']/ancestor::div[contains(@class, 'MuiFormControl-root')]//p[contains(@class, 'Mui-error')]");
    private final By passwordErrorLocator = By.xpath("//input[@name='password']/ancestor::div[contains(@class, 'MuiFormControl-root')]//p[contains(@class, 'Mui-error')]");


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

    public String getUsernameErrorMessage() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameErrorLocator));
        return error.getText();
    }

    public String getPasswordErrorMessage() {


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordErrorLocator));
        return error.getText();
    }
    public boolean isRequiredErrorDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By errorLocator = By.xpath("//*[contains(text(), 'Required')]");

        try {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}


