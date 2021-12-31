package bedbreakbegone.mixin;

import org.spongepowered.asm.mixin.Mixin;
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

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
	
	/*
	@Redirect(
			method = "getBedSpawnPosition(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/util/math/BlockPos;",
			at = @At(value = "INVOKE", 
			target = "Lnet/minecraft/block/BlockBed;getSafeExitLocation(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/util/math/BlockPos;"
			))		
	private BlockPos bbbRedirect_getSafeExitLocation(World worldIn, BlockPos pos, int tries) {
		EnumFacing enumfacing = (EnumFacing)worldIn.getBlockState(pos).getValue(BlockHorizontal.FACING);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for(int yOffset = 0; yOffset <= 1; ++yOffset) {//Fallback to check y+1
        	for(int facingOffset = 0; facingOffset <= 1; ++facingOffset) {//Vanilla facing behavior
                int xOffset = x - enumfacing.getFrontOffsetX()*facingOffset;
                int zOffset = z - enumfacing.getFrontOffsetZ()*facingOffset;
                
                for(int xIter = xOffset-1; xIter <= xOffset+1; ++xIter) {
                    for(int zIter = zOffset-1; zIter <= zOffset+1; ++zIter) {
                        BlockPos blockPos = new BlockPos(xIter, y+yOffset, zIter);
                        
                        if(isValidSpawnPos(worldIn, blockPos)) return blockPos;//Run non-stupid version of vanilla check
                    }
                }
            }
        }
        return null;
	}

	//Actually use the proper Block.canSpawnInBlock() and Material.isSolid() checks, not deprecated and incorrect ones
	private boolean isValidSpawnPos(World worldIn, BlockPos blockPos) {
		return worldIn.getBlockState(blockPos.down()).getMaterial().isSolid() && worldIn.getBlockState(blockPos).getBlock().canSpawnInBlock() && worldIn.getBlockState(blockPos.up()).getBlock().canSpawnInBlock();
	}
	*/
	
	@Inject(at = @At("HEAD"), method = "getBedSpawnLocation(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/util/math/BlockPos;", cancellable = true)	
	private static void bbbInject_getBedSpawnLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn, CallbackInfoReturnable<BlockPos> callback) {
		IBlockState state = worldIn.getBlockState(bedLocation);
        Block block = state.getBlock();
        
        if(!block.isBed(state, worldIn, bedLocation, null))//No bed at pos
        {
            if(!forceSpawn)
            {
                if(ConfigHandler.server.spawnWarning) System.out.println("BBB Non-Forced Spawn failed at: " + bedLocation.getX() + "X " + bedLocation.getY() + "Y " + bedLocation.getZ() + "Z");
                callback.setReturnValue(null);//Was a bed, no longer is there
            }
            else
            {
                callback.setReturnValue(iterateSpawnPoint(worldIn, bedLocation));
            }
        }
        else
        {
            callback.setReturnValue(iterateBedPoint(worldIn, bedLocation));
        }
	}
	
	private static BlockPos iterateSpawnPoint(World world, BlockPos pos) {
		if(isValidSpawnPos(world, pos, false)) return pos;//Check default first, then fallback to 3x3
		
		int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        
        for(int xIter = x-1; xIter <= x+1; ++xIter) {
        	for(int zIter = z-1; zIter <= z+1; ++zIter) {
                BlockPos blockPos = new BlockPos(xIter, y, zIter);
                
                if(isValidSpawnPos(world, blockPos, false)) return blockPos;
        	}
        }
        if(ConfigHandler.server.spawnWarning) System.out.println("BBB Non-Bed Spawnpoint failed at: " + x + "X " + y + "Y " + z + "Z");
        return null;
	}
	
	private static BlockPos iterateBedPoint(World world, BlockPos pos) {
		EnumFacing enumfacing = (EnumFacing)world.getBlockState(pos).getValue(BlockHorizontal.FACING);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for(int yOffset = 0; yOffset <= 1; ++yOffset) {//Fallback to check y+1
        	for(int facingOffset = 0; facingOffset <= 1; ++facingOffset) {//Vanilla facing behavior
                int xOffset = x - enumfacing.getFrontOffsetX()*facingOffset;
                int zOffset = z - enumfacing.getFrontOffsetZ()*facingOffset;
                
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
	private static boolean isValidSpawnPos(World worldIn, BlockPos blockPos, boolean requireFloor) {
		return (worldIn.getBlockState(blockPos.down()).getMaterial().isSolid() || !requireFloor) && worldIn.getBlockState(blockPos).getBlock().canSpawnInBlock() && worldIn.getBlockState(blockPos.up()).getBlock().canSpawnInBlock();
	}
}
