package club.sk1er.elementa.font;

import club.sk1er.mods.core.universal.UGraphics;
import club.sk1er.mods.core.universal.UMinecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class VanillaFontRenderer implements FontProvider {
    @Override
    public float getStringWidth(@NotNull String string, float pointSize) {
        return UMinecraft.getFontRenderer().getStringWidth(string);
    }

    @Override
    public void drawString(@NotNull String string, @NotNull Color color, float x, float y, float originalPointSize, boolean shadow, @Nullable Color shadowColor) {
        if (shadowColor == null) {
            UGraphics.drawString(string, x, y, color.getRGB(), shadow);
        } else {
            UGraphics.drawString(string, x, y, color.getRGB(), shadowColor.getRGB());
        }
    }
}
