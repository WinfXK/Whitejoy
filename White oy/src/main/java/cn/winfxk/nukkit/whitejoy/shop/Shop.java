package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.form.api.CustomForm;
import cn.winfxk.nukkit.winfxklib.form.api.SimpleForm;
import cn.winfxk.nukkit.winfxklib.money.MyEconomy;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop extends BaseForm {
    protected final File file;
    protected Config config;
    protected Map<String, Object> shops;
    protected static final String[] FishKey = {"{FishName}", "{Player}", "{Money}", "{Size}", "{Length}", "{FishPlayer}", "{FishMoney}", "{FishPlayerMoney}", "{FishContent}"};
    protected List<MyEconomy> economies;
    protected String defButtonText;

    public Shop(Player player, BaseFormin Update, boolean isBack, File file) {
        super(player, Update, isBack);
        if (file == null)
            file = new File(main.getConfigDir(), Whitejoy.ShopFileName);
        FormKey = "Main";
        this.file = file;
    }

    @Override
    public boolean MakeForm() {
        if (file == null)
            return makeShow(player, message.getSon(MainKey, "NotFile", this));
        config = new Config(this.file);
        Object obj = config.get("Shops");
        shops = obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
        SimpleForm form = new SimpleForm(getID(), getTitle(), getContent());
        boolean MyShop = WinfxkLib.getconfig().getBoolean("个人商店");
        int index = 0;
        for (Map.Entry<String, Object> entry : shops.entrySet()) {
            final Map<String, Object> ShopItem = entry.getValue() instanceof Map ? (Map<String, Object>) entry.getValue() : new HashMap<>();
            if (ShopItem.size() <= 0) continue;
            if (Tool.ObjToBool(ShopItem.get("SystemShop"))) {
                index++;
                form.addButton(message.getText(ShopItem.get("ButtonText")), true, "textures/items/fish_raw.png", (player1, formResponse) -> show(new SystemShop(player1, this, true, (Map<String, Object>) entry.getValue())));
                continue;
            }
            if (!MyShop) continue;
            obj = ShopItem.get("Item");
            if (!(obj instanceof Map)) continue;
            Item Fish = Tool.loadItem((Map<String, Object>) obj);
            index++;
            form.addButton(getShopItem(ShopItem, Fish), true, itemlist.getItem(Fish).getPath(), (a, b) -> {
                if (ShopItem.get("Player").equals(player.getName()))
                    return soldOut(entry.getKey(), ShopItem, Fish);
                return show(new BuyFish(player, this, true, file, entry.getKey()));
            });
        }
        if (MyShop)
            form.addButton(getString("SellFish"), (a, b) -> show(new SellFish(a, this, true, file)));
        if (player.hasPermission("Whitejoy.Command.Admin")) {
            if (index > 0)
                form.addButton(getString("DelFish"), (a, b) -> show(new DeleteFish(a, this, true, file)));
            form.addButton(getString("SystemShop"), (a, b) -> addSystemShop());
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

    private boolean addSystemShop() {
        CustomForm form = new CustomForm(getID(), getTitle());
        form.addLabel(getString("SystemShopContent"));
        form.addDropdown(getString("SelectEconomy"), getEconomyID());
        form.addInput(getString("FishMoneyBit"), 1);
        form.addInput(getString("InputButtonText"), defButtonText = Tool.objToString(((Map<String, Object>) ((Map<String, Object>) message.getConfig().get(MainKey)).get(FormKey)).get("ButtonText")));
        form.show(player, (player1, formResponse) -> disSystemShop((FormResponseCustom) formResponse), (player1, formResponse) -> MakeForm());
        return true;
    }

    private boolean disSystemShop(FormResponseCustom d) {
        int ID = d.getDropdownResponse(1).getElementID();
        if (ID < 0 || ID >= economies.size())
            return sendMessage(getString("EconomyAPISB")) & addSystemShop();
        MyEconomy economy = economies.get(ID);
        String s = d.getInputResponse(2);
        double rate = 0;
        if (s == null || s.isEmpty() || !Tool.isInteger(s) || (rate = Tool.objToDouble(s)) <= 0)
            return sendMessage(getString("RateSB")) & addSystemShop();
        String ButtonText = d.getInputResponse(3);
        ButtonText = ButtonText == null || ButtonText.isEmpty() ? defButtonText : ButtonText;
        String Key = Tool.getRandString();
        while (shops.containsKey(Key)) Key += Tool.getRandString();
        Map<String, Object> ShopItem = new HashMap<>();
        ShopItem.put("Player", player.getName());
        ShopItem.put("Economy", economy.getEconomyName());
        ShopItem.put("ButtonText", ButtonText);
        ShopItem.put("Rate", rate);
        ShopItem.put("SystemShop", true);
        shops.put(Key, ShopItem);
        config.set("Shops", shops);
        return config.save() & sendMessage(getString("addSystemShopOK")) & MakeForm();
    }

    private List<String> getEconomyID() {
        List<String> list = new ArrayList<>();
        for (MyEconomy economy : economies = main.getMyEconomys())
            list.add(getString("EconomyItem", SellFish.EconomyKey, new Object[]{economy.getEconomyName(), economy.getMoneyName()}));
        return list;
    }
}
