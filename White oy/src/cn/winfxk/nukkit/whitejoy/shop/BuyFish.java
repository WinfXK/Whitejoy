package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.form.api.SimpleForm;
import cn.winfxk.nukkit.winfxklib.money.MyEconomy;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.MyMap;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;

public class BuyFish extends BaseForm {
    private final String Key;
    private static final String[] FishKey = {"{Money}", "{Player}", "{UseEconomyName}", "{UseMoneyName}", "{FishName}", "{FishSize}", "{FishLength}", "{FishContent}", "{Price}"};
    private final MyMap<String, Object> ShopItem;
    private final MyMap<String, Object> Shops;
    private final Config config;
    private final MyEconomy economy;
    private final String EconomyID;
    private final Item item;
    private static Boolean ToRank;

    public BuyFish(Player player, BaseFormin Update, boolean isBack, File file, String Key) {
        super(player, Update, isBack);
        this.Key = Key;
        config = new Config(file);
        Shops = config.getMap("Shops");
        ShopItem = Shops.getMap(Key);
        economy = WinfxkLib.getEconomy(EconomyID = ShopItem.getString("Economy"));
        item = Tool.loadItem(ShopItem.getMap("Item"));
        if (ToRank == null)
            ToRank = main.getconfig().getBoolean("上架转移排名");
        setK(FishKey);
    }

    @Override
    public boolean MakeForm() {
        setD(myPlayer.getMoney(), player.getName(), EconomyID, economy == null ? "unknown" : economy.getMoneyName(), item.hasCustomName() ? item.getCustomName() : item.getName(), Tool.Double2(ShopItem.getDouble("Size")), Tool.Double2(ShopItem.getDouble("Length")), message.getText(ShopItem.getString("Content"), player), Tool.Double2(ShopItem.getDouble("Money")));
        if (economy == null)
            return makeShow(true, player.getName(), getTitle(), getString("BanEconomy"), getBack(), (aa, a) -> isBack(), getExitString(), (ab, bb) -> false);
        SimpleForm form = new SimpleForm(getID(), getTitle(), getContent());
        form.addButton(getString("Buy"), (a, b) -> {
            if (economy.getMoney(player) < ShopItem.getDouble("Money"))
                return makeShow(true, player.getName(), getTitle(), getString("NotMoney"), getBackString(), (aa, ba) -> MakeForm(), getExitString(), (ab, bb) -> false);
            Shops.remove(Key);
            config.set("Shops", Shops);
            config.save();
            economy.reduceMoney(player, ShopItem.getDouble("Money"));
            player.getLevel().dropItem(player, item);
            if (ToRank) {
                double Rank, ItemRank = Tool.Double2(ShopItem.getDouble("Size"));
                if (main.getRanking().containsKey(player.getName())) {
                    Rank = main.getRanking().getDouble(player.getName());
                    main.getRanking().set(player.getName(), Math.max(ItemRank, Rank));
                } else
                    main.getRanking().set(player.getName(), ItemRank);
                String PlayerName = ShopItem.getString("Player");
                if (main.getRanking().containsKey(PlayerName)) {
                    double MyRank = main.getRanking().getDouble(PlayerName);
                    if (ItemRank >= MyRank)
                        main.getRanking().remove(player.getName());
                }
                main.getRanking().save();
            }
            return makeShow(true, player.getName(), getTitle(), getString("BuyOK"), getBack(), (aa, ba) -> isBack(), getExitString(), (ab, bb) -> false);
        });
        form.addButton(getBack(), (a, b) -> isBack());
        form.show(player);
        return true;
    }
}
