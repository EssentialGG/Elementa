package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.markdown.MarkdownState

class BreakElement : Element() {
    override fun draw(state: MarkdownState) {
        if (state.x != state.newlineX)
            state.gotoNextLine()
        state.gotoNextLine()
    }
}