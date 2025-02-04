package bedbreakbegone.mixin;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockWaystone.class)
public abstract class BlockWaystoneMixin {
    @ModifyArg(
            method = "activateWaystone",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSpawnPoint(Lnet/minecraft/util/math/BlockPos;Z)V")
    )
    private boolean bbb_waystonesBlockWaystone_setSpawnPoint(boolean pos){
        //Make waystone behave like bed and check if it's still there
        return false;
    }

    @Redirect(
            method = "activateWaystone",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;")
    )
    private BlockPos bbb_waystonesBlockWaystone_offsetBlockPos(BlockPos instance, EnumFacing facing){
        //Don't offset waystone position so EntityPlayer.getBedSpawnLocation can check if it is still there
        return instance;
    }
}
