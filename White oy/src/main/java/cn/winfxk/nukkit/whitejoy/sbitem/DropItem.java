package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.ArrayList;
import java.util.List;

public class DropItem extends BaseItem {
    public DropItem() {
        super("DropItem");
    }

    @Override
    public boolean handle(Player player) {
        PlayerInventory Inventory = player.getInventory();
        List<Item> list = new ArrayList<>();
        for (Item item : Inventory.getContents().values())
            if (item != null && item.getId() != 0)
                list.add(item);
        if (list.size() <= 0) return false;
        player.dropItem(list.get(Tool.getRand(0, list.size() - 1)));
        return true;
    }
}
