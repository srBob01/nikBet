package ru.arsentiev.validator;

import ru.arsentiev.singleton.check.MoneyCheck;

public class MoneyValidator {
    private final MoneyCheck moneyCheck;

    public MoneyValidator(MoneyCheck moneyCheck) {
        this.moneyCheck = moneyCheck;
    }

    public boolean isValidMoney(String money) {
        if (money == null) {
            return false;
        }
        return moneyCheck.isCorrect(money);
    }
}
