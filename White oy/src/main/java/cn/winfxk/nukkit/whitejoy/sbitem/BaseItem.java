package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.winfxk.nukkit.whitejoy.Whitejoy;

public abstract class BaseItem implements Cloneable {
    private final String Type;
    protected String Key;

    public abstract boolean handle(Player player);

    public BaseItem(String Type) {
        this.Type = Type;
        Key = getClass().getSimpleName();
    }

    public String getName(Player player) {
        return getSimpleName(player);
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

    protected String getSimpleName(Player player) {
        return Whitejoy.getMain().getMessage().getSun("Game", "SBItem", Key, player);
    }
}
