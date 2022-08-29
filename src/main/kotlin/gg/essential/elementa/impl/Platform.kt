package gg.essential.elementa.impl

import org.jetbrains.annotations.ApiStatus
import java.util.*

@ApiStatus.Internal
interface Platform {
    val mcVersion: Int

    var currentScreen: Any?

    fun isAllowedInChat(char: Char): Boolean

    fun enableStencil()

    fun isCallingFromMinecraftThread(): Boolean

    fun deleteFramebuffers(buffer: Int)

    fun genFrameBuffers(): Int

    fun framebufferTexture2D(targt: Int, attachment: Int, textarget: Int, texture: Int, level: Int)

    fun bindFramebuffer(target: Int, framebuffer: Int)

    fun runOnMinecraftThread(runnable: () -> Unit)

    @ApiStatus.Internal
    companion object {
        internal val platform: Platform =
            ServiceLoader.load(Platform::class.java, Platform::class.java.classLoader).iterator().next()
    }
}