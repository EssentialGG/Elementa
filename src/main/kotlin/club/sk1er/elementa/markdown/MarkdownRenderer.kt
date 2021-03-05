package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.drawables.*
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownRenderer(private val text: String, private val config: MarkdownConfig) : AbstractVisitor() {
    private val drawables = mutableListOf<Drawable>()
    private var isBold = false
    private var isItalic = false
    private var isStrikethrough = false

    private val marks = mutableListOf<Int>()

    private fun mark() {
        marks.add(drawables.size)
    }

    fun render(): DrawableList {
        val document = Parser.builder().extensions(extensions).build().parse(text)
        document.accept(this)
        return DrawableList(config, drawables)
    }

    private fun unmarkAndCollect(): DrawableList {
        val lastMark = marks.removeAt(marks.lastIndex)
        val slice = drawables.subList(lastMark, drawables.size).toList()
        repeat(slice.size) {
            drawables.removeAt(drawables.lastIndex)
        }
        return DrawableList(config, slice)
    }

    override fun visit(emphasis: Emphasis) {
        isItalic = true
        super.visit(emphasis)
        isItalic = false
    }

    override fun visit(strongEmphasis: StrongEmphasis) {
        isBold = true
        super.visit(strongEmphasis)
        isBold = false
    }

    override fun visit(text: Text) {
        if (text.firstChild != null)
            TODO()
        drawables.add(TextDrawable(config, text.literal, TextDrawable.Style(isBold, isItalic, isStrikethrough)))
    }

    override fun visit(paragraph: Paragraph) {
        mark()
        super.visit(paragraph)
        drawables.add(ParagraphDrawable(config, unmarkAndCollect()))
    }

    override fun visit(blockQuote: BlockQuote) {
        mark()
        super.visit(blockQuote)
        drawables.add(BlockquoteDrawable(config, unmarkAndCollect()))
    }

    override fun visit(bulletList: BulletList) {
        mark()
        super.visit(bulletList)
        val children = unmarkAndCollect()
        if (children.any { it !is DrawableList && it !is ListDrawable })
            TODO()

        drawables.add(ListDrawable(
            config,
            children,
            isOrdered = false,
            isLoose = !bulletList.isTight
        ))
    }

    override fun visit(listItem: ListItem) {
        mark()
        super.visit(listItem)
        drawables.add(DrawableList(config, unmarkAndCollect()))
    }

    override fun visit(orderedList: OrderedList) {
        mark()
        super.visit(orderedList)
        val children = unmarkAndCollect()
        if (children.any { it !is DrawableList && it !is ListDrawable })
            TODO()

        drawables.add(ListDrawable(
            config,
            children,
            isOrdered = true,
            isLoose = !orderedList.isTight
        ))
    }

    override fun visit(code: Code?) {
        TODO("Not yet implemented")
    }

    override fun visit(fencedCodeBlock: FencedCodeBlock?) {
        TODO("Not yet implemented")
    }

    override fun visit(hardLineBreak: HardLineBreak) {
        if (hardLineBreak.firstChild != null)
            TODO()
        drawables.add(HardBreakDrawable(config))
    }

    override fun visit(heading: Heading) {
        mark()
        super.visit(heading)
        val children = unmarkAndCollect()
        drawables.add(HeaderDrawable(config, heading.level, ParagraphDrawable(config, children)))
    }

    override fun visit(thematicBreak: ThematicBreak?) {
        TODO("Not yet implemented")
    }

    override fun visit(htmlInline: HtmlInline?) {
        TODO("Not yet implemented")
    }

    override fun visit(htmlBlock: HtmlBlock?) {
        TODO("Not yet implemented")
    }

    override fun visit(image: Image?) {
        TODO("Not yet implemented")
    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock?) {
        TODO("Not yet implemented")
    }

    override fun visit(link: Link?) {
        TODO("Not yet implemented")
    }

    override fun visit(softLineBreak: SoftLineBreak) {
        if (softLineBreak.firstChild != null)
            TODO()
        drawables.add(SoftBreakDrawable(config))
    }

    override fun visit(linkReferenceDefinition: LinkReferenceDefinition?) {
        TODO("Not yet implemented")
    }

    override fun visit(customBlock: CustomBlock?) {
        TODO("Not yet implemented")
    }

    override fun visit(customNode: CustomNode) {
        if (customNode is Strikethrough) {
            isStrikethrough = true
            super.visit(customNode)
            isStrikethrough = false

            return
        }

        TODO()
    }

    companion object {
        private val extensions = listOf(StrikethroughExtension.create())
    }
}
