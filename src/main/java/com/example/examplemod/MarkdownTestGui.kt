package com.example.examplemod

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.markdown.MarkdownComponent

class MarkdownTestGui : WindowScreen(ElementaVersion.V2) {
    init {
        val scrollBox = ScrollComponent().constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf window

        MarkdownComponent(
            "# Markdown Test" +
                    "\n![](https://picsum.photos/800/300)" +
                    "\nBig image" +
                    "\n\n![](https://picsum.photos/225)![](https://picsum.photos/225)![](https://picsum.photos/225) Small images with text that wraps directly after it."
        ).constrain {
            x = CenterConstraint()
            y = 0.pixels()
            width = 800.pixels()
            height = 100.percent()
        } childOf scrollBox
    }
}