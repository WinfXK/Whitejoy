package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.MyMap;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseItem {
    private String Type;
    private String Name;
    protected String Key;
    protected static final String MainKey = "灾难模式";
    protected static final String[] BaseKey = {"{Player}", "{Money}"};

    public abstract boolean handle(Player player);

    public BaseItem(String Type) {
        this.Type = Type;
        Key = getClass().getSimpleName();
        Name = getClass().getSimpleName();
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    @Override
    public BaseItem clone() {
        try {
            return (BaseItem) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
