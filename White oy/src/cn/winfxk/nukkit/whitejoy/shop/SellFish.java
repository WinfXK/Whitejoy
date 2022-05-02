package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;

import java.io.File;

public class SellFish extends BaseForm {
    public SellFish(Player player, BaseFormin Update, boolean isBack, File file) {
        super(player, Update, isBack);
    }

    @Override
    public boolean MakeForm() {
        return false;
    }
}
