package club.sk1er.elementa.markdown

import java.awt.Color

data class MarkdownConfig(
    val headerConfig: HeaderConfig = HeaderConfig(),
    val listConfig: ListConfig = ListConfig(),
    val textConfig: TextConfig = TextConfig()
)

data class HeaderConfig(
    val fontColor: Color = Color.WHITE,
    val level1: HeaderLevelConfig = HeaderLevelConfig(fontColor, 3.0f, 12f, 18f, hasDivider = true),
    val level2: HeaderLevelConfig = HeaderLevelConfig(fontColor, 2.6f, 10f, 15f, hasDivider = true),
    val level3: HeaderLevelConfig = HeaderLevelConfig(fontColor, 2.2f, 8f, 12f),
    val level4: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.8f, 6f, 9f),
    val level5: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.4f, 4f, 6f),
    val level6: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.0f, 2f, 3f)
)

data class HeaderLevelConfig(
    val fontColor: Color,
    val textScale: Float,
    val spaceBefore: Float,
    val spaceAfter: Float,
    val hasDivider: Boolean = false,
    val dividerColor: Color = Color(80, 80, 80),
    val dividerWidth: Float = 2f
)

data class ListConfig(
    val indentation: Float = 2f,
    val elementSpacing: Float = 2f,
    val newLevelPreSpacing: Float = 0f,
    val newLevelPostSpacing: Float = 0f
)

data class TextConfig(
    val color: Color = Color.WHITE,
    val shadow: Boolean = true,
    val spaceBetweenLines: Float = 2f
)
