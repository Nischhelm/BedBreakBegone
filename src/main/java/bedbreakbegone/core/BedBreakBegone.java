package bedbreakbegone.core;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = BedBreakBegone.MODID,
        version = BedBreakBegone.VERSION,
        name = BedBreakBegone.NAME,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:fermiumbooter"
)
public class BedBreakBegone
{
    public static final String MODID = "bedbreakbegone";
    public static final String VERSION = "1.0.3";
    public static final String NAME = "BedBreakBegone";
	
	//All we do is load a mixin and config, shhh
}
