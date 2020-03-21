package club.sk1er.mods.core.universal;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class UniversalGraphicsHandler {
    //#if MC<=10809
    private WorldRenderer instance;
    //#else
    //$$ private BufferBuilder instance;
    //$$
    //#endif
    //#if MC<=10809
    public UniversalGraphicsHandler(WorldRenderer instance) {
        this.instance = instance;
    }

    //#else
    //$$ public UniversalGraphicsHandler(BufferBuilder instance) {
    //$$     this.instance = instance;
    //$$ }
    //#endif

    public static void pushMatrix() {
        GlStateManager.pushMatrix();
    }

    public static void popMatrix() {
        GlStateManager.popMatrix();
    }

    public static UniversalGraphicsHandler getFromTessellator() {
        //#if MC<=10809
        return new UniversalGraphicsHandler(getTessellator().getWorldRenderer());
        //#else
        //$$ return new UniversalGraphicsHandler(getTessellator().getBuffer());
        //#endif
    }

    public static void translate(float x, float y, float z) {
        translate((double) x, (double) y, (double) z); //Don't remove double casts or this breaks
    }

    public static void translate(double x, double y, double z) {
        GlStateManager.translate(x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z) {
        GlStateManager.rotate(angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        scale((double) x, (double) y, (double) z);
    }

    public static void scale(double x, double y, double z) {
        GlStateManager.scale(x, y, z);
    }

    public static Tessellator getTessellator() {
        return Tessellator.getInstance();
    }

    public static void draw() {
        getTessellator().draw();
    }

    public static void cullFace(int mode) {
        //#if MC>10809
        //$$ GlStateManager.CullFace[] values = GlStateManager.CullFace.values();
        //$$ for (GlStateManager.CullFace value : values) {
        //$$     if (value.mode == mode) {
        //$$         GlStateManager.cullFace(value);
        //$$         return;
        //$$     }
        //$$ }
        //$$ throw new IllegalArgumentException(String.format("Mode %d is not valid!", mode));
        //#else
        GlStateManager.cullFace(mode);
        //#endif

    }

    public static void enableBlend() {
        GlStateManager.enableBlend();
    }

    public static void disableTexture2D() {
        GlStateManager.disableTexture2D();
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
    }

    public static void enableTexture2D() {
        GlStateManager.enableTexture2D();
    }

    public static void disableBlend() {
        GlStateManager.disableBlend();
    }

    public static void deleteTexture(int glTextureId) {
        GlStateManager.deleteTexture(glTextureId);
    }

    public static void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    public static void bindTexture(int glTextureId) {
        GlStateManager.bindTexture(glTextureId);
    }

    public static int getStringWidth(String in) {
        return UniversalMinecraft.getFontRenderer().getStringWidth(in);
    }

    public static void drawString(String text, float x, float y, int color, boolean shadow) {
        UniversalMinecraft.getFontRenderer().drawString(text, x, y, color, shadow);
    }

    public void begin(int glMode, VertexFormat format) {
        instance.begin(glMode, format);
    }

    public UniversalGraphicsHandler pos(double x, double y, double z) {
        instance.pos(x, y, z);
        return this;
    }

    public UniversalGraphicsHandler color(float red, float green, float blue, float alpha) {
        instance.color(red, green, blue, alpha);
        return this;
    }

    public void endVertex() {
        instance.endVertex();
    }

    public UniversalGraphicsHandler tex(double u, double v) {
        instance.tex(u, v);
        return this;
    }

    public static List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return UniversalMinecraft.getFontRenderer().listFormattedStringToWidth(str, wrapWidth);
    }

    public static int getCharWidth(char character) {
        return UniversalMinecraft.getFontRenderer().getCharWidth(character);
    }

    public static void glClear(int mode) {
        GL11.glClear(mode);
    }

    public static void glClearStencil(int mode) {
        GL11.glClearStencil(mode);
    }
}
