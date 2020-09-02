package club.sk1er.elementa.markdown

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.ScrollComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.font.FontRenderer
import java.awt.Color
import java.util.concurrent.CompletableFuture

/**
 * Component that parses a string as Markdown and renders it.
 *
 * This component's width and height must be non-child-related
 * constraints. This is because the actual text rendering is
 * done with direct render calls instead of through the component
 * hierarchy.
 */
class MarkdownComponent(
    text: String,
    private val config: MarkdownConfig = MarkdownConfig()
) : UIComponent() {
    private val documentFuture = CompletableFuture.supplyAsync {
        Document.fromString(text)
    }
    private var document: Document? = null

    private val scrollComponent = ScrollComponent().constrain {
        width = RelativeConstraint() - 15.pixels()
        height = RelativeConstraint()
    } childOf this

    private val scrollChild = UIContainer().constrain {
        width = RelativeConstraint()
        height = 0.pixels()
    } childOf scrollComponent

    private val scissor = ScissorEffect(scrollComponent)

    init {
        val scrollBar = UIContainer().constrain {
            x = 2.pixels(alignOpposite = true)
            width = 10.pixels()
            height = FillConstraint()
        } childOf this

        val scrollBarContainer = UIContainer().constrain {
            x = CenterConstraint()
            y = 2.pixels()
            width = 3.pixels()
            height = RelativeConstraint() - 4.pixels()
        } childOf scrollBar

        val scrollBarGrip = UIBlock(Color(180, 180, 180, 180)).constrain {
            x = CenterConstraint()
            y = 0.pixels()
            width = RelativeConstraint()
            height = 30.pixels()
        } childOf scrollBarContainer

        scrollComponent.setScrollBarComponent(scrollBarGrip, hideWhenUseless = true)

        onMouseClick { event ->
            document?.onClick(event.absoluteX, event.absoluteY)
        }
    }

    override fun draw() {
        super.draw()

        scissor.beforeDraw(this)

        if (document == null && documentFuture.isDone && !documentFuture.isCompletedExceptionally)
            document = documentFuture.get()

        val state = MarkdownState(
            getLeft(),
            scrollChild.getTop(),
            scrollChild.getWidth(),
            getHeight(),
            config = config
        )

        document?.draw(state)
        scrollChild.setHeight(state.y.pixels())

        scissor.afterDraw(this)
    }

    companion object {
        val codeFontRenderer = FontRenderer(FontRenderer.SupportedFont.Menlo, 18f)
    }
}