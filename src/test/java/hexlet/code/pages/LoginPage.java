package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public final class LoginPage extends BasePage {
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By signInButton = By.cssSelector("button");
    private final By usernameErrorLocator =
            By.xpath("//input[@name='username']/ancestor::div[contains(@class,"
                    +
                    " 'MuiFormControl-root')]//p[contains(@class, 'Mui-error')]");
    private final By passwordErrorLocator =
            By.xpath("//input[@name='password']/ancestor::div[contains(@class, "
                    +
                    "'MuiFormControl-root')]//p[contains(@class, 'Mui-error')]");
    private static final int MIN_WAIT_TIME_SECS = 5;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));

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
            return getDriver().findElement(usernameField).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameErrorMessage() {

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameErrorLocator));
        return error.getText();
    }

    public String getPasswordErrorMessage() {


        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordErrorLocator));
        return error.getText();
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
    public void refreshPage() {
        getDriver().navigate().refresh();
    }
}


