package cn.winfxk.nukkit.whitejoy.sbitem;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import cn.winfxk.nukkit.whitejoy.Whitejoy;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.tool.Effectlist;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.util.ArrayList;
import java.util.List;

public class Poisoning extends BaseItem {
    private static final List<EffectTime> Effects = new ArrayList<>();
    private static final EffectTime BaseEffect = new EffectTime(WinfxkLib.getMain().getEffectlist().getEffet("中毒").getEffect());

    public Poisoning() {
        super("Poisoning");
    }

    static {
        List<Object> list = Whitejoy.getMain().getconfig().getList("灾难模式药水效果", new ArrayList<>());
        String string;
        String[] strings;
        Effectlist effect;
        EffectTime time;
        for (Object obj : list) {
            if (obj == null) continue;
            string = Tool.objToString(obj, "");
            if (string.isEmpty()) continue;
            strings = string.split("|");
            effect = WinfxkLib.getMain().getEffectlist().getEffet(strings[0]);
            if (effect == null) continue;
            time = new EffectTime(effect.getEffect());
            if (strings.length <= 1 || strings[1] == null || strings[1].isEmpty()) {
                time.Max = 30;
                time.Min = 10;
            } else {
                strings = strings[1].split("-");
                if (Tool.isInteger(strings[0]) && (strings.length >= 2 && Tool.isInteger(strings[1]))) {
                    time.Max = Tool.ObjToInt(strings[1]);
                    time.Min = Tool.ObjToInt(strings[0]);
                } else {
                    time.Max = 30;
                    time.Min = 10;
                }
            }
            Effects.add(time);
        }
        BaseEffect.Min = 10;
        BaseEffect.Max = 30;
    }

    @Override
    public boolean handle(Player player) {
        EffectTime time = Effects.size() < 1 ? BaseEffect : Effects.get(Tool.getRand(0, Effects.size() - 1));
        time.setDuration(Tool.getRand(time.Min, time.Max) * 20);
        player.addEffect(time);
        return true;
    }

    static class EffectTime extends Effect {
        private int Min, Max;

        public EffectTime(Effect effect) {
            super(effect.getId(), effect.getName(), effect.getColor()[0], effect.getColor()[1], effect.getColor()[2]);
        }
    }
}
