package bedbreakbegone.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import bedbreakbegone.core.BedBreakBegone;

@Config(modid = BedBreakBegone.MODID)
public class ConfigHandler {
	
	@Config.Comment("Server Config")
	@Config.Name("Server")
	public static final ServerConfig server = new ServerConfig();
	
	public static class ServerConfig{
		
		@Config.Comment("Output a warning and coordinates in the log when a spawnpoint fails?")
		@Config.Name("Failed Spawn Warning")
		public boolean spawnWarning= false;
	}
	
	@Mod.EventBusSubscriber(modid = BedBreakBegone.MODID)
	private static class EventHandler{
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(BedBreakBegone.MODID)) ConfigManager.sync(BedBreakBegone.MODID, Config.Type.INSTANCE);
		}
	}
}