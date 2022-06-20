package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

/**
 * 永久减少血量上限
 */
public class Oligemia extends BaseItem {
    public Oligemia() {
        super("Oligemia");
    }

    @Override
    public boolean handle(Player player) {
        player.setMaxHealth(player.getMaxHealth() - Tool.getRand(1, 2));
        return true;
    }
}
