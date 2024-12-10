package me.yic.xconomy.api.event;

import java.math.BigDecimal;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private String account;
    private BigDecimal balance;
    private BigDecimal amount;
    private Boolean isadd;
    private String method;

    public AccountEvent(String account, BigDecimal balance, BigDecimal amount, Boolean isadd, String method) {
        this.account = account;
        this.balance = balance;
        this.amount = amount;
        this.isadd = isadd;
        this.method = method;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getaccountname() {
        return this.account;
    }

    public BigDecimal getbalance() {
        return this.balance;
    }

    public BigDecimal getamount() {
        return this.amount;
    }

    public Boolean getisadd() {
        return this.isadd;
    }

    public String getmethod() {
        return this.method;
    }
}
