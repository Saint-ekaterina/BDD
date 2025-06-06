package ru.netology.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.pages.DashboardPage;
import ru.netology.pages.LoginPage;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
        Configuration.browser = "chrome";
        Configuration.timeout = 10000;
        Configuration.browserSize = "1920x1080";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
        closeWebDriver();
    }

    @Test
    @DisplayName("Should transfer money from first to second card")
    void shouldTransferMoneyFromFirstToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        var dashboardPage = verificationPage.validVerify(verificationCode);

        var firstCardBalance = dashboardPage.getFirstCardBalance();
        var secondCardBalance = dashboardPage.getSecondCardBalance();
        var amount = 1000;

        var transferPage = dashboardPage.selectSecondCard();
        dashboardPage = transferPage.makeTransfer(String.valueOf(amount), DataHelper.getFirstCardInfo().getNumber());

        Assertions.assertEquals(firstCardBalance - amount, dashboardPage.getFirstCardBalance());
        Assertions.assertEquals(secondCardBalance + amount, dashboardPage.getSecondCardBalance());
    }

    @Test
    void shouldTransferMoneyFromSecondToFirstCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        var dashboardPage = verificationPage.validVerify(verificationCode);

        var firstCardBalance = dashboardPage.getFirstCardBalance();
        var secondCardBalance = dashboardPage.getSecondCardBalance();
        var amount = 500;
        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;

        var transferPage = dashboardPage.selectFirstCard();
        var secondCardNumber = DataHelper.getSecondCardInfo().getNumber();
        dashboardPage = transferPage.makeTransfer(String.valueOf(amount), secondCardNumber);

        var actualFirstCardBalance = dashboardPage.getFirstCardBalance();
        var actualSecondCardBalance = dashboardPage.getSecondCardBalance();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferWhenAmountExceedsBalance() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        var dashboardPage = verificationPage.validVerify(verificationCode);

        var firstCardBalance = dashboardPage.getFirstCardBalance();
        var amount = firstCardBalance + 10000;

        var transferPage = dashboardPage.selectSecondCard();
        var firstCardNumber = DataHelper.getFirstCardInfo().getNumber();
        transferPage.makeInvalidTransfer(String.valueOf(amount), firstCardNumber);

        var errorMessage = transferPage.getErrorNotification();
        assertEquals("Ошибка! Недостаточно средств для перевода", errorMessage);
    }
}