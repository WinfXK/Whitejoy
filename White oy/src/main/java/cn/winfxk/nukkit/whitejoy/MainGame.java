package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.utils.DummyBossBar;
import cn.winfxk.nukkit.whitejoy.sbitem.*;
import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.module.LeaveWord;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.ItemLoad;
import cn.winfxk.nukkit.winfxklib.tool.MyMap;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import javax.annotation.Nonnull;
import java.util.*;

public class MainGame extends Thread {
    public static final String[] BaseKey = {"{Duration}", "{GameTime}", "{Top}", "{Player}", "{Money}", "{FishCount}", "{Rank}"};
    public static final String[] GameTimeKey = {"{Duration}", "{GameTime}", "{Player}", "{Money}", "{FishCount}", "{Rank}", "{PlayerSize}"};
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
    protected int Time, initialTime;
    private static final List<TopItem> topItems = new ArrayList<>();

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
        Object obj = config.get("??????");
        Map<String, Object> map = obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null || !Tool.isInteger(entry.getValue())) continue;
            Items.put(entry.getKey(), Tool.ObjToInt(entry.getValue()));
            ItemCount += Tool.ObjToInt(entry.getValue());
        }
        map = config.getMap("??????", new MyMap<>());
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
        this.Time = initialTime = Time;
        FishTop.clear();
    }

    public static void setFishTop(@Nonnull Player player, Double size) {
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
        server.getOnlinePlayers().forEach((uuid, player) -> {
            try {
                DummyBossBar.Builder builder = new DummyBossBar.Builder(player);
                MyPlayer myPlayer = Whitejoy.getMyPlayer(player);
                builder.text(getBarData(player));
                player.createBossBar(myPlayer.bossBar = builder.length(100f).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        while (StartGame) {
            if (--Time <= 0) {
                ErrorExit = false;
                break;
            }
            final float MyTime = (float) Time / initialTime * 100;
            server.getOnlinePlayers().forEach((uuid, player) -> {
                if (player == null || !player.isOnline()) return;
                MyPlayer myPlayer = Whitejoy.getMyPlayer(player);
                if (myPlayer == null) return;
                if (myPlayer.bossBar != null) {
                    try {
                        myPlayer.bossBar.setText(getBarData(player));
                        myPlayer.bossBar.setLength(MyTime <= 0 ? 1 : MyTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                server.broadcastMessage(msg.getSon(MainKey, "ErrorStop", BaseKey, getData(null)));
                return;
            }
            if (Time == 10 || Time == 30 || (Time <= 5 && Time >= 0) || Time % 60 == 0)
                server.broadcastMessage(msg.getSon(MainKey, "Countdown", BaseKey, getData(null)));
        }
        main.getRanking().save();
        server.getOnlinePlayers().forEach((uuid, player) -> {
            try {
                MyPlayer myPlayer = Whitejoy.getMyPlayer(player);
                if (myPlayer != null && myPlayer.bossBar != null)
                    player.removeBossBar(myPlayer.bossBar.getBossBarId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    protected String getBarData(Player player) {
        return msg.getSon(MainKey, "GameTime", GameTimeKey, new Object[]{Tool.getTimeBy(Time), Time, player.getName(), Whitejoy.getMyPlayer(player).getMoney(), FishCount, getRank(player.getName()), FishTop.size()});
    }

    public static void setFishTop(@Nonnull Player player, double FishSIze) {
        String playerName = player.getName();
        double Size = FishTop.containsKey(playerName) ? FishTop.get(playerName) : 0;
        FishTop.put(playerName, Math.max(FishSIze, Size));
        Size = main.getRanking().containsKey(playerName) ? main.getRanking().getDouble(playerName) : 0;
        main.getRanking().set(playerName, Math.max(Size, FishSIze));
    }

    private Object[] getData(Player player) {
        return new Object[]{Tool.getTimeBy(Time), Time, getTopString(), player == null ? "" : player.getName(), player == null ? "" : Whitejoy.getMyPlayer(player).getMoney(), FishCount, player == null ? 0 : getRank(player.getName())};
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    Object getTopString() {
        String s = "";
        LinkedHashMap<String, Double> map = getTop();
        List<String> list = new ArrayList<>(map.keySet());
        int Count = Whitejoy.getMain().getconfig().getInt("??????????????????");
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
     * ????????????????????????
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
     * ????????????
     *
     * @return
     */
    public static LinkedHashMap<String, Double> getTop() {
        return new LinkedHashMap<>(FishTop.size() <= 0 ? FishTop : Tool.sortByValueDescending(FishTop));
    }

    static class TopItem {
        List<LeaveWord.Economy> Economys;
        List<Item> Items;
        List<ItemLoad.MyCommand> Commands;
    }
}
