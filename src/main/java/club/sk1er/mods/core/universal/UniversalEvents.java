package club.sk1er.mods.core.universal;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.List;

public class UniversalEvents {
    public static class UGuiOpenEvent {
        public static GuiScreen getGui(GuiOpenEvent event) {
            //#if MC<=10809
            return event.gui;
            //#else
            //$$ return event.getGui();
            //#endif
        }

    }


    public static class UGuiScreenEvent {
        public static GuiScreen getGui(GuiScreenEvent event) {
            //#if MC<=10809
            return event.gui;
            //#else
            //$$ return event.getGui();
            //#endif
        }
    }
    public static class UGuiScreenEvent$InitGuiEvent {

        public static List<GuiButton> getButtonList(GuiScreenEvent.InitGuiEvent event) {
            //#if MC<=10809
            return event.buttonList;
            //#else
            //$$ return event.getButtonList();
            //#endif
        }
    }
    public static class UGuiScreenEvent$ActionPerformedEvent {

        public static GuiButton getButton(GuiScreenEvent.ActionPerformedEvent event) {
            //#if MC<=10809
            return event.button;
            //#else
            //$$ return event.getButton();
            //#endif
        }
    }
    public static class URenderPlayerEvent {
        public static EntityPlayer getPlayer(RenderPlayerEvent event) {
            //#if MC>10809
            //$$ return event.getEntityPlayer();
            //#else
            return event.entityPlayer;
            //#endif
        }

        public static double getX(RenderPlayerEvent event) {
            //#if MC>10809
            //$$ return event.getX();
            //#else
            return event.x;
            //#endif
        }

        public static double getY(RenderPlayerEvent event) {
            //#if MC>10809
            //$$ return event.getY();
            //#else
            return event.y;
            //#endif
        }

        public static double getZ(RenderPlayerEvent event) {
            //#if MC>10809
            //$$ return event.getZ();
            //#else
            return event.z;
            //#endif
        }

        public static RenderPlayer getRenderer(RenderPlayerEvent event) {
            //#if MC>10809
            //$$ return event.getRenderer();
            //#else
            return event.renderer;
            //#endif
        }

        public static float getPartialTicks(RenderPlayerEvent event) {
            //#if MC<=10809
            return event.partialRenderTick;
            //#else
            //$$ return event.getPartialRenderTick();
            //#endif
        }
    }

}
