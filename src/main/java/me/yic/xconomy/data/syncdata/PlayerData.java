package me.yic.xconomy.data.syncdata;

import java.math.BigDecimal;
import java.util.UUID;
import me.yic.xconomy.info.SyncType;

public class PlayerData extends SyncData {
    private final String name;
    private BigDecimal balance;

    public PlayerData(String sign, UUID uuid, String name, BigDecimal balance) {
        super(sign, SyncType.UPDATEPLAYER, uuid);
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }
}
