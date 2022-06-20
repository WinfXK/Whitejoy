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
        FishEntity fish = new FishEntity(player);
        boolean abc = player.attack(new EntityDamageEvent(fish, EntityDamageEvent.DamageCause.ENTITY_ATTACK, (float) (Tool.getRand(5, 20) / 10)));
        fish.kill();
        return abc;
    }
}
