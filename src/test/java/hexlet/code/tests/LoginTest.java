package hexlet.code.tests;

import hexlet.code.pages.KanbanPage;
import hexlet.code.pages.LoginPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {

    @Test
    void testSuccessfulLogin() {

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        assertTrue(kanbanPage.isWelcomeTitleDisplayed(), "The page is not loaded");
    }

    @Test
    void testLoginWithEmptyFields() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("", "");

        String actualUsernameError = loginPage.getUsernameErrorMessage();
        assertTrue(actualUsernameError.contains("Required"), "No error message displayed");

        String actualPasswordError = loginPage.getPasswordErrorMessage();
        assertTrue(actualPasswordError.contains("Required"), "No error message displayed");
    }

    @Test
    void testLoginWithEmptyFieldsConsecutively() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("qwe", "");

        assertTrue(loginPage.getCurrentUrl().contains("/login"), "Enter happened with password");

        assertTrue(loginPage.isRequiredErrorDisplayed());

        loginPage.refreshPage();

        loginPage.login("", "ASD");

        assertTrue(loginPage.getCurrentUrl().contains("/login"), "Enter happened with password");

        assertTrue(loginPage.isRequiredErrorDisplayed());
    }

    @Test
    void testSuccessfulLogout() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        assertTrue(kanbanPage.isWelcomeTitleDisplayed(), "The page is not loaded");

        kanbanPage.clickLogout();

        assertTrue(loginPage.isUsernameFieldDisplayed(), "No login page after logout");
    }
}
