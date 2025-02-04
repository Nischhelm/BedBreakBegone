package bedbreakbegone.core;

import net.blay09.mods.waystones.Waystones;

import net.minecraft.block.Block;

public class WaystonesCompat {
    public static boolean blockIsWaystone(Block block){
        return block.equals(Waystones.blockWaystone);
    }
}
