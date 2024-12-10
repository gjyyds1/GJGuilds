package me.yic.xconomy.data;

import java.math.BigDecimal;
import java.util.UUID;
import me.yic.xconomy.data.syncdata.PlayerData;

public class DataCon {
    public DataCon() {
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return null;
    }

    public static PlayerData getPlayerData(String username) {
        return null;
    }

    public static BigDecimal getAccountBalance(String account) {
        return null;
    }

    public static boolean hasaccountdatacache(String name) {
        return false;
    }

    public static void changeplayerdata(String type, UUID uid, BigDecimal amount, Boolean isAdd, String command, Object comment) {
    }

    public static void changeaccountdata(String type, String u, BigDecimal amount, Boolean isAdd, String command) {
    }
}
