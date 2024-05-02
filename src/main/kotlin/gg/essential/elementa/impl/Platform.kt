package gg.essential.elementa.impl

import org.jetbrains.annotations.ApiStatus
import java.util.*

@ApiStatus.Internal
interface Platform {
    val mcVersion: Int

    var currentScreen: Any?

    fun enableStencil()

    fun isCallingFromMinecraftThread(): Boolean

    @ApiStatus.Internal
    companion object {
        internal val platform: Platform =
            ServiceLoader.load(Platform::class.java, Platform::class.java.classLoader).iterator().next()
    }
}