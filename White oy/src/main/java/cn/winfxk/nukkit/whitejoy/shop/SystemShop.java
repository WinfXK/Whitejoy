package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.form.api.CustomForm;
import cn.winfxk.nukkit.winfxklib.money.MyEconomy;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SystemShop extends BaseForm {
    private final List<Item> items = new ArrayList<>();
    public static final String[] ItemKey = {"{ItemName}", "{ItemID}", "{ItemDamage}", "{ItemMoney}", "{Length}", "{Size}", "{Rate}"};
    private Map<String, Object> ItemMap;
    private MyEconomy economy;

    public SystemShop(Player player, BaseFormin Update, boolean isBack, Map<String, Object> ItemMap) {
        super(player, Update, isBack);
        this.ItemMap = ItemMap;
    }

    @Override
    public boolean MakeForm() {
        String s = Tool.objToString(ItemMap.get("Economy"));
        List<MyEconomy> myEconomyList = main.getMyEconomys();
        if (s == null || s.isEmpty()) return sendMessage(getString("EconomyAPISB")) & isBack();
        if ((economy = getMyEconomy(s)) == null) return sendMessage(getString("EconomyAPISB")) & isBack();
        CustomForm form = new CustomForm(getID(), getTitle());
        form.addLabel(getContent());
        List<String> list = getItems();
        if (list.size() <= 0)
            return sendMessage(getString("notItem")) & isBack();
        form.addDropdown(getString("SelectItem"), list);
        form.show(player, (player1, formResponse) -> disMake(((FormResponseCustom) formResponse).getDropdownResponse(1).getElementID()), (player1, formResponse) -> isBack());
        return true;
    }

    /**
     * 获取当前插件可用的经济支持
     *
     * @param s
     * @return
     */
    public MyEconomy getMyEconomy(String s) {
        if (main.getBlockEconomys().contains(s)) return null;
        for (MyEconomy economy : WinfxkLib.getEconomys())
            if (s.equals(economy.getEconomyName())) return economy;
        return null;
    }

    private boolean disMake(int index) {
        Item item = items.get(index);
        CompoundTag nbt = item.getNamedTag();
        double Size = nbt.getDouble("Size");
        double Length = nbt.getDouble("Length");
        double Rate = Tool.objToDouble(ItemMap.get("Rate"));
        double Money = Size * Length * Rate * item.count;
        player.getInventory().removeItem(item);
        economy.addMoney(player, Money);
        return sendMessage(getString("SellOK", ItemKey, new Object[]{item.hasCustomName() ? item.getCustomName() : itemlist.getName(item), item.getId(), item.getDamage(), Money, Length, Size, Rate})) & isBack();
    }

    public List<String> getItems() {
        List<String> list = new ArrayList<>();
        items.clear();
        CompoundTag nbt;
        double Size, Length, Money, Rate;
        for (Item item : player.getInventory().getContents().values()) {
            nbt = item.getNamedTag();
            if (nbt == null || nbt.getString(main.getName()) == null || !nbt.getString(main.getName()).equals(main.getName()))
                continue;
            Size = nbt.getDouble("Size");
            Length = nbt.getDouble("Length");
            Rate = Tool.objToDouble(ItemMap.get("Rate"));
            Money = Size * Length * Rate * item.count;
            list.add(getString("ItemText", ItemKey, new Object[]{item.hasCustomName() ? item.getCustomName() : itemlist.getName(item), item.getId(), item.getDamage(), Money, Length, Size, Rate}));
            items.add(item);
        }
        return list;
    }
}
