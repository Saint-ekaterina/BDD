package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.pages.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferMoneyFromFirstToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        var dashboardPage = verificationPage.validVerify(verificationCode);

        var firstCardBalance = dashboardPage.getFirstCardBalance();
        var secondCardBalance = dashboardPage.getSecondCardBalance();
        var amount = 1000;
        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;

        var transferPage = dashboardPage.selectSecondCard();
        var firstCardNumber = DataHelper.getFirstCardInfo().getNumber();
        dashboardPage = transferPage.makeTransfer(String.valueOf(amount), firstCardNumber);

        var actualFirstCardBalance = dashboardPage.getFirstCardBalance();
        var actualSecondCardBalance = dashboardPage.getSecondCardBalance();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
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