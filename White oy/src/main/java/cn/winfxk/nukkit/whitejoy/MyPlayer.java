package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.utils.DummyBossBar;
import cn.winfxk.nukkit.winfxklib.MyPlayerin;
import cn.winfxk.nukkit.winfxklib.tool.Config;

import java.io.File;

public class MyPlayer extends MyPlayerin {
    private final Config config;
    private final File file;
    private static Whitejoy main = Whitejoy.getMain();
    public DummyBossBar bossBar;

    public MyPlayer(Player player) {
        super(player);
        file = new File(main.getPlayerDir(), player.getName() + ".yml");
        config = new Config(file);
    }

    public File getFile() {
        return file;
    }

    public Config getConfig() {
        return config;
    }
}
