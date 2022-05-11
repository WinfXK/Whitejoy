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
    protected final File file;
    protected final Config config;
    protected Map<String, Object> shops;
    protected static final String MainKey = "Shop";
    protected static final String[] FishKey = {"{FishName}", "{Player}", "{Money}", "{Size}", "{Length}", "{FishPlayer}", "{FishMoney}", "{FishPlayerMoney}", "{FishContent}"};

    public Shop(Player player, BaseFormin Update, boolean isBack, File file) {
        super(player, Update, isBack);
        if (file == null)
            file = new File(main.getConfigDir(), Whitejoy.ShopFileName);
        config = new Config(this.file = file);
    }

    @Override
    public boolean MakeForm() {
        if (file == null)
            return makeShow(player, message.getSon(MainKey, "NotFile", this));
        Object obj = config.get("Shops");
        shops = obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
        SimpleForm form = new SimpleForm(getID(), getTitle(), getContent());
        for (Map.Entry<String, Object> entry : shops.entrySet()) {
            final Map<String, Object> ShopItem = entry.getValue() instanceof Map ? (Map<String, Object>) entry.getValue() : new HashMap<>();
            if (ShopItem.size() <= 0) continue;
            obj = ShopItem.get("Item");
            if (!(obj instanceof Map)) continue;
            Item Fish = Tool.loadItem((Map<String, Object>) obj);
            form.addButton(getShopItem(ShopItem, Fish), true, itemlist.getItem(Fish).getPath(), (a, b) -> {
                if (ShopItem.get("Player").equals(player.getName()))
                    return soldOut(entry.getKey(), ShopItem, Fish);
                return show(new BuyFish(player, this, true, file, entry.getKey()));
            });
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

    private boolean soldOut(String Key, Map<String, Object> ShopItem, Item Fish) {
        SimpleForm form = new SimpleForm(getID(), getTitle(), getString("soldOutContent", FishKey, getData(ShopItem, Fish)));
        form.addButton(getConfirm(), (a, b) -> {
            shops.remove(Key);
            config.set("Shops", shops);
            return config.save() & makeShow(true, player.getName(), getTitle(), getString("soldOutOK"), getBack(), (aa, ba) -> MakeForm(), getExitString(), (aa, ba) -> false);
        });
        form.addButton(getBackString(), (a, b) -> MakeForm());
        form.addButton(getExitString(), (a, b) -> false);
        form.show(player);
        return true;
    }

    protected Object[] getData(Map<String, Object> map, Item item) {
        return new Object[]{item.getCustomName(), player.getName(), myPlayer.getMoney(), Tool.objToDouble(map.get("Size")), Tool.objToDouble(map.get("Length")), map.get("Player"), WinfxkLib.getEconomy().getMoney(Tool.objToString(map.get("Player"))), map.get("Content")};
    }

    protected String getShopItem(Map<String, Object> map, Item item) {
        return getString("ShopItem", FishKey, getData(map, item));
    }
}
