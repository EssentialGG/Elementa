package com.example.examplemod

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class ExampleCommand : CommandBase() {
    override fun getCommandName() = "example"

    override fun getCommandUsage(sender: ICommandSender?) = "/example - open example gui"

    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        ExampleMod.gui = ExampleGui()
    }
}