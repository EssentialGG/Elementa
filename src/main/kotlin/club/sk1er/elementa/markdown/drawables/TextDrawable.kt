package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.markdown.selection.TextCursor
import club.sk1er.mods.core.universal.UGraphics

class TextDrawable(
    config: MarkdownConfig,
    text: String,
    val isBold: Boolean,
    val isItalic: Boolean
) : Drawable(config) {
    // Used by HeaderDrawable
    var scaleModifier = 1f

    var formattedText: String
        private set

    // Used by the selection API to tell this Drawable how it is
    // selected. These do not consider style characters
    var selectionStart = -1
    var selectionEnd = -1

    init {
        formattedText = buildString {
            if (isBold)
                append("§l")
            if (isItalic)
                append("§o")
            append(text)
        }
    }

    fun plainText() = formattedText.drop(styleChars())

    fun ensureTrimmed() {
        // TODO: We shouldn't mutate formattedText here, because this is used
        // conditionally based on the position this text drawable _happens_ to
        // be rendered in its parent ParagraphDrawable. This may change if the
        // MarkdownComponent re-layouts, which can happen at any time.

        val styleChars = styleChars()
        formattedText = formattedText.substring(0, styleChars) +
            formattedText.substring(styleChars, formattedText.length).trimStart()
    }

    fun styleChars() = (if (isBold) 2 else 0) + (if (isItalic) 2 else 0)

    fun width() = formattedText.width(scaleModifier)

    // Returns null if this drawable cannot be split in a way that doesn't
    // break a word. This means that the drawable should just be drawn on
    // the next line
    fun split(maxWidth: Float, breakWords: Boolean = false): Pair<TextDrawable, TextDrawable>? {
        val styleChars = styleChars()
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

        val first = TextDrawable(config, plainText.substring(0, splitPoint), isBold, isItalic)
        val second = TextDrawable(config, plainText.substring(splitPoint, plainText.length), isBold, isItalic)
        first.scaleModifier = scaleModifier
        second.scaleModifier = scaleModifier
        return first to second
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        return Layout(x, y, width, 9f * scaleModifier)
    }

    override fun draw(state: DrawState) {
        if (selectionStart == -1 && selectionEnd == -1) {
            draw(state, listOf(Text(formattedText, x, y, false)))
            return
        }

        if (selectionStart == -1 || selectionEnd == -1)
            throw IllegalStateException()

        val start = selectionStart + styleChars()
        val end = selectionEnd + styleChars()

        val texts = mutableListOf<Text>()
        val formatChars = formattedText.substring(0, styleChars())

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

        draw(state, texts)
    }

    private fun draw(state: DrawState, texts: List<Text>) {
        texts.forEach {
            UGraphics.scale(scaleModifier, scaleModifier, 1f)
            drawString(
                config,
                it.string,
                (it.x + state.xShift) / scaleModifier,
                (it.y + state.yShift) / scaleModifier,
                it.selected
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
    override fun cursorAt(mouseX: Float, mouseY: Float) = throw IllegalStateException("never called")

    override fun cursorAtStart() = TextCursor(this, 0)
    override fun cursorAtEnd() = TextCursor(this, plainText().length)

    override fun selectedText(asMarkdown: Boolean): String {
        if (selectionStart == -1 || selectionEnd == -1)
            return ""

        val selectedText = plainText().substring(selectionStart, selectionEnd)
        if (!asMarkdown)
            return selectedText

        val symbols = (if (isBold) "**" else "") + (if (isItalic) "*" else "")
        return "$symbols$selectedText$symbols"
    }

    override fun toString() = formattedText

    companion object {
        fun drawString(config: MarkdownConfig, string: String, x: Float, y: Float, selected: Boolean) {
            if (selected) {
                UIBlock.drawBlockSized(
                    config.textConfig.selectionBackgroundColor,
                    x.toDouble(),
                    y.toDouble(),
                    string.width().toDouble(),
                    9.0
                )
            }

            val foregroundColor = if (selected) {
                config.textConfig.selectionForegroundColor.rgb
            } else config.textConfig.color.rgb

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
        }
    }
}
