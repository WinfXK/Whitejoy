package cn.winfxk.nukkit.whitejoy.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class MyCommand extends Command {
    public static final String OPPermission = "Whitejoy.Command.Main";
    public static final String Permission = "Whitejoy.Command.Admin";
    protected static Whitejoy main = Whitejoy.getMain();
    protected static Message message = Whitejoy.getMain().getMessage();
    protected static final String BaseKey = "Command";
    protected String CmdKey;

    public MyCommand(String name) {
        super(name.toLowerCase(Locale.ROOT), message.getSun(BaseKey, name, "description"), message.getSun(BaseKey, name, "usageMessage"), getAliases(name));
        CmdKey = name;
    }

    public static String[] getAliases(String s) {
        List<Object> list = main.getCommand().getList(s, new ArrayList<>());
        String[] strings = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            strings[i] = Tool.objToString(list.get(i));
        return strings;
    }

    public String getNotPermission(CommandSender sender) {
        return getString("权限不足", sender);
    }


    public String getString(String Key, CommandSender sender) {
        return sender.isPlayer() ? message.getSun(BaseKey, CmdKey, Key, Whitejoy.getMyPlayer(sender.getName()).getPlayer()) : message.getSun(BaseKey, CmdKey, Key);
    }

    public String getString(String Key) {
        return message.getSun(BaseKey, CmdKey, Key);
    }

    public String getString(String Key, String[] Keys, Object[] Data) {
        return message.getSun(BaseKey, CmdKey, Key, Keys, Data);
    }
}
