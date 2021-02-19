package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.drawables.*
import org.commonmark.node.*
import org.commonmark.parser.Parser

fun main() {
    val markdown = """
        > this is *some* _formatted
         **text** with_ some extra ***shit***
    """.trimIndent()

    val drawables = MarkdownRenderer(markdown, MarkdownConfig()).render()
    println()
}

class MarkdownRenderer(private val text: String, private val config: MarkdownConfig) : AbstractVisitor() {
    private val drawables = mutableListOf<Drawable>()
    private var isBold = false
    private var isItalic = false

    private val marks = mutableListOf<Int>()

    private fun mark() {
        marks.add(drawables.size)
    }

    fun render(): List<Drawable> {
        val document = Parser.builder().build().parse(text)
        document.accept(this)
        return drawables
    }

    private fun unmarkAndCollect(): List<Drawable> {
        val lastMark = marks.removeAt(marks.lastIndex)
        val slice = drawables.subList(lastMark, drawables.size).toList()
        repeat(slice.size) {
            drawables.removeAt(drawables.lastIndex)
        }
        return slice
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
        drawables.add(TextDrawable(config, text.literal, isBold, isItalic))
    }

    override fun visit(paragraph: Paragraph) {
        mark()
        super.visit(paragraph)
        drawables.add(ParagraphDrawable(config, unmarkAndCollect()))
    }

    override fun visit(blockQuote: BlockQuote?) {
        mark()
        super.visit(blockQuote)
        drawables.add(BlockquoteDrawable(config, unmarkAndCollect()))
    }

    override fun visit(bulletList: BulletList?) {
        TODO("Not yet implemented")
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
        if (children.size != 1 || children[0] !is ParagraphDrawable)
            TODO()
        drawables.add(HeaderDrawable(config, heading.level, children[0] as ParagraphDrawable))
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

    override fun visit(listItem: ListItem?) {
        TODO("Not yet implemented")
    }

    override fun visit(orderedList: OrderedList?) {
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

    override fun visit(customNode: CustomNode?) {
        TODO("Not yet implemented")
    }
}
