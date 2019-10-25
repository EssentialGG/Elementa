package com.example.examplemod.settings

import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.CramSiblingConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.plus

abstract class SettingObject : UIContainer() {
    var selected = false

    init {
        setX(CenterConstraint())
        setY(CramSiblingConstraint())
        setWidth(RelativeConstraint(0.75f))
        setHeight(ChildBasedSizeConstraint() + 5.pixels())
    }

    open fun animateIn() { selected = true }
    open fun animateOut() { selected = false }
}