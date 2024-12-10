package me.yic.xconomy.api.event;

import java.math.BigDecimal;
import java.util.UUID;
import org.bukkit.event.HandlerList;

public class PlayerAccountEvent extends AccountEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private UUID u;
    private String reason;

    public PlayerAccountEvent(UUID u, String account, BigDecimal balance, BigDecimal amount, Boolean isadd, String reason, String method) {
        super(account, balance, amount, isadd, method);
        this.u = u;
        this.reason = reason;
    }

    public UUID getUniqueId() {
        return this.u;
    }

    public String getreason() {
        return this.reason;
    }
}
