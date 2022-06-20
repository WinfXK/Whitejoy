package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.winfxk.nukkit.whitejoy.MyPlayer;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;
import cn.winfxk.nukkit.winfxklib.tool.Itemlist;

public abstract class BaseForm extends BaseFormin {
    protected static Whitejoy main = Whitejoy.getMain();
    protected MyPlayer myPlayer;
    protected static Itemlist itemlist = WinfxkLib.getMain().getItemlist();

    public BaseForm(Player player, BaseFormin Update, boolean isBack) {
        super(player, Update, isBack);
        myPlayer = Whitejoy.getMyPlayer(player);
        message = Whitejoy.getMain().getMessage();
    }
}
