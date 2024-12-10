package us.kg.gjyyds1;

import me.yic.xconomy.api.XConomyAPI;

import java.math.BigDecimal;
import java.util.UUID;

public class MoneyTransaction {

    private final XConomyAPI api;

    public MoneyTransaction(XConomyAPI api) {
        this.api = api;
    }

    public boolean performOperation(UUID playerId, String playerName, String nonPlayerAccount, BigDecimal amount) {
        // 检查非玩家数据是否存在
        BigDecimal nonPlayerBalance = api.getNonPlayerBalance(nonPlayerAccount);
        if (nonPlayerBalance == null) {
            // 创建非玩家数据
            boolean created = api.createNonPlayerData(nonPlayerAccount);
            if (!created) {
                return false;
            }
        }

        // 检查玩家余额是否足够
        BigDecimal playerBalance = api.getPlayerData(playerId).getBalance();
        if (playerBalance.compareTo(amount) < 0) {
            return false;
        }

        // 扣除玩家余额
        int playerResult = api.changePlayerBalance(playerId, playerName, amount, false);
        if (playerResult == 0) {
            return false;
        }

        // 增加非玩家余额
        int nonPlayerResult = api.changeNonPlayerBalance(nonPlayerAccount, amount, true, "GJGuilds");
        return nonPlayerResult == 0;
    }
}
