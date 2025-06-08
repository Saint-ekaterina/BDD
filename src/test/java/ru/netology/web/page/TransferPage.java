package ru.netology.web.page;

import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {



    public void transferMoney(String amount, DataHelper.CardInfo info) {
        $("[data-test-id='amount'] .input__control").setValue(amount);
        $("[data-test-id='from'] .input__control").setValue(info.getCardNumber());
        $("[data-test-id='action-transfer']").click();
    }


}