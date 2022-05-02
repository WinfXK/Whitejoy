package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.winfxk.nukkit.whitejoy.shop.BaseForm;
import cn.winfxk.nukkit.winfxklib.form.BaseFormin;

public class Setting extends BaseForm {
    public Setting(Player player, BaseFormin Update, boolean isBack) {
        super(player, Update, isBack);
    }

    @Override
    public boolean MakeForm() {
        return false;
    }
}
