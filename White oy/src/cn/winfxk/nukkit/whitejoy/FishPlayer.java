package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.winfxk.nukkit.whitejoy.sbitem.BaseItem;
import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.Enchantlist;
import cn.winfxk.nukkit.winfxklib.tool.Itemlist;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.*;

public class FishPlayer {
    private final PlayerFishEvent event;
    private static int SBItemCount = 0;
    private static final String[] SBFishKey = {"{Name}"};
    private static final Whitejoy main = Whitejoy.getMain();
    final static Map<String, Item> SBItem = new HashMap<>();
    private static final List<String> BlockPlayer = new ArrayList<>();
    private static final Message msg = Whitejoy.getMain().getMessage();
    private final static Data SBRand = new Data(), FiveRand = new Data();
    private final static Map<String, Integer> SBItemRand = new HashMap<>();
    private static final String[] FishStringKey = {"{Size}", "{Length}"};
    private static final boolean SBModle = main.getconfig().getBoolean("灾难模式");
    private static final boolean FiveModle = main.getconfig().getBoolean("杂物模式");
    private static final File file = new File(Whitejoy.getMain().getConfigDir(), Whitejoy.SBItemFileName);
    public static final String[] BaseKey = {"{Duration}", "{GameTime}", "{Top}", "{Player}", "{Money}", "{FishCount}", "{Rank}", "{FishName}", "{Size}", "{Length}"};
    private static final Config config = new Config(file);
    private final Player player;
    private Item item;

    static {
        String s;
        Item item;
        int rand;
        String[] array;
        Map<String, Object> itemMap;
        Object o = config.get("杂物");
        Map<String, Object> map = !(o instanceof Map) ? new HashMap<>() : (Map<String, Object>) o;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) continue;
            if (entry.getValue() instanceof Map) {
                try {
                    item = Tool.loadItem((Map<String, Object>) entry.getValue());
                    rand = item.count;
                } catch (Exception e) {
                    main.getLogger().error(msg.getMessage("无法加载物品", "{ItemID}", entry.getKey()));
                    e.printStackTrace();
                    continue;
                }
            } else {
                s = Tool.objToString(entry.getValue());
                if (!s.contains("|")) {
                    rand = 1;
                    item = main.getItemlist().getItem(s).getItem();
                } else {
                    array = s.split("\\|");
                    Itemlist itemlist = main.getItemlist().getItem(array[0]);
                    if (itemlist == null) {
                        main.getLogger().error(msg.getMessage("无法加载物品", "{ItemID}", entry.getKey()));
                        continue;
                    }
                    item = itemlist.getItem();
                    if (item == null) {
                        main.getLogger().error(msg.getMessage("无法加载物品", "{ItemID}", entry.getKey()));
                        continue;
                    }
                    if (array.length >= 2 && Tool.isInteger(array[1]) && (rand = Tool.ObjToInt(array[1])) > 0) {
                    } else
                        rand = 1;
                }
            }
            item.setCount(1);
            SBItem.put(entry.getKey(), item);
            SBItemRand.put(entry.getKey(), rand);
            SBItemCount += rand;
        }
        s = main.getconfig().getString("杂物概率");
        array = s.split("\\|");
        if (!s.contains("|") || array.length < 2) {
            FiveRand.Max = 100;
            FiveRand.Min = 10;
        } else {
            FiveRand.Max = Tool.ObjToInt(array[1], 100);
            FiveRand.Min = Tool.ObjToInt(array[0], 10);
        }
        s = main.getconfig().getString("灾难概率");
        array = s.split("\\|");
        if (!s.contains("|") || array.length < 2) {
            SBRand.Max = 100;
            SBRand.Min = 1;
        } else {
            SBRand.Max = Tool.ObjToInt(array[1], 100);
            SBRand.Min = Tool.ObjToInt(array[0], 1);
        }
        o = main.getconfig().get("玩家黑名单");
        List<Object> list = !(o instanceof List) ? new ArrayList<>() : (List<Object>) o;
        for (Object obj : list) {
            s = Tool.objToString(obj, null);
            if (obj == null || s == null || s.isEmpty())
                continue;
            BlockPlayer.add(s.toLowerCase(Locale.ROOT));
        }
    }

    public FishPlayer(PlayerFishEvent event) {
        this.event = event;
        player = event.getPlayer();
        item = event.getLoot();
    }

    protected void Start() {
        if (BlockPlayer.contains(player.getName().toLowerCase(Locale.ROOT))) return;
        if (SBModle && Tool.getRand(0, SBRand.Max) < SBRand.Min) {
            BaseItem item = MainGame.getItem();
            if (item == null) return;
            item.handle(player);
            player.sendMessage(Whitejoy.getMain().getMessage().getSon("Game", "Disaster", SBFishKey, new Object[]{item.getName()}, player));
            return;
        }
        if (FiveModle && Tool.getRand(0, FiveRand.Max) < FiveRand.Min) {
            event.setLoot(getItem());
            player.sendMessage(Whitejoy.getMain().getMessage().getSon("Game", "Sundries", SBFishKey, new Object[]{item.getName()}, player));
            return;
        }
        double Length = getLength();
        double Size = getSize(Length);
        Item item = Item.get(349);
        List<String> list = Whitejoy.getMain().getMessage().getConfig().getList("GameItemName", new ArrayList<>());
        String string = list.get(Tool.getRand(0, list.size() - 1));
        if (string != null)
            item.setCustomName(Whitejoy.getMain().getMessage().getText(string, FishStringKey, new Object[]{Size, Length}, player));
        list = Whitejoy.getMain().getMessage().getConfig().getList("GameItemLore", new ArrayList<>());
        string = list.get(Tool.getRand(0, list.size() - 1));
        if (string != null)
            item.setLore(Whitejoy.getMain().getMessage().getText(string, FishStringKey, new Object[]{Size, Length}, player));
        list = Whitejoy.getMain().getconfig().getList("鱼的附魔", new ArrayList<>());
        Enchantlist enchantlist;
        for (String s : list) {
            enchantlist = WinfxkLib.getMain().getEnchantlist().getEnchant(s);
            if (enchantlist == null) continue;
            item.addEnchantment(enchantlist.getEnchantment());
        }
        CompoundTag nbt = item.getNamedTag();
        nbt = nbt == null ? new CompoundTag() : nbt;
        nbt.putDouble("Size", Size);
        nbt.putDouble("Length", Length);
        nbt.putString(main.getName(), main.getName());
        item.setNamedTag(nbt);
        event.setLoot(item);
        MainGame.setFishTop(player, Size);
        Object[] objects = new Object[]{Tool.getTimeBy(Whitejoy.getMain().GameThread.Time), Whitejoy.getMain().GameThread.Time, Whitejoy.getMain().GameThread.getTopString(), player.getName(), Whitejoy.getMyPlayer(player).getMoney(), ++MainGame.FishCount, MainGame.getRank(player.getName()), item.getName(), Size, Length};
        Server.getInstance().broadcastMessage(Whitejoy.getMain().getMessage().getSon("Game", "NewFish", BaseKey, objects));
    }


    private double getSize(double Length) {
        int i = Tool.getRand(1, 20);
        int Size;
        if (i < 2) {
            Size = Tool.getRand(500, 1000);
        } else if (i < 5) {
            Size = Tool.getRand(200, 500);
        } else if (i < 10) {
            Size = Tool.getRand(100, 200);
        } else
            Size = Tool.getRand(1, 100);
        double sb = Length / 2d;
        sb = sb == 0 ? 0.1 : sb;
        return Tool.Double2((double) (Size / 2) * sb);
    }

    private double getLength() {
        int i = Tool.getRand(1, 20);
        int Length;
        if (i == 1) {
            Length = Tool.getRand(8, 10);
        } else if (i < 5) {
            Length = Tool.getRand(3, 8);
        } else {
            Length = Tool.getRand(1, 3);
        }
        return Tool.Double2(Length + ((double) Tool.getRand(1, 10) / 10));
    }

    protected static Item getItem() {
        int index = Tool.getRand(0, SBItemCount - 1);
        int key = 0;
        Item item = null;
        for (Map.Entry<String, Integer> entry : SBItemRand.entrySet()) {
            if (index < key)
                break;
            item = SBItem.get(entry.getKey());
            key += entry.getValue();
        }
        return item.clone();
    }

    protected static class Data {
        private int Max, Min;
    }
}
