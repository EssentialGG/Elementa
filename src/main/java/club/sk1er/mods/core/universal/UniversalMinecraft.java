package club.sk1er.mods.core.universal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

public class UniversalMinecraft {

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static WorldClient getWorld() {
        //#if MC<=10809
        return getMinecraft().theWorld;
        //#else
        //$$ return getMinecraft().world;
        //#endif
    }

    public static NetHandlerPlayClient getNetHandler() {
        //#if MC<=10809
        return getMinecraft().getNetHandler();
        //#else
        //$$ return getPlayer().connection;
        //#endif
    }

    public static EntityPlayerSP getPlayer() {
        //#if MC<=10809
        return getMinecraft().thePlayer;
        //#else
        //$$ return getMinecraft().player;
        //#endif
    }

    public static FontRenderer getFontRenderer() {
        //#if MC<=10809
        return getMinecraft().fontRendererObj;
        //#else
        //$$ return getMinecraft().fontRenderer;
        //#endif
    }
}
