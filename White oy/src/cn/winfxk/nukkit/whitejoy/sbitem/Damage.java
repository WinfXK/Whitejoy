package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.winfxk.nukkit.whitejoy.FishEntity;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

public class Damage extends BaseItem {

    public Damage() {
        super("Damage");
    }

    @Override
    public boolean handle(Player player) {
        return player.attack(new EntityDamageEvent(new FishEntity(player), EntityDamageEvent.DamageCause.ENTITY_ATTACK, (float) (Tool.getRand(5, 20) / 10)));
    }
}
