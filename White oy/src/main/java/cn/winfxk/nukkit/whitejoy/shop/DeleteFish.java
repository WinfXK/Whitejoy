package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.form.api.CustomForm;
import cn.winfxk.nukkit.winfxklib.form.api.SimpleForm;
import cn.winfxk.nukkit.winfxklib.module.LeaveWord;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteFish extends Shop {

    public DeleteFish(Player player, BaseFormin Update, boolean isBack, File file) {
        super(player, Update, isBack, file);
        FormKey = getClass().getSimpleName();
    }

    @Override
    public boolean MakeForm() {
        if (file == null)
            return makeShow(player, message.getSon(MainKey, "NotFile", this));
        Object obj = config.get("Shops");
        shops = obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
        if (shops.size() <= 0)
            return makeShow(true, player.getName(), getTitle(), getString("NotItem"), getBack(), (aa, a) -> isBack(), getExitString(), (ab, bb) -> false);
        SimpleForm form = new SimpleForm(getID(), getTitle(), getContent());
        for (Map.Entry<String, Object> entry : shops.entrySet()) {
            final Map<String, Object> ShopItem = entry.getValue() instanceof Map ? (Map<String, Object>) entry.getValue() : new HashMap<>();
            if (ShopItem.size() <= 0) continue;
            obj = ShopItem.get("Item");
            if (!(obj instanceof Map)) continue;
            Item Fish = Tool.loadItem((Map<String, Object>) obj);
            form.addButton(getShopItem(ShopItem, Fish), true, itemlist.getItem(Fish).getPath(), (a, b) -> Del(ShopItem, Fish));
        }
        form.addButton(getBack(), (a, b) -> isBack());
        form.show(player);
        return true;
    }

    private boolean Del(Map<String, Object> shopItem, Item fish) {
        CustomForm form = new CustomForm(getID(), getTitle());
        form.addLabel(getString("DeleteContent", FishKey, getData(shopItem, fish)));
        form.addToggle(getString("ReturnItem"), true);
        form.addToggle(getString("ReturnServiceCharge"), false);
        form.show(player, (a, b) -> {
            FormResponseCustom data = (FormResponseCustom) b;
            boolean ReturnItem = data.getToggleResponse(1);
            boolean ReturnServiceCharge = data.getToggleResponse(2);
            List<LeaveWord.Economy> economies = new ArrayList<>();
            List<Item> items = new ArrayList<>();
            if (ReturnItem)
                items.add(fish);
            if (ReturnServiceCharge)
                economies.add(new LeaveWord.Economy(Tool.objToString(shopItem.get("ServiceEconomy")), Tool.objToDouble(shopItem.get("Money"))));
            return LeaveWord.addLeaveWord(Tool.objToString(shopItem.get("Player")), getTitle(), getString("DelMessage") + (!ReturnItem ? getString("DelItem") : ""), items, economies) & sendMessage(getString("DelOK"));
        });
        return true;
    }
}
