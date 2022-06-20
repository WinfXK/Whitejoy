package cn.winfxk.nukkit.whitejoy.shop;

import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.money.MyEconomy;

public class WC_Vault extends MyEconomy {
    private static String ID, Name;

    static {
        ID = Whitejoy.getMain().getconfig().getString("货币ID");
        Name = Whitejoy.getMain().getconfig().getString("货币名称");
    }

    public WC_Vault() {
        super(ID, Name);
    }

    @Override
    public double getMoney(String s) {
        return 0;
    }

    @Override
    public double addMoney(String s, double v) {
        return 0;
    }

    @Override
    public double reduceMoney(String s, double v) {
        return 0;
    }

    @Override
    public double setMoney(String s, double v) {
        return 0;
    }

    @Override
    public boolean allowArrears() {
        return false;
    }
}
