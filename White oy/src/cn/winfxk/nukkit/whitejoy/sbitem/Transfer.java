package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;

public class Transfer extends BaseItem {
    public Transfer() {
        super("Transfer");
    }

    @Override
    public boolean handle(Player player) {
        return false;
    }
}
