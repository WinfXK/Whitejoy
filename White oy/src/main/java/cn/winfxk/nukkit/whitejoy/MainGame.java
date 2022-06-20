package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.winfxk.nukkit.whitejoy.sbitem.*;
import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.module.LeaveWord;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.ItemLoad;
import cn.winfxk.nukkit.winfxklib.tool.MyMap;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.*;

public class MainGame extends Thread {
    public static final String[] BaseKey = {"{Duration}", "{GameTime}", "{Top}", "{Player}", "{Money}", "{FishCount}", "{Rank}"};
    public static final String[] BaseKeyByLow = {"{Duration}", "{GameTime}", "{Player}", "{Money}", "{FishCount}"};
    public static final String[] FishPlayerStopKey = {"{MaxPlayer}", "{MaxSize}"};
    protected static final Map<String, BaseItem> BaseItems = new HashMap<>();
    private static final Map<String, Integer> Items = new HashMap<>();
    private static final Message msg = Whitejoy.getMain().getMessage();
    private static final Map<String, Double> FishTop = new HashMap<>();
    private static final Server server = Server.getInstance();
    public static transient boolean StartGame = false;
    private static final Whitejoy main = Whitejoy.getMain();
    private static final String MainKey = "Game";
    static int FishCount = 0;
    private static Integer ItemCount;
    protected int Time;
    private static List<TopItem> topItems = new ArrayList<>();

    static {
        addItem(new DropItem());
        addItem(new Damage());
        addItem(new Oligemia());
        addItem(new Transfer());
        addItem(new Death());
        addItem(new Kick());
        addItem(new Poisoning());
        relaod();
    }

    public static synchronized void relaod() {
        Items.clear();
        ItemCount = 0;
        Config config = FishPlayer.config;
        Object obj = config.get("灾难");
        Map<String, Object> map = obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null || !Tool.isInteger(entry.getValue())) continue;
            Items.put(entry.getKey(), Tool.ObjToInt(entry.getValue()));
            ItemCount += Tool.ObjToInt(entry.getValue());
        }
        map = config.getMap("奖励", new MyMap<>());
        Map<String, Object> ItemMap;
        TopItem topItem;
        for (Object ob1j : map.values()) {
            if (!(ob1j instanceof Map)) continue;
            ItemMap = (Map<String, Object>) ob1j;
            topItem = new TopItem();
            obj = ItemMap.get("Items");
            topItem.Items = ItemLoad.getItem(!(obj instanceof Map) ? new HashMap<>() : (Map<String, Object>) obj, new ArrayList<>());
            obj = ItemMap.get("Economys");
            topItem.Economys = ItemLoad.getEconomy(!(obj instanceof Map) ? new HashMap<>() : (Map<String, Object>) obj, new ArrayList<>());
            obj = ItemMap.get("Commands");
            topItem.Commands = ItemLoad.getCommand(obj instanceof List ? (List<String>) obj : new ArrayList<>(), new ArrayList<>());
            topItems.add(topItem);
        }
    }

    public static BaseItem getItem() {
        int index = Tool.getRand(0, ItemCount - 1);
        int key = 0;
        BaseItem item = null;
        for (Map.Entry<String, Integer> entry : Items.entrySet()) {
            if (index < key)
                break;
            item = BaseItems.get(entry.getKey());
            key += entry.getValue();
        }
        return item.clone();
    }

    public MainGame(int Time) {
        this.Time = Time;
    }

    public static void setFishTop(Player player, Double size) {
        FishTop.put(player.getName(), size);
    }

    public static void addFishCount() {
        FishCount++;
    }

    @Override
    public void run() {
        FishTop.clear();
        FishCount = 0;
        StartGame = true;
        boolean ErrorExit = true;
        server.broadcastMessage(msg.getSon(MainKey, "StartGame", BaseKey, getData(null)));
        while (StartGame) {
            if (--Time <= 0) {
                ErrorExit = false;
                break;
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                server.broadcastMessage(msg.getSon(MainKey, "ErrorStop", BaseKey, getData(null)));
                return;
            }
            if (Time == 10 || Time == 30 || (Time <= 5 && Time >= 0) || Time % 60 == 0)
                server.broadcastMessage(msg.getSon(MainKey, "Countdown", BaseKey, getData(null)));
        }
        StartGame = false;
        if (ErrorExit) {
            server.broadcastMessage(msg.getSon(MainKey, "ForcedStop", BaseKey, getData(null)));
            return;
        }
        List<String> list = new ArrayList<>(getTop().keySet());
        if (list.size() <= 0) {
            server.broadcastMessage(msg.getSon(MainKey, "StopButNoPlayer"));
            return;
        }
        Map<String, Object> map = main.getRanking().getMap();
        double obj;
        for (Map.Entry<String, Double> entry : FishTop.entrySet()) {
            if (map.containsKey(entry.getKey())) {
                obj = Tool.objToDouble(map.get(entry.getKey()), 0d);
                if (obj < entry.getValue())
                    map.put(entry.getKey(), entry.getValue());
                continue;
            }
            map.put(entry.getKey(), entry.getValue());
        }
        main.getRanking().setAll(map);
        main.getRanking().save();
        LinkedHashMap<String, Double> Top = getTop();
        if (Top.size() > 0) {
            Player player = null;
            TopItem topItem;
            List<String> TopPlayerList = new ArrayList<>(Top.keySet());
            for (int i = 0; i < TopPlayerList.size() && i < topItems.size(); i++) {
                player = server.getPlayer(TopPlayerList.get(i));
                if (player == null || !player.isOnline()) continue;
                topItem = topItems.get(i);
                if (topItem == null) continue;
                for (Item item : topItem.Items)
                    player.getLevel().dropItem(player, item);
                for (LeaveWord.Economy economy : topItem.Economys) {
                    if (economy.getEconomy() == null) continue;
                    economy.getEconomy().addMoney(player, economy.getMoney());
                }
                for (ItemLoad.MyCommand command : topItem.Commands)
                    command.onCommand(player);
                Object[] objects = new Object[]{Tool.getTimeBy(Time), Time, getTopString(), player.getName(), Whitejoy.getMyPlayer(player).getMoney(), FishCount, getRank(player.getName())};
                player.sendMessage(Whitejoy.getMain().getMessage().getSon("Game", "GiveTopItem", BaseKey, objects));
            }
        }
        server.broadcastMessage(msg.getSon(MainKey, "Stop", FishPlayerStopKey, new Object[]{list.get(0), Top.get(list.get(0))}));
        super.run();
    }

    public static void setFishTop(Player player, double FishSIze) {
        double Size = FishTop.containsKey(player.getName()) ? FishTop.get(player.getName()) : 0;
        FishTop.put(player.getName(), Math.max(FishSIze, Size));
    }

    private Object[] getData(Player player) {
        return new Object[]{Tool.getTimeBy(Time), Time, getTopString(), player == null ? "" : player.getName(), player == null ? "" : Whitejoy.getMyPlayer(player).getMoney(), FishCount, player == null ? 0 : getRank(player.getName())};
    }

    /**
     * 返回排行榜的字符集
     *
     * @return
     */
    Object getTopString() {
        String s = "";
        LinkedHashMap<String, Double> map = getTop();
        List<String> list = new ArrayList<>(map.keySet());
        int Count = Whitejoy.getMain().getconfig().getInt("排名显示数量");
        String playerName;
        MyPlayer myPlayer;
        for (int i = 0; (Count <= 0 || i < Count) && i < list.size(); i++) {
            playerName = list.get(i);
            //TODO
            myPlayer = Whitejoy.getMyPlayer(playerName);
            s += (s.isEmpty() ? "" : "\n") + msg.getSon(MainKey, "TopItem", BaseKeyByLow, new Object[]{Tool.getTimeBy(Time), Time, playerName, myPlayer == null ? 0 : myPlayer.getMoney(), FishCount});
        }
        return s;
    }

    /**
     * 返回玩家排名位置
     *
     * @param player
     * @return
     */
    public static int getRank(String player) {
        if (player == null || player.isEmpty()) return 0;
        LinkedHashMap<String, Double> map = getTop();
        ArrayList<String> list = new ArrayList<>(map.keySet());
        return list.indexOf(player) + 1;
    }

    public static void addItem(BaseItem item) {
        BaseItems.put(item.getType(), item);
    }

    public static void removeItem(String Key) {
        BaseItems.remove(Key);
    }

    public static Map<String, BaseItem> getBaseItems() {
        return new HashMap<>(BaseItems);
    }

    /**
     * 获取排名
     *
     * @return
     */
    public static LinkedHashMap<String, Double> getTop() {
        return new LinkedHashMap<>(Tool.sortByValueDescending(FishTop));
    }

    static class TopItem {
        List<LeaveWord.Economy> Economys;
        List<Item> Items;
        List<ItemLoad.MyCommand> Commands;
    }
}
