package me.yic.xconomy.info;

public enum SyncChannalType {
    OFF("Off"),
    BUNGEECORD("BungeeCord"),
    REDIS("Redis");

    final String value;

    private SyncChannalType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(SyncChannalType Other) {
        return this.value.equals(Other.value);
    }
}
