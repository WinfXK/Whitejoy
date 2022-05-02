package cn.winfxk.nukkit.whitejoy.shop;

import cn.nukkit.Player;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;

import java.io.File;

public class BuyFish extends BaseForm {
    private File file;
    private String Key;

    public BuyFish(Player player, BaseFormin Update, boolean isBack, File file, String Key) {
        super(player, Update, isBack);
        this.Key = Key;
        this.file = file;
    }

    @Override
    public boolean MakeForm() {
        return false;
    }
}
