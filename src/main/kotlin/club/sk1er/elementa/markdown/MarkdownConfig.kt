package club.sk1er.elementa.markdown

import java.awt.Color

data class MarkdownConfig(
    val headerConfig: HeaderConfig = HeaderConfig(),
    val listConfig: ListConfig = ListConfig(),
    val textConfig: TextConfig = TextConfig(),
    val blockquoteConfig: BlockquoteConfig = BlockquoteConfig()
)

data class HeaderConfig(
    val fontColor: Color = Color.WHITE,
    val level1: HeaderLevelConfig = HeaderLevelConfig(fontColor, 2.0f, 12f, 12f, hasDivider = true),
    val level2: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.66f, 10f, 10f, hasDivider = true),
    val level3: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.33f, 8f, 8f),
    val level4: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.0f, 6f, 6f),
    val level5: HeaderLevelConfig = HeaderLevelConfig(fontColor, 0.7f, 4f, 4f),
    val level6: HeaderLevelConfig = HeaderLevelConfig(Color(155, 155, 155), 0.7f, 1f, 1f)
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
    val fontColor: Color = Color.WHITE,
    val indentation: Float = 10f,
    val elementSpacing: Float = 5f,
    val spaceBeforeText: Float = 4f,
    val spaceBeforeList: Float = 5f,
    val spaceAfterList: Float = 5f
)

data class TextConfig(
    val color: Color = Color.WHITE,
    val shadow: Boolean = true,
    val spaceBetweenLines: Float = 2f
)

data class BlockquoteConfig(
    val spaceBeforeDivider: Float = 3f,
    val spaceAfterDivider: Float = 6f,
    val spaceBeforeBlockquote: Float = 7f,
    val spaceAfterBlockquote: Float = 7f,
    val spaceBetweenLines: Float = 2f,
    val dividerColor: Color = Color(80, 80, 80),
    val dividerWidth: Float = 2f
)
