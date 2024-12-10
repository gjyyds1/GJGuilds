package me.yic.xconomy.data.caches;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cache {
    public static List<String> baltop_papi = new ArrayList();
    public static BigDecimal sumbalance;

    public Cache() {
    }

    static {
        sumbalance = BigDecimal.ZERO;
    }
}
