package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.DummyBossBar;
import cn.winfxk.nukkit.whitejoy.cmd.PlayerCommand;
import cn.winfxk.nukkit.winfxklib.Check;
import cn.winfxk.nukkit.winfxklib.Message;
import cn.winfxk.nukkit.winfxklib.MyBase;
import cn.winfxk.nukkit.winfxklib.WinfxkLib;
import cn.winfxk.nukkit.winfxklib.tool.Config;
import cn.winfxk.nukkit.winfxklib.tool.Itemlist;
import cn.winfxk.nukkit.winfxklib.tool.Tool;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Whitejoy extends MyBase implements Listener {
    public static final String PlayerDirName = "Players";
    public static final String SBItemFileName = "Items.yml";
    public static final String ShopFileName = "FishShop.yml";
    public static final String ConfigFileName = "Config.yml";
    public static final String MessageFileName = "Message.yml";
    public static final String RankingFileName = "Ranking.yml";
    public static final String CommandFileName = "Command.yml";
    public static final String[] Meta = {ConfigFileName, MessageFileName, CommandFileName};
    private static final Map<String, MyPlayer> MyPlayers = new HashMap<>();
    public static final String[] Load = {SBItemFileName};
    private final List<String> GameLevel = new ArrayList<>();
    private Config config, Ranking, Command;
    protected transient MainGame GameThread;
    protected static Whitejoy main;
    private Itemlist itemlist;
    private Instant instant;
    private Message message;
    private File PlayerDir;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!MainGame.StartGame && event.getItem() != null && event.getItem().getId() == 346) {
            if (Tool.getRand(1, 1000) <= 10) {
                player.getLevel().addSound(player, Sound.RANDOM_EXPLODE);
                for (int i = 0; i < 50; i++)
                    player.getLevel().addParticle(new ExplodeParticle(new Vector3(player.x + ((double) Tool.getRand(-10, 10) / 10), player.y + ((double) Tool.getRand(-10, 10) / 10), player.z + ((double) Tool.getRand(-10, 10) / 10))));
                player.sendMessage(message.getSon("Game", "NotGameTimeStartGame", player));
                FishEntity fish = new FishEntity(player);
                player.attack(new EntityDamageEvent(fish, EntityDamageEvent.DamageCause.ENTITY_ATTACK, (float) Tool.getRand(1, Math.max(player.getHealth(), 2))));
                fish.kill();
            }
            event.setCancelled();
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!MainGame.StartGame) {
            if (Tool.getRand(1, 1000) <= 10) {
                player.getLevel().addSound(player, Sound.RANDOM_EXPLODE);
                for (int i = 0; i < 50; i++)
                    player.getLevel().addParticle(new ExplodeParticle(new Vector3(player.x + ((double) Tool.getRand(-10, 10) / 10), player.y + ((double) Tool.getRand(-10, 10) / 10), player.z + ((double) Tool.getRand(-10, 10) / 10))));
                player.sendMessage(message.getSon("Game", "NotGameTimeStartGame", player));
                FishEntity fish = new FishEntity(player);
                player.attack(new EntityDamageEvent(fish, EntityDamageEvent.DamageCause.ENTITY_ATTACK, (float) Tool.getRand(1, Math.max(player.getHealth(), 2))));
                fish.kill();
            }
            event.setCancelled();
            return;
        }
        if (!GameLevel.contains(player.getLevel().getFolderName().toLowerCase(Locale.ROOT)))
            return;
        MyPlayer myPlayer = Whitejoy.getMyPlayer(player);
        if (myPlayer.bossBar == null)
            try {
                DummyBossBar.Builder builder = new DummyBossBar.Builder(player);
                builder.text(GameThread.getBarData(player));
                player.createBossBar(myPlayer.bossBar = builder.length((float) GameThread.Time / GameThread.initialTime * 100).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        new FishPlayer(event).Start();
    }

    @Override
    public void onLoad() {
        getLogger().info(getName() + " loading" + Tool.getColorFont("......."));
        main = this;
        super.onLoad();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info(message.getMessage("插件关闭", "{loadTime}", Tool.getTimeBy(Duration.between(instant, Instant.now()).toMillis())));
    }

    public Config getRanking() {
        return Ranking;
    }

    public Config getconfig() {
        return config;
    }

    public void StartGame(int Time) {
        (GameThread = new MainGame(Time)).start();
    }

    @Override
    public void onEnable() {
        instant = Instant.now();
        itemlist = WinfxkLib.getMain().getItemlist();
        new Check(this, Meta, new String[]{PlayerDirName}, Load).start();
        config = new Config(new File(getConfigDir(), ConfigFileName));
        message = new Message(this, new File(getConfigDir(), MessageFileName));
        PlayerDir = new File(getDataFolder(), PlayerDirName);
        Ranking = new Config(new File(getConfigDir(), RankingFileName));
        Command = new Config(new File(getConfigDir(), CommandFileName));
        getServer().getCommandMap().register(getName() + "-PlayerCommand", new PlayerCommand());
        getServer().getPluginManager().registerEvents(this, this);
        List<Object> list = config.getList("游戏世界", new ArrayList<>());
        String s;
        for (Object o : list) {
            if (o == null) continue;
            s = Tool.objToString(o);
            if (s == null || s.isEmpty()) continue;
            GameLevel.add(s.toLowerCase(Locale.ROOT));
        }
        super.onEnable();
        Plugin plugin = Server.getInstance().getPluginManager().getPlugin("WC-Vault");
        if (plugin != null && config.getBoolean("WC-Vault"))
            VC_Economy.load();
        getLogger().info(message.getMessage("插件启动", "{loadTime}", (float) Duration.between(instant, Instant.now()).toMillis() + "ms"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MyPlayers.remove(player.getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MyPlayers.put(player.getName(), new MyPlayer(player));
    }

    public static MyPlayer getMyPlayer(Player player) {
        return MyPlayers.get(player.getName());
    }

    public static MyPlayer getMyPlayer(String player) {
        return MyPlayers.get(player);
    }

    public File getPlayerDir() {
        return PlayerDir;
    }

    public Message getMessage() {
        return message;
    }

    public Config getCommand() {
        return Command;
    }

    @Override
    public File getConfigDir() {
        return getDataFolder();
    }

    public static Whitejoy getMain() {
        return main;
    }

    public Itemlist getItemlist() {
        return itemlist;
    }
}
