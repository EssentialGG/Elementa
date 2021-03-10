package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.markdown.selection.TextCursor
import club.sk1er.mods.core.universal.UGraphics
import club.sk1er.mods.core.universal.UMouse
import club.sk1er.mods.core.universal.UResolution

class TextDrawable(
    config: MarkdownConfig,
    text: String,
    val style: Style
) : Drawable(config) {
    // Used by HeaderDrawable
    var scaleModifier = 1f

    var formattedText: String = style.formattingSymbols + text
        private set

    // Used by the selection API to tell this Drawable how it is
    // selected. These do not consider style characters
    var selectionStart = -1
    var selectionEnd = -1

    // Used to store the Text classes to render between beforeDraw and draw
    private var texts = mutableListOf<Text>()

    // Stores whether or not this component is hovered
    private var hovered = false

    // Populated with any text drawables which used to be part of this
    // drawable, but were split with a call to split(). Used to show
    // hovered links across newline boundaries
    private var linkedTexts = mutableListOf<TextDrawable>()

    fun plainText() = formattedText.drop(style.numFormattingChars)

    fun ensureTrimmed() {
        // TODO: We shouldn't mutate formattedText here, because this is used
        // conditionally based on the position this text drawable _happens_ to
        // be rendered in its parent ParagraphDrawable. This may change if the
        // MarkdownComponent re-layouts, which can happen at any time.

        val styleChars = style.numFormattingChars
        formattedText = formattedText.substring(0, styleChars) +
            formattedText.substring(styleChars, formattedText.length).trimStart()
    }

    fun width() = formattedText.width(scaleModifier)

    // Returns null if this drawable cannot be split in a way that doesn't
    // break a word. This means that the drawable should just be drawn on
    // the next line
    fun split(maxWidth: Float, breakWords: Boolean = false): Pair<TextDrawable, TextDrawable>? {
        val styleChars = style.numFormattingChars
        val plainText = plainText()

        var splitPoint = formattedText.indices.drop(styleChars).firstOrNull {
            formattedText.substring(0, it + 1).width(scaleModifier) > maxWidth
        }

        if (splitPoint == null)
            throw IllegalStateException("TextDrawable#split called when it should not have been called")

        splitPoint = splitPoint - 1 - styleChars

        if (!breakWords) {
            while (splitPoint > styleChars && formattedText[splitPoint - 1] != ' ')
                splitPoint--

            if (splitPoint == styleChars)
                return null
        }

        val first = TextDrawable(config, plainText.substring(0, splitPoint), style)
        val second = TextDrawable(config, plainText.substring(splitPoint, plainText.length), style)

        first.linkedTexts.add(second)
        second.linkedTexts.add(first)
        first.scaleModifier = scaleModifier
        second.scaleModifier = scaleModifier

        return first to second
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        return Layout(x, y, width, 9f * scaleModifier)
    }

    fun beforeDraw(state: DrawState) {
        texts.clear()

        if (selectionStart == -1 && selectionEnd == -1) {
            texts.add(Text(formattedText, x, y, false))
        } else if (selectionStart == -1 || selectionEnd == -1) {
            throw IllegalStateException()
        } else {
            val start = selectionStart + style.numFormattingChars
            val end = selectionEnd + style.numFormattingChars

            val formatChars = formattedText.substring(0, style.numFormattingChars)

            val nextX = if (selectionStart > 0) {
                texts.add(Text(
                    formattedText.substring(0, start),
                    x,
                    y,
                    false
                ))
                x + formattedText.substring(0, start).width(scaleModifier)
            } else x

            val selectedString = formatChars + formattedText.substring(start, end)
            texts.add(Text(
                selectedString,
                nextX,
                y,
                true
            ))

            if (end < formattedText.length) {
                texts.add(Text(
                    formatChars + formattedText.substring(end),
                    nextX + selectedString.width(scaleModifier),
                    y,
                    false
                ))
            }
        }

        val mouseX = UMouse.getScaledX() - state.xShift
        val mouseY = UResolution.scaledHeight - UMouse.getScaledY() - state.yShift
        hovered = if (style.linkLocation != null) {
            isHovered(mouseX.toFloat(), mouseY.toFloat())
        } else false
    }

    override fun draw(state: DrawState) {
        val hovered = this.hovered || linkedTexts.any { it.hovered }

        texts.forEach {
            UGraphics.scale(scaleModifier, scaleModifier, 1f)
            drawString(
                config,
                it.string,
                (it.x + state.xShift) / scaleModifier,
                (it.y + state.yShift) / scaleModifier,
                it.selected,
                style.linkLocation != null,
                hovered
            )
            UGraphics.scale(1f / scaleModifier, 1f / scaleModifier, 1f)
        }
    }

    data class Text(
        val string: String,
        val x: Float,
        val y: Float,
        val selected: Boolean
    )

    // TextDrawable mouse selection is managed by ParagraphDrawable#select
    override fun cursorAt(mouseX: Float, mouseY: Float, dragged: Boolean) = throw IllegalStateException("never called")

    override fun cursorAtStart() = TextCursor(this, 0)
    override fun cursorAtEnd() = TextCursor(this, plainText().length)

    override fun selectedText(asMarkdown: Boolean): String {
        if (selectionStart == -1 || selectionEnd == -1)
            return ""

        val selectedText = plainText().substring(selectionStart, selectionEnd)
        if (!asMarkdown)
            return selectedText

        return "${style.markdownSymbols}$selectedText${style.markdownSymbols}"
    }

    override fun toString() = formattedText

    data class Style(
        val isBold: Boolean,
        val isItalic: Boolean,
        val isStrikethrough: Boolean,
        val isUnderline: Boolean,
        val linkLocation: String?
    ) {
        val formattingSymbols = buildString {
            if (isBold)
                append("§l")
            if (isItalic)
                append("§o")
            if (isStrikethrough)
                append("§m")
            if (isUnderline)
                append("§n")
        }

        val markdownSymbols = buildString {
            if (isBold)
                append("**")
            if (isItalic)
                append("*")
            if (isStrikethrough)
                append("~~")
            if (isUnderline)
                append("++")
        }

        val numFormattingChars: Int get() = formattingSymbols.length

        companion object {
            val EMPTY = Style(
                isBold = false,
                isItalic = false,
                isStrikethrough = false,
                isUnderline = false,
                linkLocation = null
            )
        }
    }

    companion object {
        fun drawString(
            config: MarkdownConfig,
            string: String,
            x: Float,
            y: Float,
            selected: Boolean = false,
            isLink: Boolean = false,
            isHovered: Boolean = false
        ) {
            if (selected) {
                UIBlock.drawBlockSized(
                    config.textConfig.selectionBackgroundColor,
                    x.toDouble(),
                    y.toDouble(),
                    string.width().toDouble(),
                    9.0
                )
            }

            val foregroundColor = when {
                isLink -> config.textConfig.linkColor.rgb
                selected -> config.textConfig.selectionForegroundColor.rgb
                else -> config.textConfig.color.rgb
            }

            if (config.textConfig.hasShadow) {
                UGraphics.drawString(
                    string,
                    x,
                    y,
                    foregroundColor,
                    config.textConfig.shadowColor.rgb
                )
            } else {
                UGraphics.drawString(
                    string,
                    x,
                    y,
                    foregroundColor,
                    false
                )
            }

            if (isLink && isHovered) {
                UIBlock.drawBlockSized(
                    config.textConfig.linkColor,
                    x.toDouble(),
                    y.toDouble() + 8,
                    string.width().toDouble(),
                    1.0
                )
            }
        }
    }
}
