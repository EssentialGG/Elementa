package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.elements.Element
import kotlin.reflect.KClass

data class MarkdownState(
    val left: Float,
    val top: Float,
    var width: Float,
    val height: Float,
    var x: Float = 0f,
    var y: Float = 0f,
    var textScaleModifier: Float = 1f,
    var newlineX: Float = 0f,
    val config: MarkdownConfig = MarkdownConfig(),
    var previousElementType: KClass<out Element>? = null
) {
    val headerConfig: HeaderConfig
        get() = config.headerConfig

    val listConfig: ListConfig
        get() = config.listConfig

    val textConfig: TextConfig
        get() = config.textConfig

    val blockquoteConfig: BlockquoteConfig
        get() = config.blockquoteConfig

    val inlineCodeConfig: InlineCodeConfig
        get() = config.inlineCodeConfig

    val codeblockConfig: CodeblockConfig
        get() = config.codeblockConfig

    val urlConfig: URLConfig
        get() = config.urlConfig

    fun gotoNextLine() {
        x = newlineX
        y += 9f * textScaleModifier + textConfig.spaceBetweenLines
    }
}