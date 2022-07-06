package cn.winfxk.nukkit.whitejoy.cmd;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.Plugin;
import cn.winfxk.nukkit.whitejoy.MainGame;
import cn.winfxk.nukkit.whitejoy.MyPlayer;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.whitejoy.shop.Shop;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.*;

public class PlayerCommand extends MyCommand {
    private static final String[] RankKey = {"{Player}", "{Money}", "{PlayerName}", "{Size}", "{Top}"};

    public PlayerCommand() {
        super("PlayerCommand", main.getName());
        commandParameters.clear();
        commandParameters.put(getString("开始游戏"), new CommandParameter[]{new CommandParameter(getString("开始游戏"), false, new String[]{"start", "开始游戏"})});
        commandParameters.put(getString("结束游戏"), new CommandParameter[]{new CommandParameter(getString("结束游戏"), false, new String[]{"stop", "结束游戏"})});
        commandParameters.put(getString("游戏商店"), new CommandParameter[]{new CommandParameter(getString("游戏商店"), false, new String[]{"shop", "商店"})});
        commandParameters.put(getString("游戏排行"), new CommandParameter[]{new CommandParameter(getString("游戏排行"), false, new String[]{"top", "排行"})});
        commandParameters.put(getString("游戏总排行"), new CommandParameter[]{new CommandParameter(getString("游戏总排行"), false, new String[]{"tops", "总排行"})});
    }

    @Override
    public boolean execute(CommandSender sender, String lab, String[] strings) {
        if (strings == null || strings.length <= 0) {
            sender.sendMessage(Tool.getCommandHelp(this));
            return false;
        }
        MyPlayer myPlayer;
        List<String> list;
        Map<String, Object> map;
        Map<String, Double> RankTop = new HashMap<>();
        double money;
        Object[] objects;
        String RankName;
        int max;
        switch (strings[0].toLowerCase(Locale.ROOT)) {
            case "top":
            case "排行":
            case "t":
                max = main.getconfig().getInt("排行榜显示长度");
                if (!MainGame.StartGame) {
                    sender.sendMessage(getString("GameNotStart"));
                    break;
                }
                if (max <= 0 || (RankTop = MainGame.getTop()).size() <= 0) {
                    sender.sendMessage(getString("NotRank"));
                    break;
                }
                list = new ArrayList<>(RankTop.keySet());
                money = sender.isPlayer() ? Whitejoy.getMyPlayer(sender.getName()).getMoney() : 0;
                sender.sendMessage(getString("RandMessage"));
                for (int i = 0; i < max && i < RankTop.size(); i++) {
                    objects = new Object[]{sender.getName(), money, RankName = list.get(i), Tool.Double2(Tool.objToDouble(RankTop.get(RankName))), i + 1};
                    sender.sendMessage(message.getSun("Command", "PlayerCommand", "RankItem", RankKey, objects));
                }
                break;
            case "ts":
            case "tops":
            case "总排行":
                max = main.getconfig().getInt("排行榜显示长度");
                if (max <= 0 || main.getRanking().getSize() <= 0) {
                    sender.sendMessage(getString("NotRank"));
                    break;
                }
                map = main.getRanking().getMap();
                for (Map.Entry<String, Object> entry : map.entrySet())
                    RankTop.put(entry.getKey(), Tool.objToDouble(entry.getValue()));
                RankTop = Tool.sortByValueDescending(RankTop);
                list = new ArrayList<>(RankTop.keySet());
                sender.sendMessage(getString("RandMessage"));
                money = sender.isPlayer() ? Whitejoy.getMyPlayer(sender.getName()).getMoney() : 0;
                for (int i = 0; i < list.size() && i < max; i++) {
                    objects = new Object[]{sender.getName(), money, RankName = list.get(i), Tool.Double2(Tool.objToDouble(RankTop.get(RankName))), i + 1};
                    sender.sendMessage(message.getSun("Command", "PlayerCommand", "RankItem", RankKey, objects));
                }
                break;
            case "商店":
            case "shop":
            case "form":
                if (!sender.isPlayer()) {
                    sender.sendMessage(message.getMessage("非玩家"));
                    return true;
                }
                if (!sender.hasPermission(Permission)) {
                    sender.sendMessage(getNotPermission(sender));
                    return true;
                }
                myPlayer = Whitejoy.getMyPlayer(sender.getName());
                myPlayer.showForm(new Shop(myPlayer.getPlayer(), null, true, null));
                return true;
            case "stop":
            case "结束":
            case "终止":
            case "停止":
            case "关闭":
            case "end":
            case "ed":
                if (!sender.hasPermission(OPPermission)) {
                    sender.sendMessage(getNotPermission(sender));
                    return true;
                }
                if (!MainGame.StartGame) {
                    sender.sendMessage(getString("游戏未开始", sender));
                    return true;
                }
                MainGame.StartGame = false;
                sender.sendMessage(getString("游戏已结束", sender));
                return true;
            case "start":
            case "s":
            case "开始":
            case "开始游戏":
            case "开始钓鱼":
                if (!sender.hasPermission(OPPermission)) {
                    sender.sendMessage(getNotPermission(sender));
                    return true;
                }
                if (MainGame.StartGame) {
                    sender.sendMessage(getString("游戏已经开始", sender));
                    return true;
                }
                int Time = main.getconfig().getInt("默认游戏时长", 600);
                if (strings.length >= 2 && Tool.isInteger(strings[1]) && Tool.ObjToInt(strings[1]) > 1)
                    Time = Tool.ObjToInt(strings[1]);
                main.StartGame(Time);
                return true;
            default:
                sender.sendMessage(Tool.getCommandHelp(this));
        }
        return true;
    }
}
