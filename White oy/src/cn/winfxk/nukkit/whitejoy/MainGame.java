package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.winfxk.nukkit.whitejoy.sbitem.*;
import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.*;

public class MainGame extends Thread {
    public static final String[] BaseKey = {"{Duration}", "{GameTime}", "{Top}", "{Player}", "{Money}", "{FishCount}", "{Rank}"};
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
        Config config = new Config(new File(Whitejoy.getMain().getConfigDir(), Whitejoy.SBItemFileName));
        Map<String, Object> map = config.getMap("灾难");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null || !Tool.isInteger(entry.getValue())) continue;
            Items.put(entry.getKey(), Tool.ObjToInt(entry.getValue()));
            ItemCount += Tool.ObjToInt(entry.getValue());
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
        server.broadcastMessage(msg.getSon(MainKey, "StartGame", BaseKey, getData((Player) null)));
        while (StartGame) {
            if (Time-- <= 0) {
                ErrorExit = false;
                break;
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                server.broadcastMessage(msg.getSon(MainKey, "ErrorStop", BaseKey, getData((Player) null)));
                return;
            }
            if (Time == 10 || Time == 30 || (Time <= 5 && Time >= 0) || Time % 60 == 0)
                server.broadcastMessage(msg.getSon(MainKey, "Countdown", BaseKey, getData((Player) null)));
        }
        StartGame = false;
        if (ErrorExit) {
            server.broadcastMessage(msg.getSon(MainKey, "ForcedStop", BaseKey, getData((Player) null)));
            return;
        }
        super.run();
    }

    public void sendMessage(Player player, String Message) {
        server.broadcastMessage(msg.getSon(MainKey, Message, BaseKey, getData(player)));
    }

    public static void setFishTop(Player player, double FishSIze) {
    }

    public static void setFishTop(String player, double FishSIze) {
        double Size = FishTop.containsKey(player) ? FishTop.get(player) : 0;
        FishTop.put(player, Math.max(FishSIze, Size));
    }

    private Object[] getData(Player player) {
        return new Object[]{Tool.getTimeBy(Time), Time, getTopString(), player == null ? "" : player.getName(), player == null ? "" : Whitejoy.getMyPlayer(player).getMoney(), FishCount, player == null ? 0 : getRank(player.getName())};
    }

    private Object[] getData(String player) {
        return new Object[]{Tool.getTimeBy(Time), Time, getTopString(), player == null ? "" : player, player == null ? "" : Whitejoy.getMyPlayer(player).getMoney(), FishCount, player == null ? 0 : getRank(player)};
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
        for (int i = 0; (Count <= 0 || i < Count) && i < list.size(); i++)
            s += (s.isEmpty() ? "" : "\n") + msg.getSon(MainKey, "TopItem", BaseKey, getData(list.get(i)));
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
        return list.indexOf(player);
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
}
