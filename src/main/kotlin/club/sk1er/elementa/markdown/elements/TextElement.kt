package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.MarkdownComponent
import club.sk1er.elementa.markdown.MarkdownState
import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UniversalDesktop
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.Color
import java.awt.image.BufferedImage
import java.net.URI
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class TextElement internal constructor(internal val spans: List<Span>) : Element() {
    internal var renderables: List<Pair<Span, List<Renderable>>>? = null

    override fun draw(state: MarkdownState) {
        draw(state, state.textConfig.color)
    }

    internal data class Rectangle(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    ) {
        val width = x2 - x1
        val height = y2 - y1
    }

    internal open class Renderable(val bounds: Rectangle)

    internal class TextRenderable(
        bounds: Rectangle,
        val text: String,
        val cutOffBeginning: Boolean,
        val cutOffEnd: Boolean
    ) : Renderable(bounds)

    internal class ImageRenderable(
        bounds: Rectangle,
        val image: BufferedImage,
        val texture: DynamicTexture
    ) : Renderable(bounds)

    override fun onClick(mouseX: Float, mouseY: Float) {
        val clicked = renderables!!
            .filter { it.first.style.isURL }
            .firstOrNull { (_, renderables) ->
                renderables.any { renderable ->
                    renderable.bounds.let {
                        mouseX > it.x1 && mouseX < it.x2 && mouseY > it.y1 && mouseY < it.y2
                    }
                }
            }

        if (clicked != null)
            UniversalDesktop.browse(URI(clicked.first.style.url!!))
    }

    internal fun calculateRenderables(state: MarkdownState) {
        renderables = spans.map { span ->
            if (span.style.isEmbed) {
                if (span.style.bufferedImage?.let { it.isDone && !it.isCompletedExceptionally } == true) {
                    if (state.x != state.newlineX)
                        state.gotoNextLine()

                    val image = span.style.bufferedImage!!.get()

                    if (span.style.texture == null)
                        span.style.texture = UniversalGraphicsHandler.getTexture(image)

                    val width = image.width.toFloat().coerceAtMost(state.width)
                    val scale = width / image.width
                    val height = image.height * scale

                    val renderable = ImageRenderable(
                        Rectangle(
                            state.left + state.x,
                            state.top + state.y,
                            state.left + state.x + width,
                            state.top + state.y + height
                        ),
                        image,
                        span.style.texture!!
                    )

                    state.y += image.height * scale

                    return@map span to listOf(renderable)
                }
            }

            fun textWidth(text: String) = if (span.style.code) {
                state.codeFontRenderer.getWidth(text) * state.textScaleModifier +
                    state.inlineCodeConfig.leftPadding + state.inlineCodeConfig.rightPadding
            } else text.width(state.textScaleModifier)

            fun textHeight(text: String) = if (span.style.code) {
                state.codeFontRenderer.getHeight(text) * state.textScaleModifier
            } else 9f * state.textScaleModifier

            var text = span.styledText
            val scale = state.textScaleModifier
            val width = textWidth(text)
            val renderables = mutableListOf<Renderable>()

            if (state.x + width <= state.width) {
                renderables.add(TextRenderable(
                    Rectangle(
                        (state.left + state.x) / scale,
                        (state.top + state.y) / scale,
                        (state.left + state.x + width) / scale,
                        (state.top + state.y + textHeight(text)) / scale
                    ),
                    text,
                    cutOffBeginning = false,
                    cutOffEnd = false
                ))

                if (span.endsInNewline) {
                    state.gotoNextLine()
                } else {
                    state.x += width
                }
            } else {
                var textIndex = text.length
                var first = true

                outer@ while (text.isNotEmpty()) {
                    while (textIndex > 0) {
                        if (textWidth(text.substring(0, textIndex)) + state.x <= state.width)
                            break
                        textIndex--
                    }

                    if (textIndex - 1 < text.lastIndex && text[textIndex] != ' ') {
                        // Handle word wrapping
                        while (text[textIndex - 1] != ' ') {
                            if (textIndex - 1 == 0) {
                                state.gotoNextLine()
                                textIndex = text.length
                                continue@outer
                            }
                            textIndex--
                        }
                    }

                    val textToDraw = text.substring(0, textIndex)
                    val textToDrawWidth = textWidth(textToDraw)
                    text = text.substring(textIndex).trimStart()

                    renderables.add(TextRenderable(
                        Rectangle(
                            (state.left + state.x) / scale,
                            (state.top + state.y) / scale,
                            (state.left + state.x + textToDrawWidth) / scale,
                            (state.top + state.y + textHeight(textToDraw)) / scale
                        ),
                        textToDraw,
                        cutOffBeginning = !first,
                        cutOffEnd = text.isNotEmpty()
                    ))

                    if (text.isNotEmpty()) {
                        state.gotoNextLine()
                    } else {
                        state.x += textToDrawWidth
                    }

                    textIndex = text.length

                    first = false
                }

                if (span.endsInNewline)
                    state.gotoNextLine()
            }

            span to renderables
        }
    }

    fun draw(state: MarkdownState, textColor: Color, avoidRenderableCalculation: Boolean = false) {
        if (!avoidRenderableCalculation)
            calculateRenderables(state)

        renderables!!.forEach { (span, spanRenderables) ->
            fun drawString(text: String, x: Float, y: Float, color: Color, shadow: Boolean) {
                val actualColor = if (span.style.isURL) {
                    state.config.urlConfig.fontColor
                } else color

                if (span.style.code) {
                    state.codeFontRenderer.drawString(
                        text,
                        x + state.inlineCodeConfig.leftPadding,
                        y - 1.5f,
                        actualColor.rgb,
                        shadow
                    )
                } else {
                    UniversalGraphicsHandler.drawString(text, x, y, actualColor.rgb, shadow)
                }
            }

            fun drawBackground(x1: Float, y1: Float, x2: Float, y2: Float, cutOffBeginning: Boolean = false, cutOffEnd: Boolean = false) {
                if (!span.style.code || avoidRenderableCalculation)
                    return

                state.config.inlineCodeConfig.run {
                    UIRoundedRectangle.drawRoundedRectangle(
                        if (cutOffBeginning) state.left - 10 else x1 - leftPadding,
                        y1 - topPadding,
                        if (cutOffEnd) state.left + state.width + 10 else x2 + rightPadding,
                        y2 + bottomPadding,
                        cornerRadius,
                        outlineColor
                    )

                    UIRoundedRectangle.drawRoundedRectangle(
                        if (cutOffBeginning) state.left - 10 else x1 - leftPadding + outlineWidth,
                        y1 - topPadding + outlineWidth,
                        if (cutOffEnd) state.left + state.width + 10 else x2 + rightPadding - outlineWidth,
                        y2 + bottomPadding - outlineWidth,
                        cornerRadius,
                        backgroundColor
                    )
                }
            }

            val scale = state.textScaleModifier.toDouble()
            UniversalGraphicsHandler.scale(scale, scale, 1.0)

            val mouseX = UniversalMouse.getScaledX()
            val mouseY = UniversalResolutionUtil.getInstance().scaledHeight - UniversalMouse.getScaledY()

            val isHovered = span.style.isURL && spanRenderables.any { renderable ->
                renderable.bounds.let {
                    mouseX > it.x1 && mouseX < it.x2 && mouseY > it.y1 && mouseY < it.y2
                }
            }

            spanRenderables.forEach {
                if (it is TextRenderable) {
                    drawBackground(
                        it.bounds.x1,
                        it.bounds.y1,
                        it.bounds.x2,
                        it.bounds.y2,
                        it.cutOffBeginning,
                        it.cutOffEnd
                    )

                    drawString(
                        span.style.applyStyle(it.text),
                        it.bounds.x1,
                        it.bounds.y1,
                        textColor,
                        state.textConfig.shadow
                    )

                    if (isHovered && state.config.urlConfig.showBarOnHover) {
                        UIBlock.drawBlock(
                            state.config.urlConfig.barColor,
                            it.bounds.x1.toDouble(),
                            it.bounds.y2.toDouble() + state.config.urlConfig.spaceBeforeBar,
                            it.bounds.x2.toDouble(),
                            it.bounds.y2.toDouble() + state.config.urlConfig.spaceBeforeBar + state.config.urlConfig.barWidth
                        )
                    }
                } else if (it is ImageRenderable) {
                    drawTexture(
                        it.texture,
                        Color.WHITE,
                        it.bounds.x1.toDouble(),
                        it.bounds.y1.toDouble(),
                        it.bounds.width.toDouble(),
                        it.bounds.height.toDouble()
                    )
                }
            }

            UniversalGraphicsHandler.scale(1f / scale, 1f / scale, 1.0)
        }
    }

    internal data class Style(
        var italic: Boolean = false,
        var bold: Boolean = false,
        var code: Boolean = false,
        var url: String? = null,
        var bufferedImage: CompletableFuture<BufferedImage>? = null,
        var texture: DynamicTexture? = null
    ) {
        val isURL: Boolean
            get() = url != null

        val isEmbed: Boolean
            get() = bufferedImage != null

        fun applyStyle(text: String) = (if (bold) "§l" else "") + (if (italic) "§o" else "") + text
    }

    internal class Span(
        text: String,
        val style: Style,
        var endsInNewline: Boolean = false
    ) {
        val styledText: String
            get() = style.applyStyle(text)

        var text: String = text
            private set

        init {
            if (style.code)
                this.text = text.replace("\n", "")
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        spans.forEach {
            it.style.texture?.let { texture ->
                val glTextureId = texture.glTextureId
                if (glTextureId != 0 && glTextureId != -1) {
                    UniversalGraphicsHandler.deleteTexture(glTextureId);
                }
            }
        }
    }

    companion object {
        private val specialChars = listOf('*', '_', '`', '[', ']', ')', '!')

        fun parse(text: String): TextElement {
            val style = Style()
            var spanStart = 0
            val spans = mutableListOf<Span>()
            var couldBeEmbed = false
            var inURL = false
            var inURLText = false
            val replacedText = text.replace("\n", " ")

            fun addSpan(index: Int, hasNewline: Boolean = false) {
                if (index == spanStart && !hasNewline)
                    return
                spans.add(Span(replacedText.substring(spanStart, index), style.copy(), hasNewline))
            }

            var index = 0
            loop@ while (index < replacedText.length) {
                val ch = replacedText[index]

                if (ch == '\\' && index != replacedText.lastIndex) {
                    index++
                    continue
                }

                if (!isSpecialChar(ch) || (inURL && ch != ')') || (style.code && ch != '`')) {
                    index++
                    continue
                }

                when (ch) {
                    '*', '_' -> {
                        addSpan(index)
                        if (index + 1 <= replacedText.lastIndex && ch == replacedText[index + 1]) {
                            index++
                            style.bold = !style.bold
                        } else {
                            style.italic = !style.italic
                        }
                    }
                    '`' -> {
                        addSpan(index)
                        if (style.code) {
                            style.code = false
                        } else if (index + 1 < replacedText.length) {
                            val remainingLines = replacedText.substring(index + 1).split('\n')
                            var lineIndex = 0

                            while (lineIndex < remainingLines.size) {
                                val line = remainingLines[lineIndex]
                                if (line.isBlank())
                                    break
                                lineIndex++
                            }

                            style.code = remainingLines.subList(0, lineIndex).any { '`' in it }
                        }
                    }
                    '!' -> {
                        if (!inURL && !inURLText)
                            couldBeEmbed = true
                        index++
                        continue@loop
                    }
                    '[' -> {
                        if (index + 3 >= text.length) {
                            index++
                            continue@loop
                        }

                        val remainingText = replacedText.substring(index + 1)
                        val cbIndex = remainingText.indexOf(']')
                        val opIndex = remainingText.indexOf('(')
                        val cpIndex = remainingText.indexOf(')')

                        if (cbIndex == -1 || opIndex == -1 || cpIndex == -1 || cbIndex >= opIndex || opIndex >= cpIndex) {
                            index++
                            continue@loop
                        }

                        inURLText = true
                        addSpan(if (couldBeEmbed) index - 1 else index)
                    }
                    ']' -> {
                        if (inURLText) {
                            inURLText = false
                            inURL = true
                            addSpan(index)
                            index++
                        } else {
                            index++
                            continue@loop
                        }
                    }
                    ')' -> {
                        if (inURL) {
                            inURL = false
                            val url = replacedText.substring(spanStart, index)
                            spans.last().style.url = url

                            if (couldBeEmbed) {
                                spans.last().style.bufferedImage = CompletableFuture.supplyAsync {
                                    ImageIO.read(URL(url))
                                }
                            }

                            couldBeEmbed = false
                        } else {
                            index++
                            continue@loop
                        }
                    }
                    else -> throw IllegalStateException()
                }

                index++
                spanStart = index
            }

            addSpan(replacedText.length)

            return TextElement(spans)
        }

        private fun isSpecialChar(ch: Char) = ch in specialChars
    }
}