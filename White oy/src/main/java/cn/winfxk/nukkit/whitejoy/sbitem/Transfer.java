package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

public class Transfer extends BaseItem {
    private static final int distance = Whitejoy.getMain().getconfig().getInt("传送距离");

    public Transfer() {
        super("Transfer");
    }

    @Override
    public boolean handle(Player player) {
        Vector3 vector3 = player.clone();
        vector3.x = Tool.getRand(-1 * distance, distance);
        vector3.y = Tool.getRand(-1 * distance, distance);
        vector3.z = Tool.getRand(-1 * distance, distance);
        player.teleport(vector3);
        return true;
    }
}
