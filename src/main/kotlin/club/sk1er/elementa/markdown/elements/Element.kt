package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.markdown.MarkdownState

abstract class Element {
    abstract fun draw(state: MarkdownState)
}