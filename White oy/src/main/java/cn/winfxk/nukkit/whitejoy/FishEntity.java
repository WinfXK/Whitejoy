package cn.winfxk.nukkit.whitejoy;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityVex;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import javax.annotation.Nonnull;

public class FishEntity extends EntityVex {
    private final Player player;

    public static CompoundTag getDefaultNBT(Location loc) {
        return new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", loc.x)).add(new DoubleTag("", loc.y)).add(new DoubleTag("", loc.z)))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", (float) loc.getYaw())).add(new FloatTag("", (float) loc.getPitch())))
                .putCompound("Skin", new CompoundTag());
    }

    @Nonnull
    @Override
    public String getName() {
        return Whitejoy.getMain().getMessage().getSon("Game", "MyEntityName", player);
    }

    public FishEntity(Player player) {
        super(player.getChunk(), getDefaultNBT(player));
        this.player = player;
    }
}
