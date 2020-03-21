package club.sk1er.mods.core.universal;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class UniversalTextComponent {


    //#if MC<=10809
    public static IChatComponent buildSimple(String in) {
        return new ChatComponentText(in);
        //#else
        //$$ public static ITextComponent buildSimple(String in) {
        //$$ return new TextComponentString(in);
        //#endif
    }

    public static String getTextWithoutFormattingCodes(String in) {
        //#if MC<=10809
        return EnumChatFormatting.getTextWithoutFormattingCodes(in);
        //#else
        //$$ return TextFormatting.getTextWithoutFormattingCodes(in);
        //#endif
    }
}
