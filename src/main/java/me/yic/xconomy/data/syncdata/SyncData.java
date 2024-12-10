package me.yic.xconomy.data.syncdata;

import java.io.Serializable;
import java.util.UUID;
import me.yic.xconomy.info.SyncType;

public class SyncData implements Serializable {
    final String sign;
    final SyncType st;
    final UUID uuid;

    protected SyncData(String sign, SyncType st, UUID uuid) {
        this.sign = sign;
        this.st = st;
        this.uuid = uuid;
    }

    public String getSign() {
        return this.sign;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public SyncType getSyncType() {
        return this.st;
    }
}
