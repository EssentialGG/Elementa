package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.elements.Element
import kotlin.reflect.KClass

data class MarkdownState(
    val left: Float,
    val top: Float,
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    var textScaleModifier: Float,
    val config: MarkdownConfig = MarkdownConfig(),
    var previousElementType: KClass<out Element>? = null
) {
    val headerConfig: HeaderConfig
        get() = config.headerConfig

    val listConfig: ListConfig
        get() = config.listConfig

    val textConfig: TextConfig
        get() = config.textConfig
}