package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement firstCardButton = $$("[data-test-id=action-deposit]").first();
    private SelenideElement secondCardButton = $$("[data-test-id=action-deposit]").last();

    public DashboardPage() {
        heading.shouldBeVisible();
    }

    public int getFirstCardBalance() {
        String text = $$(".list__item").first().text();
        return extractBalance(text);
    }

    public int getSecondCardBalance() {
        String text = $$(".list__item").last().text();
        return extractBalance(text);
    }

    private int extractBalance(String text) {
        String balanceStart = "баланс: ";
        String balanceFinish = " р.";
        int start = text.indexOf(balanceStart);
        int finish = text.indexOf(balanceFinish);
        String value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public MoneyTransferPage selectFirstCard() {
        firstCardButton.click();
        return new MoneyTransferPage();
    }

    public MoneyTransferPage selectSecondCard() {
        secondCardButton.click();
        return new MoneyTransferPage();
    }
}
