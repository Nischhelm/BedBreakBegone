package bedbreakbegone.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;

@Mod(modid = BedBreakBegone.MODID, version = BedBreakBegone.VERSION, name = BedBreakBegone.NAME)
public class BedBreakBegone
{
    public static final String MODID = "bedbreakbegone";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "BedBreakBegone";
    public static final String CHANNEL = "BEDBREAKBEGONE";
	
	@Instance(MODID)
	public static BedBreakBegone instance;
	
	//All we do is load a mixin, shhh
}
