package me.yic.xconomy.info;

public enum SyncType {
    UPDATEPLAYER("updateplayer"),
    BALANCEALL("balanceall"),
    MESSAGE("message"),
    MESSAGE_SEMI("message#semi"),
    BROADCAST("broadcast"),
    SYNCONLINEUUID("syncOnlineUUID"),
    PERMISSION("permission");

    final String value;

    private SyncType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(SyncType Other) {
        return this.value.equals(Other.value);
    }
}
