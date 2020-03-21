package club.sk1er.mods.core.universal;

import net.minecraft.client.gui.GuiButton;

public class UniversalGuiButton {

    public static int getX(GuiButton button) {
        //#if MC<=10809
        return button.xPosition;
        //#else
        //$$ return button.x;
        //#endif
    }

    public static int getY(GuiButton button) {
        //#if MC<=10809
        return button.yPosition;
        //#else
        //$$ return button.y;
        //#endif
    }
}
