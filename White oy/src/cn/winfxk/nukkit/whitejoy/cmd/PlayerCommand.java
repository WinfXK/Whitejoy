package cn.winfxk.nukkit.whitejoy.cmd;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.winfxk.nukkit.whitejoy.MainGame;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.Locale;

public class PlayerCommand extends MyCommand {
    public PlayerCommand() {
        super("PlayerCommand");
        commandParameters.clear();
        commandParameters.put(getString("开始游戏"), new CommandParameter[]{new CommandParameter(getString("开始游戏"), false, new String[]{"start", "开始游戏"})});
    }

    @Override
    public boolean execute(CommandSender sender, String lab, String[] strings) {
        if (strings == null || strings.length <= 0)
            return false;
        switch (strings[0].toLowerCase(Locale.ROOT)) {
            case "stop":
            case "结束":
            case "终止":
            case "停止":
            case "关闭":

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
