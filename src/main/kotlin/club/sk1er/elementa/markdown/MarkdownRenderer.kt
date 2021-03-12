package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.drawables.*
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.ins.Ins
import org.commonmark.ext.ins.InsExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser
import java.net.URL

class MarkdownRenderer(private val text: String, private val md: MarkdownComponent) : AbstractVisitor() {
    private val drawables = mutableListOf<Drawable>()
    private val style = MutableStyle()

    private val marks = mutableListOf<Int>()

    private fun mark() {
        marks.add(drawables.size)
    }

    fun render(): DrawableList {
        val document = Parser.builder()
            .extensions(extensions)
            .enabledBlockTypes(setOf(
                Heading::class.java,
                FencedCodeBlock::class.java,
                IndentedCodeBlock::class.java,
                BlockQuote::class.java,
                ListBlock::class.java
            ))
            .build()
            .parse(text)
        document.accept(this)
        return DrawableList(md, drawables)
    }

    private fun unmarkAndCollect(): DrawableList {
        val lastMark = marks.removeAt(marks.lastIndex)
        val slice = drawables.subList(lastMark, drawables.size).toList()
        repeat(slice.size) {
            drawables.removeAt(drawables.lastIndex)
        }
        return DrawableList(md, slice)
    }

    override fun visit(emphasis: Emphasis) {
        style.isItalic = true
        super.visit(emphasis)
        style.isItalic = false
    }

    override fun visit(strongEmphasis: StrongEmphasis) {
        style.isBold = true
        super.visit(strongEmphasis)
        style.isBold = false
    }

    override fun visit(text: Text) {
        if (text.firstChild != null)
            TODO()
        drawables.add(TextDrawable(md, text.literal, style.toTextStyle()))
    }

    override fun visit(paragraph: Paragraph) {
        mark()
        super.visit(paragraph)
        drawables.add(ParagraphDrawable(md, unmarkAndCollect()))
    }

    override fun visit(blockQuote: BlockQuote) {
        mark()
        super.visit(blockQuote)
        drawables.add(BlockquoteDrawable(md, unmarkAndCollect()))
    }

    override fun visit(bulletList: BulletList) {
        mark()
        super.visit(bulletList)
        val children = unmarkAndCollect()
        if (children.any { it !is DrawableList && it !is ListDrawable })
            TODO()

        drawables.add(ListDrawable(
            md,
            children,
            isOrdered = false,
            isLoose = !bulletList.isTight
        ))
    }

    override fun visit(listItem: ListItem) {
        mark()
        super.visit(listItem)
        drawables.add(DrawableList(md, unmarkAndCollect()))
    }

    override fun visit(orderedList: OrderedList) {
        mark()
        super.visit(orderedList)
        val children = unmarkAndCollect()
        if (children.any { it !is DrawableList && it !is ListDrawable })
            TODO()

        drawables.add(ListDrawable(
            md,
            children,
            isOrdered = true,
            isLoose = !orderedList.isTight
        ))
    }

    override fun visit(code: Code) {
        TODO("Not yet implemented")
    }

    override fun visit(fencedCodeBlock: FencedCodeBlock) {
        TODO("Not yet implemented")
    }

    override fun visit(hardLineBreak: HardLineBreak) {
        if (hardLineBreak.firstChild != null)
            TODO()
        drawables.add(HardBreakDrawable(md))
    }

    override fun visit(heading: Heading) {
        mark()
        super.visit(heading)
        val children = unmarkAndCollect()
        drawables.add(HeaderDrawable(md, heading.level, ParagraphDrawable(md, children)))
    }

    override fun visit(thematicBreak: ThematicBreak) {
        TODO("Not yet implemented")
    }

    override fun visit(htmlInline: HtmlInline) {
        // HtmlBlocks are disabled, but HTML tags will still be parsed as inline HTML.
        // This is fine, as if the text inside of the angle brackets contains any
        // formatting (or anything that makes it an invalid HTML tag), it will not
        // be parsed as an HTML tag. If we're here, it is unformatted and we just
        // handle it like raw text.
        drawables.add(TextDrawable(md, htmlInline.literal, style.toTextStyle()))
    }

    override fun visit(image: Image?) {
        TODO("Not yet implemented")
    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock) {
        TODO("Not yet implemented")
    }

    override fun visit(link: Link) {
        style.linkLocation = link.destination
        super.visit(link)
        style.linkLocation = null
    }

    override fun visit(softLineBreak: SoftLineBreak) {
        if (softLineBreak.firstChild != null)
            TODO()
        drawables.add(SoftBreakDrawable(md))
    }

    override fun visit(linkReferenceDefinition: LinkReferenceDefinition) {
        TODO("Not yet implemented")
    }

    override fun visit(customBlock: CustomBlock) {
        TODO("Not yet implemented")
    }

    override fun visit(customNode: CustomNode) {
        when (customNode) {
            is Strikethrough -> {
                style.isStrikethrough = true
                super.visit(customNode)
                style.isStrikethrough = false
            }
            is Ins -> {
                style.isUnderline = true
                super.visit(customNode)
                style.isUnderline = false
            }
            else -> TODO()
        }
    }

    data class MutableStyle(
        var isBold: Boolean = false,
        var isItalic: Boolean = false,
        var isStrikethrough: Boolean = false,
        var isUnderline: Boolean = false,
        var linkLocation: String? = null
    ) {
        fun toTextStyle() = TextDrawable.Style(isBold, isItalic, isStrikethrough, isUnderline, linkLocation)
    }

    companion object {
        private val extensions = listOf(
            StrikethroughExtension.create(),
            InsExtension.create()
        )
    }
}
