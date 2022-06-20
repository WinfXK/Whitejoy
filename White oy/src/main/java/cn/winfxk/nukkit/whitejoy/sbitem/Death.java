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
        FishEntity fish = new FishEntity(player);
        boolean abc = player.attack(new EntityDamageEvent(fish, EntityDamageEvent.DamageCause.ENTITY_ATTACK, player.getMaxHealth() * 10));
        fish.kill();
        return abc;
    }
}
