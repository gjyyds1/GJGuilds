package me.yic.xconomy.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import me.yic.xconomy.AdapterManager;
import me.yic.xconomy.XConomyLoad;
import me.yic.xconomy.data.DataCon;
import me.yic.xconomy.data.DataFormat;
import me.yic.xconomy.data.DataLink;
import me.yic.xconomy.data.caches.Cache;
import me.yic.xconomy.data.syncdata.PlayerData;
import me.yic.xconomy.info.PermissionINFO;
import me.yic.xconomy.info.SyncChannalType;

public class XConomyAPI {
    public XConomyAPI() {
    }

    public String getversion() {
        return "XConomy.PVersion";
    }

    public SyncChannalType getSyncChannalType() {
        return XConomyLoad.Config.SYNCDATA_TYPE;
    }

    public BigDecimal formatdouble(String amount) {
        return DataFormat.formatString(amount);
    }

    public String getdisplay(BigDecimal balance) {
        return DataFormat.shown(balance);
    }

    public boolean createPlayerData(UUID uid, String name) {
        return DataLink.newPlayer(uid, name);
    }

    public PlayerData getPlayerData(UUID uid) {
        return DataCon.getPlayerData(uid);
    }

    public PlayerData getPlayerData(String name) {
        return DataCon.getPlayerData(name);
    }

    public boolean createNonPlayerData(String account) {
        return DataLink.newAccount(account);
    }

    public BigDecimal getNonPlayerBalance(String account) {
        return DataCon.getAccountBalance(account);
    }

    /** @deprecated */
    @Deprecated
    public BigDecimal getorcreateAccountBalance(String account) {
        return DataCon.getAccountBalance(account);
    }

    public boolean ismaxnumber(BigDecimal amount) {
        return DataFormat.isMAX(amount);
    }

    public int changePlayerBalance(UUID u, String playername, BigDecimal amount, Boolean isadd) {
        return this.changePlayerBalance(u, playername, amount, isadd, (String)null);
    }

    public int changePlayerBalance(UUID u, String playername, BigDecimal amount, Boolean isadd, String pluginname) {
        if (XConomyLoad.getSyncData_Enable() & AdapterManager.BanModiftyBalance()) {
            return 1;
        } else {
            BigDecimal bal = this.getPlayerData(u).getBalance();
            if (isadd != null) {
                if (isadd) {
                    if (this.ismaxnumber(bal.add(amount))) {
                        return 3;
                    }
                } else if (bal.compareTo(amount) < 0) {
                    return 2;
                }
            }

            DataCon.changeplayerdata("PLUGIN_API", u, amount, isadd, pluginname, (Object)null);
            return 0;
        }
    }

    /** @deprecated */
    @Deprecated
    public int changeAccountBalance(String account, BigDecimal amount, Boolean isadd) {
        return this.changeNonPlayerBalance(account, amount, isadd, (String)null);
    }

    /** @deprecated */
    @Deprecated
    public int changeAccountBalance(String account, BigDecimal amount, Boolean isadd, String pluginname) {
        return this.changeNonPlayerBalance(account, amount, isadd, pluginname);
    }

    public int changeNonPlayerBalance(String account, BigDecimal amount, Boolean isadd) {
        return this.changeNonPlayerBalance(account, amount, isadd, (String)null);
    }

    public int changeNonPlayerBalance(String account, BigDecimal amount, Boolean isadd, String pluginname) {
        BigDecimal bal = this.getorcreateAccountBalance(account);
        if (isadd != null) {
            if (isadd) {
                if (this.ismaxnumber(bal.add(amount))) {
                    return 3;
                }
            } else if (bal.compareTo(amount) < 0) {
                return 2;
            }
        }

        DataCon.changeaccountdata("PLUGIN_API", account, amount, isadd, pluginname);
        return 0;
    }

    public List<String> getbalancetop() {
        return Cache.baltop_papi;
    }

    public BigDecimal getsumbalance() {
        return Cache.sumbalance;
    }

    public boolean getglobalpermission(String permission) {
        return permission.equalsIgnoreCase("pay") ? PermissionINFO.getGlobalPayment() : true;
    }

    public void setglobalpermission(String permission, boolean vaule) {
        PermissionINFO.globalpayment = vaule;
    }

    public Boolean getpaymentpermission(UUID uid) {
        return PermissionINFO.getPaymentPermission(uid);
    }

    public void setpaymentpermission(UUID uid, Boolean vaule) {
        PermissionINFO.setPaymentPermission(uid, vaule);
    }

    public Boolean getpaytoggle(UUID uid) {
        return PermissionINFO.getRPaymentPermission(uid);
    }

    public void setpaytoggle(UUID uid, boolean vaule) {
        PermissionINFO.setRPaymentPermission(uid, vaule);
    }
}
