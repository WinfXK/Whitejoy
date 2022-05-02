package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.winfxk.nukkit.whitejoy.FishEntity;

public class Death extends BaseItem {
    public Death() {
        super("Death");
    }

    @Override
    public boolean handle(Player player) {
        return player.attack(new EntityDamageEvent(new FishEntity(player), EntityDamageEvent.DamageCause.ENTITY_ATTACK, player.getMaxHealth() * 10));
    }
}
