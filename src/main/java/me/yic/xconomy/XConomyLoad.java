package me.yic.xconomy;

import java.util.concurrent.ExecutorService;
import me.yic.xconomy.info.DefaultConfig;
import me.yic.xconomy.info.SyncChannalType;

public class XConomyLoad {
    public static DefaultConfig Config;
    public static ExecutorService FixedThreadPool;

    public XConomyLoad() {
    }

    public static boolean getSyncData_Enable() {
        return !Config.SYNCDATA_TYPE.equals(SyncChannalType.OFF);
    }

    public static void runTaskAsynchronously(Runnable runnable) {
        FixedThreadPool.execute(runnable);
    }
}
