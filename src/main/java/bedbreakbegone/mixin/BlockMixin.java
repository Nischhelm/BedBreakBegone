package bedbreakbegone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public abstract class BlockMixin {
	
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
}
