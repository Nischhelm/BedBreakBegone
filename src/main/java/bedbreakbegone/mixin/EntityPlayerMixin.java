package bedbreakbegone.mixin;

import bedbreakbegone.core.WaystonesCompat;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import bedbreakbegone.handlers.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

	@Inject(at = @At("HEAD"), method = "getBedSpawnLocation(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/util/math/BlockPos;", cancellable = true)
	private static void bbbInject_getBedSpawnLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn, CallbackInfoReturnable<BlockPos> cir) {
		IBlockState state = worldIn.getBlockState(bedLocation);
        Block block = state.getBlock();

        //Respawn at bed
        if(block.isBed(state, worldIn, bedLocation, null))
            cir.setReturnValue(iterateBedPoint(worldIn, bedLocation));

        //Respawn at forced spawn point or waystone
        else if(forceSpawn || (Loader.isModLoaded("waystones") && WaystonesCompat.blockIsWaystone(block)))
            cir.setReturnValue(iterateSpawnPoint(worldIn, bedLocation));

        //No bed or waystone found at spawnpoint, spawn not forced. Respawn at worldspawn / randomly
        else {
            if(ConfigHandler.server.spawnWarning) System.out.println("BBB Non-Forced Spawn failed at: " + bedLocation.getX() + "X " + bedLocation.getY() + "Y " + bedLocation.getZ() + "Z");
            cir.setReturnValue(null);
        }
	}

    //First adjacent, then corners, clockwise starting from north / north-east
    // Player looks southwards at respawn, so looks at waystone in default respawn
    @Unique private static final int[] xOrder = {0, 1, 0, -1, 1, 1, -1, -1};
    @Unique private static final int[] zOrder = {-1, 0, 1, 0, -1, 1, 1, -1};

    @Unique
	private static BlockPos iterateSpawnPoint(World world, BlockPos pos) {
        if(isValidSpawnPos(world, pos, false)) return pos;//Check default first, then fallback to 3x3. Always fails for waystone

		int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for(int i = 0; i < xOrder.length; i++){
            BlockPos blockPos = new BlockPos(x+xOrder[i], y, z+zOrder[i]);
            if(isValidSpawnPos(world, blockPos, false)) return blockPos;
        }

        if(ConfigHandler.server.spawnWarning) System.out.println("BBB Non-Bed Spawnpoint failed at: " + x + "X " + y + "Y " + z + "Z");
        return null;
	}

    @Unique
	private static BlockPos iterateBedPoint(World world, BlockPos pos) {
		EnumFacing enumfacing = (EnumFacing)world.getBlockState(pos).getValue(BlockHorizontal.FACING);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for(int yOffset = 0; yOffset <= 1; ++yOffset) {//Fallback to check y+1
        	for(int facingOffset = 0; facingOffset <= 1; ++facingOffset) {//Vanilla facing behavior
                int xOffset = x - enumfacing.getXOffset()*facingOffset;
                int zOffset = z - enumfacing.getZOffset()*facingOffset;
                
                for(int xIter = xOffset-1; xIter <= xOffset+1; ++xIter) {
                    for(int zIter = zOffset-1; zIter <= zOffset+1; ++zIter) {
                        BlockPos blockPos = new BlockPos(xIter, y+yOffset, zIter);
                        
                        if(isValidSpawnPos(world, blockPos, true)) return blockPos;
                    }
                }
            }
        }
        if(ConfigHandler.server.spawnWarning) System.out.println("BBB Bed Spawnpoint failed at: " + x + "X " + y + "Y " + z + "Z");
        return null;
	}
	
	//Actually use the proper Block.canSpawnInBlock() and Material.isSolid() checks, not deprecated and incorrect ones
	@Unique
    private static boolean isValidSpawnPos(World worldIn, BlockPos blockPos, boolean requireFloor) {
		return (worldIn.getBlockState(blockPos.down()).getMaterial().isSolid() || !requireFloor) &&
                worldIn.getBlockState(blockPos).getBlock().canSpawnInBlock() &&
                worldIn.getBlockState(blockPos.up()).getBlock().canSpawnInBlock();
	}
}
