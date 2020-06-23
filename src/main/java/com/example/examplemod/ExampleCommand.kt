package com.example.examplemod

//#if MC<=11202
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer

class ExampleCommand : CommandBase() {
    //#if MC<=10809
    override fun getCommandName() = "example"

    override fun getCommandUsage(sender: ICommandSender?) = "/example - open example gui"

    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
       ExampleMod.gui = ExampleGui()
    }
    //#else
    //$$ override fun getName() = "example"
    //$$
    //$$ override fun getUsage(sender: ICommandSender) = "/example - open example gui"
    //$$
    //$$ override fun getRequiredPermissionLevel() = 0
    //$$
    //$$ override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
    //$$     ExampleMod.gui = ExampleGui()
    //$$ }
    //#endif

}
//#else
//$$ import com.mojang.brigadier.CommandDispatcher
//$$ import net.minecraft.command.CommandSource
//$$ import net.minecraft.command.Commands
//$$ import com.mojang.brigadier.context.CommandContext
//$$ class ExampleCommand {
//$$  fun register(dispatcher: CommandDispatcher<CommandSource?>) {
//$$      dispatcher.register(Commands.literal("example").requires { usr: CommandSource -> usr.hasPermissionLevel(0) }.executes { usr: CommandContext<CommandSource> ->
//$$          ExampleMod.gui = ExampleGui()
//$$          1
//$$      })
//$$  }
//$$ }
//#endif