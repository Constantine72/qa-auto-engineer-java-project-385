package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class KanbanPage extends BasePage {
    private final By burger = By.cssSelector("[data-testid='MenuIcon']");
    private final By title = By.xpath("//*[contains(text(), 'Welcome to the administration')]");
    private final By avatarButton = By.xpath("//*[contains(text(), 'Jane Doe')]");
    private final By searchField = By.xpath("//*[contains(text(), 'Lorem ipsum sic dolor amet...')]");
    private final By logoutButton = By.cssSelector("[data-testid='PowerSettingsNewIcon']");
    private final By dashboardButton = By.xpath("//*[contains(text(), 'Dashboard')]");
    private final By tasksButton = By.xpath("//*[contains(text(), 'Tasks')]");
    private final By usersButton = By.xpath("//*[contains(text(), 'Users')]");

    public KanbanPage(WebDriver driver) {
        super(driver);
    }

    public boolean isWelcomeTitleDisplayed() {

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(title));
            wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardButton));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void goToUsers() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        wait.until(ExpectedConditions.elementToBeClickable(burger)).click();

        wait.until(ExpectedConditions.elementToBeClickable(usersButton)).click();

    }

    public void clickLogout() {
        driver.findElement(avatarButton).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(logoutButton).click();
    }

    public void goToTasks() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        wait.until(ExpectedConditions.elementToBeClickable(burger)).click();

        wait.until(ExpectedConditions.elementToBeClickable(tasksButton)).click();

    }
}




