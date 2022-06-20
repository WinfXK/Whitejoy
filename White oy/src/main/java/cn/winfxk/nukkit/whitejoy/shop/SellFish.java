package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.form.api.CustomForm;
import cn.winfxk.nukkit.winfxklib.money.MyEconomy;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.MyMap;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellFish extends BaseForm {
    private final Config config;
    private final List<Item> items = new ArrayList<>();
    private final List<MyEconomy> Economys = new ArrayList<>();
    private static final String[] EconomyKey = {"{UseEconomyName}", "{UseMoneyName}"};
    private static MyEconomy economy;
    private static Double ServiceCharge;

    public SellFish(Player player, BaseFormin Update, boolean isBack, File file) {
        super(player, Update, isBack);
        config = new Config(file);
        if (economy == null) {
            String EconomyID = main.getconfig().getString("上架手续费货币");
            economy = WinfxkLib.getEconomy(EconomyID);
            if (economy == null) {
                economy = WinfxkLib.getEconomy();
                main.getLogger().error(getString("EconomyAPIError"));
            }
            ServiceCharge = main.getconfig().getDouble("上架手续费");
        }
    }

    @Override
    public boolean MakeForm() {
        List<String> list = getItems();
        if (economy.getMoney(player) < ServiceCharge)
            return makeShow(true, player.getName(), getTitle(), getString("NotServiceCharge", EconomyKey, new Object[]{economy.getEconomyName(), economy.getMoneyName()}), getBack(), (a, b) -> isBack(), getExitString(), (a, b) -> false);
        if (list.size() <= 0)
            return makeShow(true, player.getName(), getTitle(), getString("NotFish"), getBack(), (a, b) -> isBack(), getExitString(), (a, b) -> false);
        CustomForm form = new CustomForm(getID(), getTitle());
        form.addLabel(getContent());
        form.addDropdown(getString("SelectItem"), list);
        form.addDropdown(getString("SelectEconomy"), getEconomys());
        form.addInput(getString("InputMoney"), "", getString("InputMoney"));
        form.addInput(getString("InputContent"), message.getConfig().getMap("Shop", new MyMap<>()).getMap("SellFish", new MyMap<>()).get("DefaultContent"), getString("InputContent"));
        form.show(player, null, (a, b) -> isBack());
        return true;
    }

    @Override
    public boolean DisposeCustom(FormResponseCustom data) {
        Item item = items.get(data.getDropdownResponse(1).getElementID());
        MyEconomy economy = Economys.get(data.getDropdownResponse(2).getElementID());
        String s = data.getInputResponse(3);
        double Money = Tool.objToDouble(s);
        if (s == null || s.isEmpty() || !Tool.isInteger(s) || Money <= 0)
            return makeShow(true, player.getName(), getTitle(), getString("InputMoneySB"), getConfirm(), (a, b) -> MakeForm(), getExitString(), (a, b) -> false);
        String Content = data.getInputResponse(4);
        int index = main.getconfig().getInt("商店个性化介绍上限");
        Content = Content == null ? "" : Content.length() > index ? Content.substring(0, index) : Content;
        Map<String, Object> Shops = config.getMap("Shops", new MyMap<>());
        String Key = Tool.getRandColor();
        while (Shops.containsKey(Key))
            Key += Tool.getRandColor();
        Map<String, Object> ShopItem = new HashMap<>();
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null || nbt.getString(main.getName()) == null || !nbt.getString(main.getName()).equals(main.getName()))
            return makeShow(true, player.getName(), getTitle(), getString("ItemError"), getConfirm(), (a, b) -> MakeForm(), getExitString(), (a, b) -> false);
        ShopItem.put("Item", Tool.saveItem(item));
        ShopItem.put("Length", nbt.getDouble("Size"));
        ShopItem.put("Size", nbt.getDouble("Size"));
        ShopItem.put("Player", player.getName());
        ShopItem.put("Economy", economy.getEconomyName());
        ShopItem.put("Content", Content);
        ShopItem.put("ServiceCharge", ServiceCharge);
        ShopItem.put("ServiceEconomy", economy.getEconomyName());
        ShopItem.put("Money", Money);
        Shops.put(Key, ShopItem);
        config.set("Shops", Shops);
        player.getInventory().remove(item);
        economy.reduceMoney(player, ServiceCharge);
        return super.DisposeCustom(data) & config.save() & sendMessage(getString("OK"));
    }

    public List<String> getItems() {
        List<String> list = new ArrayList<>();
        items.clear();
        CompoundTag nbt;
        for (Item item : player.getInventory().getContents().values()) {
            nbt = item.getNamedTag();
            if (nbt == null || nbt.getString(main.getName()) == null || !nbt.getString(main.getName()).equals(main.getName()))
                continue;
            list.add(item.hasCustomName() ? item.getCustomName() : itemlist.getName(item));
            items.add(item);
        }
        return list;
    }

    public List<String> getEconomys() {
        Economys.clear();
        List<String> list = new ArrayList<>();
        for (MyEconomy economy : main.getMyEconomys()) {
            list.add(getString("EconomyItem", EconomyKey, new Object[]{economy.getEconomyName(), economy.getMoneyName()}));
            Economys.add(economy);
        }
        return list;
    }
}
