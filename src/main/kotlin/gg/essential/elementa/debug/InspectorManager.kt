package gg.essential.elementa.debug

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.impl.ExternalInspectorDisplay
import gg.essential.elementa.impl.Platform

/**
 * The inspector manager provides debug utilities for debugging with the [Inspector] or with a custom debug component.
 */
object InspectorManager {


    private val debugSessions = mutableMapOf<Window, DebugSession>()
    private val platform = Platform.platform

    /**
     * Controls whether new inspectors or debug components open in a detached window or on the window they are debugging.
     */
    var startDetached = System.getProperty("elementa.inspector.detached", "true") == "true"

    /**
     * Toggles the current [Inspector] and/or custom debug window existing in an external display
     */
    fun toggleInspectorDetached(window: Window) {
        val session = debugSessions[window] ?: return

        session.isExternal = !session.isExternal

        session.components.filterIsInstance<Inspector>().forEach { inspector ->
            inspector.setFloating(false) // To prevent it from lingering as a floating component
        }

        if (session.isExternal) {
            session.components.forEach {
                window.removeChild(it)
                session.display.addComponent(it)
            }
        } else {
            session.components.forEach {
                session.display.removeComponent(it)
                window.addChild(it)
            }
        }
    }

    /**
     * Called immediately following [Window.draw] with the current window
     */
    internal fun onDraw(window: Window) {
        val openDisplay = debugSessions[window] ?: return
        openDisplay.display.updateFrameBuffer(window.resolutionManager)
    }

    /**
     * Cleans up the inspector and/or debug component from the supplied [window]
     */
    private fun cleanupWindow(window: Window) {
        val inspectorState = debugSessions[window] ?: return
        inspectorState.components.filterIsInstance<Inspector>().forEach { inspector ->
            inspector.cleanup()
        }
        inspectorState.display.cleanup()
        debugSessions.remove(window)
    }

    /**
     * Adds a custom debug component either directly to [window] or to an external display
     * depending on whether the current session is external or not. If no debug instance exists,
     * then a new one will be created from on the current value of [startDetached]
     */
    fun addCustomDebugComponent(window: Window, component: UIComponent) {
        val openDisplay = debugSessions[window]
        if (openDisplay == null) {
            createExternalDisplay(window, component)
        } else {
            openDisplay.components.add(component)

            if (openDisplay.isExternal) {
                openDisplay.display.addComponent(component)
            } else {
                window.addChild(component)
            }
        }
    }

    /**
     * Removes a custom debug component from the debug session of the supplied [window].
     * If the result of this operation leaves no debug components, then the debug session is closed.
     *
     * If no debug session exists, then nothing will be done.
     */
    fun removeCustomDebugComponent(window: Window, component: UIComponent) {
        val session = debugSessions.remove(window) ?: return
        session.components.remove(component)
        if (session.isExternal) {
            session.display.removeComponent(component)
        } else {
            window.removeChild(component)
        }
        if (session.components.isEmpty()) {
            cleanupWindow(window)
        }
    }

    /**
     * Creates a new debug session for the supplied [window] and adds [component] to it.
     */
    private fun createExternalDisplay(window: Window, component: UIComponent) {
        val display = platform.generateExternalDisplay()
        debugSessions[window] = DebugSession(
            mutableListOf(component),
            display,
            startDetached
        )
        if (startDetached) {
            display.addComponent(component)
        } else {
            window.addChild(component)
        }
    }

    /**
     * Toggles the inspector for the supplied [window]
     */
    fun toggleInspector(window: Window) {
        val openInspector = debugSessions[window]
        if (openInspector == null) {
            val inspector = Inspector(window) {
                removeCustomDebugComponent(window, this)
            }
            addCustomDebugComponent(window, inspector)
        } else {
            val inspectors = openInspector.components.filterIsInstance<Inspector>()
            if (inspectors.isEmpty()) {
                addCustomDebugComponent(window, Inspector(window) {
                    removeCustomDebugComponent(window, this)
                })
            } else {
                inspectors.forEach {
                    removeCustomDebugComponent(window, it)
                }
            }
        }
    }


    private data class DebugSession(
        val components: MutableList<UIComponent>,
        val display: ExternalInspectorDisplay,
        var isExternal: Boolean,
    )

}