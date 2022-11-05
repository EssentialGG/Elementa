package gg.essential.elementa.impl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.manager.ResolutionManager
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
interface ExternalInspectorDisplay {

    val visible: Boolean

    fun updateVisiblity(visible: Boolean)

    fun addComponent(component: UIComponent)

    fun removeComponent(component: UIComponent)

    fun getWidth(): Int

    fun getHeight(): Int

    fun updateFrameBuffer(resolutionManager: ResolutionManager)

    fun cleanup()

}