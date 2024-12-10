package me.yic.xconomy.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import me.yic.xconomy.XConomyLoad;

public class DataFormat {
    public static boolean isint = false;
    public static DecimalFormat decimalFormat;
    public static BigDecimal maxNumber;
    static final String displayformat;
    static final String pluralname;
    static final String singularname;

    public DataFormat() {
    }

    public static BigDecimal formatString(String am) {
        BigDecimal bigDecimal = new BigDecimal(am);
        return isint ? bigDecimal.setScale(0, RoundingMode.DOWN) : bigDecimal.setScale(2, RoundingMode.DOWN);
    }

    public static BigDecimal formatdouble(double am) {
        BigDecimal bigDecimal = BigDecimal.valueOf(am);
        return isint ? bigDecimal.setScale(0, RoundingMode.DOWN) : bigDecimal.setScale(2, RoundingMode.DOWN);
    }

    public static String shown(BigDecimal am) {
        return "";
    }

    public static String shown(double am) {
        return shown(BigDecimal.valueOf(am));
    }

    public static String PEshownf(BigDecimal am) {
        return "";
    }

    public static boolean isMAX(BigDecimal am) {
        return am.compareTo(maxNumber) > 0;
    }

    public static void load() {
        maxNumber = setmaxnumber();
        isint = XConomyLoad.Config.INTEGER_BAL;
        String gpoint = XConomyLoad.Config.THOUSANDS_SEPARATOR;
        decimalFormat = new DecimalFormat();
        if (!isint) {
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(2);
        }

        if (gpoint != null && !gpoint.equals("") && gpoint.length() == 1) {
            DecimalFormatSymbols spoint = new DecimalFormatSymbols();
            spoint.setGroupingSeparator(gpoint.charAt(0));
            decimalFormat.setDecimalFormatSymbols(spoint);
        }

        XConomyLoad.Config.PAYMENT_TAX = setpaymenttax();
    }

    private static BigDecimal setmaxnumber() {
        String maxn = XConomyLoad.Config.MAX_NUMBER;
        BigDecimal defaultmaxnumber = new BigDecimal("10000000000000000");
        if (maxn == null) {
            return defaultmaxnumber;
        } else if (maxn.length() > 17) {
            return defaultmaxnumber;
        } else {
            BigDecimal mnumber = new BigDecimal(maxn);
            return mnumber.compareTo(defaultmaxnumber) >= 0 ? defaultmaxnumber : mnumber;
        }
    }

    private static BigDecimal setpaymenttax() {
        return BigDecimal.ZERO;
    }

    private static String getformatbalance(BigDecimal bal) {
        return "";
    }

    static {
        displayformat = XConomyLoad.Config.DISPLAY_FORMAT;
        pluralname = XConomyLoad.Config.PLURAL_NAME;
        singularname = XConomyLoad.Config.SINGULAR_NAME;
    }
}
