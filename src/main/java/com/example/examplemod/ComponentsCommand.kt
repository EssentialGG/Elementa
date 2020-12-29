package com.example.examplemod

//#if MC<=11202
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer

class ComponentsCommand : CommandBase() {
    //#if MC<=10809
    override fun getCommandName() = "components"

    override fun getCommandUsage(sender: ICommandSender?) = "/example - open example gui"

    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
       ExampleMod.gui = ComponentsGui()
    }
    //#else
    //$$ override fun getName() = "components"
    //$$
    //$$ override fun getUsage(sender: ICommandSender) = "/example - open example gui"
    //$$
    //$$ override fun getRequiredPermissionLevel() = 0
    //$$
    //$$ override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
    //$$     ExampleMod.gui = ComponentsGui()
    //$$ }
    //#endif
}
//#else
//$$ import com.mojang.brigadier.CommandDispatcher
//$$ import com.mojang.brigadier.context.CommandContext
//#if FABRIC
//$$ import net.minecraft.server.command.CommandSource
//$$ import net.minecraft.server.command.ServerCommandSource
//$$ import net.minecraft.server.command.CommandManager
//$$
//$$ object ComponentsCommand {
//$$     fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
//$$         dispatcher.register(CommandManager.literal("components")
//#else
//$$ import net.minecraft.command.CommandSource
//$$ import net.minecraft.command.Commands
//$$
//$$ object ComponentsCommand {
//$$     fun register(dispatcher: CommandDispatcher<CommandSource?>) {
//$$         dispatcher.register(Commands.literal("components")
//#endif
//$$             .requires { it.hasPermissionLevel(0) }
//$$             .executes {
//$$                 ExampleMod.gui = ComponentsGui()
//$$                 1
//$$             })
//$$     }
//$$ }
//#endif
