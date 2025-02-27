package gg.essential.elementa.utils

import gg.essential.elementa.dsl.width
import gg.essential.elementa.font.DefaultFonts
import gg.essential.elementa.font.FontProvider
import gg.essential.universal.ChatColor
import gg.essential.universal.UGraphics

fun splitStringToWidthTruncated(
    text: String,
    maxLineWidth: Float,
    textScale: Float,
    maxLines: Int,
    ensureSpaceAtEndOfLines: Boolean = true,
    processColorCodes: Boolean = true,
    fontProvider: FontProvider = DefaultFonts.VANILLA_FONT_RENDERER,
    trimmedTextSuffix: String = "..."
): List<String> {
    val lines = getStringSplitToWidth(text, maxLineWidth, textScale, ensureSpaceAtEndOfLines, processColorCodes,fontProvider)
    if (lines.size <= maxLines)
        return lines

    return lines.subList(0, maxLines).mapIndexed { index, contents ->
        if (index == maxLines - 1) {
            var length = contents.length
            while (length > 0 && (contents.substring(0, length) + trimmedTextSuffix).width(textScale,fontProvider) > maxLineWidth)
                length--
            contents.substring(0, length) + trimmedTextSuffix
        } else contents
    }
}

@Deprecated(
    "Does not properly take suffix and text combined width into account and multiplies maxLineWidth by textScale unnecessarily.",
    ReplaceWith("splitStringToWidthTruncated()"),
    DeprecationLevel.WARNING,
)
fun getStringSplitToWidthTruncated(
    text: String,
    maxLineWidth: Float,
    textScale: Float,
    maxLines: Int,
    ensureSpaceAtEndOfLines: Boolean = true,
    processColorCodes: Boolean = true,
    fontProvider: FontProvider = DefaultFonts.VANILLA_FONT_RENDERER,
    trimmedTextSuffix: String = "..."
): List<String> {
    val lines = getStringSplitToWidth(text, maxLineWidth, textScale, ensureSpaceAtEndOfLines, processColorCodes,fontProvider)
    if (lines.size <= maxLines)
        return lines

    val suffixWidth = trimmedTextSuffix.width(textScale,fontProvider)

    return lines.subList(0, maxLines).mapIndexed { index, contents ->
        if (index == maxLines - 1) {
            var length = contents.length
            while (length > 0 && contents.substring(0, length).width(textScale,fontProvider) + suffixWidth > maxLineWidth * textScale)
                length--
            contents.substring(0, length) + trimmedTextSuffix
        } else contents
    }
}

fun getStringSplitToWidth(
    text: String,
    maxLineWidth: Float,
    textScale: Float,
    ensureSpaceAtEndOfLines: Boolean = true,
    processColorCodes: Boolean = true,
    fontProvider: FontProvider = DefaultFonts.VANILLA_FONT_RENDERER
): List<String> {
    val spaceWidth = ' '.width(textScale)
    val maxLineWidthSpace = maxLineWidth - if (ensureSpaceAtEndOfLines) spaceWidth else 0f
    val lineList = mutableListOf<String>()
    val currLine = StringBuilder()
    var textPos = 0
    var currChatColor: ChatColor? = null
    var currChatFormatting: ChatColor? = null

    fun pushLine() {
        lineList.add(currLine.toString())
        currLine.clear()
        if (processColorCodes) {
            currChatColor?.also { currLine.append("§${it.char}") }
            currChatFormatting?.also { currLine.append("§${it.char}") }
        }
    }

    while (textPos < text.length) {
        val builder = StringBuilder()

        while (textPos < text.length && text[textPos].let { it != ' ' && it != '\n'}) {
            val ch = text[textPos]
            if (processColorCodes && (ch == '§' || ch == '&') && textPos + 1 < text.length) {
                val colorCh = text[textPos + 1]
                val nextColor = ChatColor.values().firstOrNull { it.char == colorCh }
                if (nextColor != null) {
                    builder.append('§')
                    builder.append(colorCh)

                    if (nextColor.isFormat) {
                        currChatFormatting = nextColor
                    } else {
                        currChatColor = nextColor
                    }

                    textPos += 2
                    continue
                }
            }

            builder.append(ch)
            textPos++
        }

        val newline = textPos < text.length && text[textPos] == '\n'
        val word = builder.toString()
        val wordWidth = word.width(textScale, fontProvider)

        if (processColorCodes && newline) {
            currChatColor = null
            currChatFormatting = null
        }

        if ((currLine.toString() + word).width(textScale, fontProvider) > maxLineWidthSpace) {
            if (wordWidth > maxLineWidthSpace) {
                // Split up the word into it's own lines
                if (currLine.toString().width(textScale, fontProvider) > 0)
                    pushLine()

                for (char in word.toCharArray()) {
                    if (currLine.isNotEmpty() && (currLine.toString() + char).width(textScale, fontProvider) > maxLineWidthSpace)
                        pushLine()
                    currLine.append(char)
                }
            } else {
                pushLine()
                currLine.append(word)
            }

            // Check if we have a space, and if so, append it to the new line
            if (textPos < text.length) {
                if (!newline) {
                    if (currLine.toString().width(textScale, fontProvider) + spaceWidth > maxLineWidthSpace)
                        pushLine()
                    currLine.append(' ')
                    textPos++
                } else {
                    pushLine()
                    textPos++
                }
            }
        } else {
            currLine.append(word)

            // Check if we have a space, and if so, append it to a line
            if (!newline && textPos < text.length) {
                textPos++
                currLine.append(' ')
            } else if (newline) {
                pushLine()
                textPos++
            }
        }
    }

    lineList.add(currLine.toString())

    return lineList
}

fun sizeStringToWidth(string: String, width: Float): Int {
    val i = string.length
    var j = 0f
    var k = 0
    var l = -1

    var flag = false
    while (k < i) {
        val c0: Char = string[k]

        when (c0) {
            '\n' -> k--
            ' ' -> {
                l = k
                j += UGraphics.getCharWidth(c0)

                if (flag) j++
            }
            '\u00a7' -> if (k < i - 1) {
                k++
                val c1 = string[k]
                if (c1.code != 108 && c1.code != 76) {
                    if (c1.code == 114 || c1.code == 82 || isFormatColor(c1.code)) {
                        flag = false
                    }
                } else {
                    flag = true
                }
            }
            else -> {
                j += UGraphics.getCharWidth(c0)

                if (flag) j++
            }
        }

        if (c0.code == 10) {
            k++
            l = k
            break
        }

        if (j > width) break

        k++
    }

    return if (k != i && l != -1 && l < k) l else k
}

fun isFormatColor(char: Int) = char in 48..57 || char in 97..102 || char in 65..70
