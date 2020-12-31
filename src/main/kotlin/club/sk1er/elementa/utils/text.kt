package club.sk1er.elementa.utils

import club.sk1er.elementa.dsl.width
import club.sk1er.mods.core.universal.ChatColor
import club.sk1er.mods.core.universal.UGraphics

fun getStringSplitToWidthTruncated(
    text: String,
    maxLineWidth: Float,
    textScale: Float,
    maxLines: Int,
    ensureSpaceAtEndOfLines: Boolean = true,
    processColorCodes: Boolean = true
): List<String> {
    val lines = getStringSplitToWidth(text, maxLineWidth, textScale, ensureSpaceAtEndOfLines, processColorCodes)
    if (lines.size <= maxLines)
        return lines

    val ellipsisWidth = "...".width(textScale)

    return lines.subList(0, maxLines).mapIndexed { index, contents ->
        if (index == maxLines - 1) {
            var length = contents.lastIndex
            while (contents.substring(0, length).width(textScale) + ellipsisWidth > maxLineWidth * textScale)
                length--
            contents.substring(0, length) + "..."
        } else contents
    }
}

fun getStringSplitToWidth(
    text: String,
    maxLineWidth: Float,
    textScale: Float,
    ensureSpaceAtEndOfLines: Boolean = true,
    processColorCodes: Boolean = true
): List<String> {
    val spaceWidth = ' '.width(textScale)
    val maxLineWidthSpace = maxLineWidth * textScale - if (ensureSpaceAtEndOfLines) spaceWidth else 0f
    val lineList = mutableListOf<String>()
    val currLine = StringBuilder()
    var currLineWidth = 0f
    var textPos = 0
    var currChatColor: ChatColor? = null
    var currChatFormatting: ChatColor? = null

    fun pushLine(newLineWidth: Float = 0f) {
        lineList.add(currLine.toString())
        currLine.clear()
        currLineWidth = newLineWidth
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
        val wordWidth = word.width(textScale)

        if (processColorCodes && newline) {
            currChatColor = ChatColor.WHITE
            currChatFormatting = null
        }

        if (currLineWidth + wordWidth > maxLineWidthSpace) {
            if (wordWidth > maxLineWidthSpace) {
                // Split up the word into it's own lines
                if (currLineWidth > 0)
                    pushLine()

                for (char in word.toCharArray()) {
                    currLineWidth += char.width(textScale)
                    if (currLineWidth > maxLineWidthSpace)
                        pushLine(char.width(textScale))
                    currLine.append(char)
                }
            } else {
                pushLine(wordWidth)
                currLine.append(word)
            }

            // Check if we have a space, and if so, append it to the new line
            if (textPos < text.length) {
                if (!newline) {
                    if (currLineWidth + spaceWidth > maxLineWidthSpace)
                        pushLine()
                    currLine.append(' ')
                    currLineWidth += spaceWidth
                    textPos++
                } else {
                    pushLine()
                    textPos++
                }
            }
        } else {
            currLine.append(word)
            currLineWidth += wordWidth

            // Check if we have a space, and if so, append it to a line
            if (!newline && textPos < text.length) {
                textPos++
                currLine.append(' ')
                currLineWidth += spaceWidth
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
                if (c1.toInt() != 108 && c1.toInt() != 76) {
                    if (c1.toInt() == 114 || c1.toInt() == 82 || isFormatColor(c1.toInt())) {
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

        if (c0.toInt() == 10) {
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
