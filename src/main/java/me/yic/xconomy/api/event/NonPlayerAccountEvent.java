package me.yic.xconomy.api.event;

import java.math.BigDecimal;

public class NonPlayerAccountEvent extends AccountEvent {
    public NonPlayerAccountEvent(String account, BigDecimal balance, BigDecimal amount, Boolean isadd, String method) {
        super(account, balance, amount, isadd, method);
    }
}
