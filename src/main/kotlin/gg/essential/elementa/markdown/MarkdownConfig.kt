package gg.essential.elementa.markdown

import java.awt.Color

data class MarkdownConfig @JvmOverloads constructor(
    val headerConfig: HeaderConfig = HeaderConfig(),
    val listConfig: ListConfig = ListConfig(),
    val paragraphConfig: ParagraphConfig = ParagraphConfig(),
    val textConfig: TextConfig = TextConfig(),
    val blockquoteConfig: BlockquoteConfig = BlockquoteConfig(),
    val inlineCodeConfig: InlineCodeConfig = InlineCodeConfig(),
    val codeBlockConfig: CodeBlockConfig = CodeBlockConfig(),
    val urlConfig: URLConfig = URLConfig()
)

data class HeaderConfig @JvmOverloads constructor(
    val fontColor: Color = Color.WHITE,
    val level1: HeaderLevelConfig = HeaderLevelConfig(fontColor, 2.0f, 12f, 12f, hasDivider = true),
    val level2: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.66f, 10f, 10f, hasDivider = true),
    val level3: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.33f, 8f, 8f),
    val level4: HeaderLevelConfig = HeaderLevelConfig(fontColor, 1.0f, 6f, 6f),
    val level5: HeaderLevelConfig = HeaderLevelConfig(fontColor, 0.7f, 4f, 4f),
    val level6: HeaderLevelConfig = HeaderLevelConfig(Color(155, 155, 155), 0.7f, 4f, 4f),
    val enabled: Boolean = true
)

data class HeaderLevelConfig @JvmOverloads constructor(
    val fontColor: Color =  Color.WHITE,
    val textScale: Float = 1f,
    val verticalSpaceBefore: Float = 0f,
    val verticalSpaceAfter: Float = 5f,
    val hasDivider: Boolean = false,
    val dividerColor: Color = Color(80, 80, 80),
    val dividerWidth: Float = 2f,
    val spaceBeforeDivider: Float = 5f
)

data class ListConfig @JvmOverloads constructor(
    val fontColor: Color = Color.WHITE,
    val indentation: Float = 10f,
    val elementSpacingTight: Float = 5f,
    val elementSpacingLoose: Float = 10f,
    val spaceBeforeText: Float = 4f,
    val spaceBeforeList: Float = 5f,
    val spaceAfterList: Float = 5f,
    val unorderedSymbols: String = "●◯■□",
    val enabled: Boolean = true
)

data class ParagraphConfig @JvmOverloads constructor(
    val spaceBetweenLines: Float = 4f,
    val spaceBefore: Float = 5f,
    val spaceAfter: Float = 5f,
    val softBreakIsNewline: Boolean = false,
    val centered: Boolean = false
)

data class TextConfig @JvmOverloads constructor(
    val color: Color = Color.WHITE,
    val hasShadow: Boolean = true,
    val shadowColor: Color = Color(0x3f, 0x3f, 0x3f),
    val selectionForegroundColor: Color = Color(64, 139, 229),
    val selectionBackgroundColor: Color = Color.WHITE,
    val linkColor: Color = Color(1, 165, 82),
    val underlineHoveredLink: Boolean = true
)

data class InlineCodeConfig @JvmOverloads constructor(
    val fontColor: Color = Color.WHITE,
    val backgroundColor: Color = Color(60, 60, 60, 255),
    val outlineColor: Color = Color(140, 140, 140, 255),
    val outlineWidth: Float = 0.5f,
    val cornerRadius: Float = 3f,
    val horizontalPadding: Float = 0f,
    val verticalPadding: Float = 0f,
    val enabled: Boolean = true
)

data class CodeBlockConfig @JvmOverloads constructor(
    val fontColor: Color = Color.WHITE,
    val backgroundColor: Color = Color(40, 40, 40, 255),
    val outlineColor: Color = Color(120, 120, 120, 255),
    val outlineWidth: Float = 0.5f,
    val cornerRadius: Float = 3f,
    val leftPadding: Float = 5f,
    val topPadding: Float = 5f,
    val rightPadding: Float = 5f,
    val bottomPadding: Float = 5f,
    val topMargin: Float = 10f,
    val bottomMargin: Float = 10f,
    val enabled: Boolean = true
)

data class URLConfig @JvmOverloads constructor(
    val fontColor: Color = Color(6, 217, 210),
    val showBarOnHover: Boolean = true,
    val barColor: Color = Color(6, 217, 210),
    val barWidth: Float = 1f,
    val spaceBeforeBar: Float = 1f,
    val enabled: Boolean = true
)

data class BlockquoteConfig @JvmOverloads constructor(
    val spaceBeforeDivider: Float = 3f,
    val spaceAfterDivider: Float = 12f,
    val spaceBeforeBlockquote: Float = 7f,
    val spaceAfterBlockquote: Float = 7f,
    val dividerPaddingTop: Float = 3f,
    val dividerPaddingBottom: Float = 3f,
    val dividerColor: Color = Color(80, 80, 80),
    val dividerWidth: Float = 2f,
    val enabled: Boolean = true
)
