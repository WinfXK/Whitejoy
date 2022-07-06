package cn.winfxk.nukkit.whitejoy;

import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.money.MyEconomy;
import cn.winfxk.nukkit.winfxklib.tool.MyMap;
import cn.winfxk.nukkit.winfxklib.tool.Tool;
import top.wcpe.wcvault.WCVaultApi;

import java.util.Map;

public class VC_Economy extends MyEconomy {
    private final String Type;
    private static final String Key = "Economy";
    private static final String[] FormKey = {"{newEconomyName}", "{newMoneyName}", "{newEconomyType}"};
    private static final Message msg = Whitejoy.getMain().getMessage();

    protected static void load() {
        Map<String, Object> map = Whitejoy.getMain().getconfig().getMap("自定义货币", new MyMap<>());
        Map<String, Object> item;
        String EconomyName, MoneyName, Type;
        for (Object obj : map.values()) {
            if (!(obj instanceof Map)) continue;
            item = (Map<String, Object>) obj;
            if (item.size() <= 0) continue;
            EconomyName = Tool.objToString(item.get("ID"), null);
            MoneyName = Tool.objToString(item.get("Name"), null);
            Type = Tool.objToString(item.get("Type"), null);
            if (EconomyName == null || EconomyName.isEmpty()) {
                Whitejoy.getMain().getLogger().info(msg.getSon(Key, "EconomyIDisEmpty"));
                continue;
            }
            if (MoneyName == null || MoneyName.isEmpty()) {
                Whitejoy.getMain().getLogger().info(msg.getSon(Key, "MoneyNameisEmpty"));
                continue;
            }
            if (Type == null || Type.isEmpty()) {
                Whitejoy.getMain().getLogger().info(msg.getSon(Key, "EconomyTypeisEmpty"));
                continue;
            }
            if (WinfxkLib.getEconomy(EconomyName) != null) {
                Whitejoy.getMain().getLogger().info(msg.getSon(Key, "EconomyExist", FormKey, new Object[]{EconomyName, MoneyName, Type}));
                continue;
            }
            if (Whitejoy.getMain().getBlockEconomys().contains(EconomyName) || WinfxkLib.getBlacklistEconomy().contains(EconomyName)) {
                Whitejoy.getMain().getLogger().info(msg.getSon(Key, "EconomyBlock", FormKey, new Object[]{EconomyName, MoneyName, Type}));
                continue;
            }
            if (!WinfxkLib.addEconomy(new VC_Economy(EconomyName, MoneyName, Type)))
                Whitejoy.getMain().getLogger().info(msg.getSon(Key, "addFail", FormKey, new Object[]{EconomyName, MoneyName, Type}));
        }
    }

    public VC_Economy(String EconomyName, String MoneyName, String Type) {
        super(EconomyName, MoneyName);
        this.Type = Type;
    }

    @Override
    public double getMoney(String s) {
        return WCVaultApi.look(s, Type);
    }

    @Override
    public double addMoney(String s, double v) {
        WCVaultApi.pay(s, Type, v);
        return getMoney(s);
    }

    @Override
    public double reduceMoney(String s, double v) {
        WCVaultApi.take(s, Type, v);
        return getMoney(s);
    }

    @Override
    public double setMoney(String s, double v) {
        WCVaultApi.set(s, Type, v);
        return getMoney(s);
    }

    @Override
    public boolean allowArrears() {
        return false;
    }
}
