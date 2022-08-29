package gg.essential.elementa.impl;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ChatAllowedCharacters;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//#if MC>=11700
//$$ import static org.lwjgl.opengl.GL30.glBindFramebuffer;
//$$ import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
//$$ import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
//$$ import static org.lwjgl.opengl.GL30.glGenFramebuffers;
//#elseif MC>=11400
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#else
import static net.minecraft.client.renderer.OpenGlHelper.glBindFramebuffer;
import static net.minecraft.client.renderer.OpenGlHelper.glDeleteFramebuffers;
import static net.minecraft.client.renderer.OpenGlHelper.glFramebufferTexture2D;
import static net.minecraft.client.renderer.OpenGlHelper.glGenFramebuffers;
//#endif


@ApiStatus.Internal
@SuppressWarnings("unused") // instantiated via reflection from Platform.Companion
public class PlatformImpl implements Platform {

    @Override
    public int getMcVersion() {
        //#if MC==11801
        //$$ return 11801;
        //#elseif MC==11701
        //$$ return 11701;
        //#elseif MC==11602
        //$$ return 11602;
        //#elseif MC==11502
        //$$ return 11502;
        //#elseif MC==11202
        return 11202;
        //#elseif MC==10809
        //$$ return 10809;
        //#endif
    }

    @Nullable
    @Override
    public Object getCurrentScreen() {
        return Minecraft.getMinecraft().currentScreen;
    }

    @Override
    public void setCurrentScreen(@Nullable Object screen) {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen) screen);
    }

    @Override
    public boolean isAllowedInChat(char c) {
        return ChatAllowedCharacters.isAllowedCharacter(c);
    }

    @Override
    public void enableStencil() {
        //#if MC<11500
        Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
        if (!framebuffer.isStencilEnabled()) {
            framebuffer.enableStencil();
        }
        //#endif
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        //#if MC>=11400
        //$$ return Minecraft.getInstance().isOnExecutionThread();
        //#else
        return Minecraft.getMinecraft().isCallingFromMinecraftThread();
        //#endif
    }

    @Override
    public void deleteFramebuffers(int buffer) {
        //#if MC<=11202 || MC>=11700
        glDeleteFramebuffers(buffer);
        //#else
        //$$ GlStateManager.deleteFramebuffers(buffer);
        //#endif
    }

    @Override
    public int genFrameBuffers() {
        //#if MC<=11202 || MC>=11700
        return glGenFramebuffers();
        //#else
        //$$ return GlStateManager.genFramebuffers();
        //#endif
    }

    @Override
    public void framebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        //#if MC<=11202 || MC>=11700
        glFramebufferTexture2D(target, attachment, textarget, texture, level);
        //#else
        //$$ GlStateManager.framebufferTexture2D(target, attachment, textarget, texture, level);
        //#endif
    }

    @Override
    public void bindFramebuffer(int target, int framebuffer) {
        //#if MC<=11202 || MC>=11700
        glBindFramebuffer(target, framebuffer);
        //#else
        //$$ GlStateManager.bindFramebuffer(target, framebuffer);
        //#endif
    }


    @Override
    public void runOnMinecraftThread(@NotNull Function0<Unit> runnable) {
        //#if MC<=11202
        Minecraft.getMinecraft().addScheduledTask(runnable::invoke);
        //#else
        //$$ Minecraft.getInstance().execute(runnable::invoke);
        //#endif
    }
}
