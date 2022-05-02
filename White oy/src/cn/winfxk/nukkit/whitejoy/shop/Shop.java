package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.winfxk.nukkit.whitejoy.Setting;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.form.api.SimpleForm;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Shop extends BaseForm {
    private File file;
    private Config config;
    private static final String MainKey = "Shop";
    private Map<String, Object> Shops;
    private static final String[] FishKey = {"{FishName}", "{Player}", "{Money}", "{Size}", "{Length}", "{FishPlayer}", "{FishMoney}", "{FishPlayerMoney}"};

    public Shop(Player player, BaseFormin Update, boolean isBack) {
        this(player, Update, isBack, null);
        FormKey = "Main";
    }

    public Shop(Player player, BaseFormin Update, boolean isBack, File file) {
        super(player, Update, isBack);
        if (file == null)
            file = new File(main.getConfigDir(), Whitejoy.ShopFileName);
        config = new Config(file);
    }

    @Override
    public boolean MakeForm() {
        if (file == null)
            return cn.winfxk.nukkit.winfxklib.form.BaseForm.makeShow(player, message.getSon(MainKey, "NotFile", this));
        Object obj = config.get("Shops");
        Shops = obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
        SimpleForm form = new SimpleForm(getID(), getTitle(), getContent());
        Map<String, Object> ShopItem;
        Item Fish;
        for (Map.Entry<String, Object> entry : Shops.entrySet()) {
            ShopItem = entry.getValue() instanceof Map ? (Map<String, Object>) entry.getValue() : new HashMap<>();
            if (ShopItem.size() <= 0) continue;
            obj = ShopItem.get("Item");
            if (!(obj instanceof Map)) continue;
            Fish = Tool.loadItem((Map<String, Object>) obj);
            form.addButton(getShopItem(ShopItem, Fish), true, itemlist.getItem(Fish).getPath(), (a, b) -> show(new BuyFish(player, this, true, file, entry.getKey())));
        }
        form.addButton(getString("SellFish"), (a, b) -> show(new SellFish(a, this, true, file)));
        if (player.hasPermission("Whitejoy.Command.Admin")) {
            form.addButton(getString("DelFish"), (a, b) -> show(new DeleteFish(a, this, true, file)));
            form.addButton(getString("Setting"), (a, b) -> show(new Setting(a, this, true)));
        }
        form.addButton(getBack(), (a, b) -> isBack());
        form.show(player);
        return true;
    }

    private String getShopItem(Map<String, Object> map, Item item) {
        return getString("ShopItem", FishKey, new Object[]{item.getCustomName(), player.getName(), myPlayer.getMoney(), Tool.objToDouble(map.get("Size")), Tool.objToDouble(map.get("Length")), map.get("Player"), WinfxkLib.getEconomy().getMoney(Tool.objToString(map.get("Player")))});
    }
}
