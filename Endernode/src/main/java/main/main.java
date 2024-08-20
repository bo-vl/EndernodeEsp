package main;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import utils.Endernode;
import utils.Renderer;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class main {
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Renderer());
        MinecraftForge.EVENT_BUS.register(new Endernode());
        MinecraftForge.EVENT_BUS.register(this);
    }
}
