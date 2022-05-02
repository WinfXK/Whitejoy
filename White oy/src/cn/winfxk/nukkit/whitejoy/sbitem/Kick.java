package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.winfxk.nukkit.whitejoy.Whitejoy;

public class Kick extends BaseItem {
    public Kick() {
        super("Kick");
    }

    @Override
    public boolean handle(Player player) {
        player.kick(Whitejoy.getMain().getMessage().getSon("Game", "FishKick", player));
        return true;
    }
}
