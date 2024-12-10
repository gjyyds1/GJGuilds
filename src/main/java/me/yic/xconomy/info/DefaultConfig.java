package me.yic.xconomy.info;

import java.math.BigDecimal;
import java.util.List;

public class DefaultConfig {
    public String LANGUAGE = "";
    public boolean CHECK_UPDATE = false;
    public int REFRESH_TIME = 0;
    public boolean ECO_COMMAND = false;
    public boolean DISABLE_ESSENTIAL = false;
    public double INITIAL_BAL = 0.0;
    public BigDecimal PAYMENT_TAX;
    public int RANKING_SIZE;
    public int LINES_PER_PAGE;
    public boolean DISABLE_CACHE;
    public boolean TRANSACTION_RECORD;
    public boolean PAY_TIPS;
    public boolean USERNAME_IGNORE_CASE;
    public boolean NON_PLAYER_ACCOUNT;
    public List<String> NON_PLAYER_ACCOUNT_SUBSTRING;
    public String SINGULAR_NAME;
    public String PLURAL_NAME;
    public boolean INTEGER_BAL;
    public String THOUSANDS_SEPARATOR;
    public String DISPLAY_FORMAT;
    public String MAX_NUMBER;
    public List<Integer> FORMAT_BALANCE;
    public boolean BUNGEECORD_ENABLE;
    public String BUNGEECORD_SIGN;
    public SyncChannalType SYNCDATA_TYPE;

    public DefaultConfig() {
        this.PAYMENT_TAX = BigDecimal.ZERO;
        this.RANKING_SIZE = this.getrankingsize();
        this.LINES_PER_PAGE = 0;
        this.DISABLE_CACHE = false;
        this.TRANSACTION_RECORD = false;
        this.PAY_TIPS = false;
        this.USERNAME_IGNORE_CASE = false;
        this.NON_PLAYER_ACCOUNT = false;
        this.NON_PLAYER_ACCOUNT_SUBSTRING = null;
        this.SINGULAR_NAME = "";
        this.PLURAL_NAME = "";
        this.INTEGER_BAL = false;
        this.THOUSANDS_SEPARATOR = "";
        this.DISPLAY_FORMAT = "";
        this.MAX_NUMBER = "";
        this.FORMAT_BALANCE = null;
        this.BUNGEECORD_ENABLE = false;
        this.BUNGEECORD_SIGN = "";
        this.SYNCDATA_TYPE = SyncChannalType.OFF;
    }

    private int getrankingsize() {
        return 0;
    }

    private void setnonplayeraccount() {
    }

    private void setpaytips() {
    }
}
