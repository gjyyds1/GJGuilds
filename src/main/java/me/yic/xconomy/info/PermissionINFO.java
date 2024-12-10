package me.yic.xconomy.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PermissionINFO {
    public static boolean globalpayment = true;
    private static final Map<UUID, Boolean> payment = new HashMap();
    private static final List<UUID> rpayment = new ArrayList();

    public PermissionINFO() {
    }

    public static boolean getGlobalPayment() {
        return globalpayment;
    }

    public static Boolean getPaymentPermission(UUID u) {
        return (Boolean)payment.getOrDefault(u, (Boolean) null);
    }

    public static void setPaymentPermission(UUID u, Boolean b) {
        if (b == null) {
            payment.remove(u);
        } else {
            payment.put(u, b);
        }

    }

    public static boolean getRPaymentPermission(UUID u) {
        return !rpayment.contains(u);
    }

    public static void setRPaymentPermission(UUID u) {
        if (rpayment.contains(u)) {
            rpayment.remove(u);
        } else {
            rpayment.add(u);
        }

    }

    public static void setRPaymentPermission(UUID u, boolean value) {
        if (value) {
            rpayment.remove(u);
        } else {
            rpayment.add(u);
        }

    }
}
