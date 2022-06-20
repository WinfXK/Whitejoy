package cn.winfxk.nukkit.whitejoy.cmd;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.winfxk.nukkit.whitejoy.MainGame;
import cn.winfxk.nukkit.whitejoy.MyPlayer;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.whitejoy.shop.Shop;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.Locale;

public class PlayerCommand extends MyCommand {
    public PlayerCommand() {
        super("PlayerCommand", main.getName());
        commandParameters.clear();
        commandParameters.put(getString("开始游戏"), new CommandParameter[]{new CommandParameter(getString("开始游戏"), false, new String[]{"start", "开始游戏"})});
        commandParameters.put(getString("结束游戏"), new CommandParameter[]{new CommandParameter(getString("结束游戏"), false, new String[]{"stop", "结束游戏"})});
        commandParameters.put(getString("游戏商店"), new CommandParameter[]{new CommandParameter(getString("游戏商店"), false, new String[]{"shop", "商店"})});
    }

    @Override
    public boolean execute(CommandSender sender, String lab, String[] strings) {
        if (strings == null || strings.length <= 0)
            return false;
        MyPlayer myPlayer;
        switch (strings[0].toLowerCase(Locale.ROOT)) {
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
        }
        return true;
    }
}
