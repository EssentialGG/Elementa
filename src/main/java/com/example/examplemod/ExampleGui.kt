package com.example.examplemod

import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import java.awt.Color
import java.net.URL
import java.util.*

class ExampleGui : GuiScreen() {
    private val window = Window()

    private val searchInput = UITextInput("Find or start a conversation.", wrapped = false)
    private var messageScreen: MessageScreen? = null

    private val rightHandContainer = UIContainer().constrain {
        x = SiblingConstraint() + 3.pixels()
        width = FillConstraint()
        height = FillConstraint()
    }

    init {
        window.onMouseClick { mouseX, mouseY, mouseButton ->
            searchInput.active = false
            messageScreen?.deactivateMessageInput()
//            println("$mouseX, $mouseY")
        }

        val overallBackgroundBox = UIBlock(TRANSPARENT_BLACK).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            height = RelativeConstraint(.9f)
            width = RelativeConstraint(.9f)
        } childOf window

        val containingWindow = UIContainer().constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = RelativeConstraint(.96f)
            height = RelativeConstraint(.96f)
        } childOf overallBackgroundBox

        val leftHandContainer = UIContainer().constrain {
            width = RelativeConstraint(.25f)
            height = FillConstraint()
        } childOf containingWindow

        val searchBox = UIBlock(DARK_GRAY).constrain {
            width = FillConstraint()
            height = ChildBasedSizeConstraint() + 4.pixels()
        } childOf leftHandContainer

        val innerSearchBox = UIBlock(DARKEST_GRAY).constrain {
            x = 2.pixels()
            y = 2.pixels()
            width = RelativeConstraint(1f) - 4.pixels() as SizeConstraint
            height = 11.pixels()
        }.onMouseClick { _, _, _ ->
            searchInput.active = true
        } childOf searchBox

        // TODO: Make this a UIImage :)
        UIBlock(Color.RED).constrain {
            x = 1.pixels()
            y = 1.pixels()
            width = 9.pixels()
            height = 9.pixels()
        } childOf innerSearchBox

        val searchInputHolder = UIContainer().constrain {
            x = SiblingConstraint() + 2.pixels()
            y = CenterConstraint()
            width = FillConstraint()
            height = ChildBasedSizeConstraint()
        } effect ScissorEffect() childOf innerSearchBox

        searchInput childOf searchInputHolder

        val friendListContainer = UIContainer().constrain {
            y = SiblingConstraint() + 2.pixels()
            width = RelativeConstraint(1f)
            height = FillConstraint()
        } childOf leftHandContainer

        val friendsListTitle = UIBlock(DARK_GRAY).constrain {
            width = FillConstraint()
            height = 12.pixels()
        } childOf friendListContainer

        UIText("Direct Messages and Groups").constrain {
            x = 1.pixels()
            y = CenterConstraint()
        } childOf friendsListTitle effect ScissorEffect(friendListContainer)

        val friendsListScroller = UIContainer().constrain {
            y = SiblingConstraint() + 2.pixels()
            width = RelativeConstraint(1f)
            height = FillConstraint()
        } childOf friendListContainer

        val friendsListScrollList = (ScrollComponent().constrain {
            width = RelativeConstraint(1f) - 7.pixels() as SizeConstraint
            height = FillConstraint()
        } childOf friendsListScroller) as ScrollComponent

        val friendsListScrollBar = UIBlock(MEDIUM_GRAY).constrain {
            x = 0.pixels(alignOpposite = true)
            width = 6.pixels()
            height = FillConstraint()
        } childOf friendsListScroller

        val friendsListScrollBarContainer = UIContainer().constrain {
            y = 2.pixels()
            width = RelativeConstraint(1f)
            height = RelativeConstraint(1f) - 4.pixels()
        } childOf friendsListScrollBar

        val friendsListScrollBarGrip = UIBlock(LIGHTEST_GRAY).constrain {
            x = CenterConstraint()
            y = 3.pixels()
            width = RelativeConstraint(1f) - 2.pixels() as SizeConstraint
            height = 30.pixels()
        } childOf friendsListScrollBarContainer

//        val groups = ModCore.getInstance().messageHandler.groups
//        val dms = groups.filter { it.value.type == PrivateMessageType.SINGLE_USER }
//
//        dms.forEach { (uuid, group) ->
//            val otherPerson = group.participants.first { it != UUIDUtil.getClientUUID() }
//            FriendPreview(
//                group.name,
//                group.messages.lastOrNull()?.message ?: "New Chat",
//                "https://visage.surgeplay.com/face/32/${otherPerson.toString().replace("-", "")}",
//                uuid,
//                this
//            ) childOf friendsListScrollList
//        }

        FriendPreview(
            "Vek",
            "Hey man did you see what Sk1er did?",
            "https://visage.surgeplay.com/face/32/b80a30a6d6d7472490c0c6081684b769",
            UUID.randomUUID(),
            this
        ) childOf friendsListScrollList

        repeat(10) {
            FriendPreview(
                "M0F",
                "Thanks for letting me borrow your TESLA!",
                "https://visage.surgeplay.com/face/32/b80a30a6d6d7472490c0c6081684b769",
                UUID.randomUUID(),
                this
            ) childOf friendsListScrollList
        }

        friendsListScrollList.setScrollBarComponent(friendsListScrollBarGrip)

        searchInput.onUpdate { text ->
            friendsListScrollList.filterChildren {
                it is FriendPreview && it.name.contains(text, ignoreCase = true)
            }
        }

        rightHandContainer childOf containingWindow
    }

    fun openGroup(groupID: UUID) {
//        ModCore.getInstance().messageHandler.requestInfo(groupID)
//        val group = ModCore.getInstance().messageHandler.groups[groupID]

        var msgScreen = messageScreen

        if (msgScreen != null) {
            rightHandContainer.removeChild(msgScreen)
        }

        // TODO: Message screens need to be lazy-loaded
        msgScreen = MessageScreen(
            listOf("..."),
//            group?.participants?.filter { it != UUIDUtil.getClientUUID() }?.map {
//                UUIDUtil.getName(
//                    it
//                ).join()
//            } ?: listOf("..."),
            groupID
        )

        msgScreen childOf rightHandContainer
        messageScreen = msgScreen
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        drawDefaultBackground()

        try {
            window.draw()
        } catch (e: Exception) {
            // Print the error and close the gui rather than hard crashing.
            e.printStackTrace()
            Minecraft.getMinecraft().displayGuiScreen(null)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        window.mouseClick(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        window.mouseRelease()
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        window.mouseDrag(mouseX, mouseY, clickedMouseButton)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val delta = Mouse.getEventDWheel().coerceIn(-1, 1)
        window.mouseScroll(delta)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1) {
            when {
                searchInput.active -> {
                    searchInput.active = false
                }
                messageScreen?.isInputActive() ?: false -> {
                    messageScreen?.deactivateMessageInput()
                }
                else -> {
                    super.keyTyped(typedChar, keyCode)
                }
            }
        } else {
            super.keyTyped(typedChar, keyCode)
        }

        window.keyType(typedChar, keyCode)
    }

    class FriendPreview(val name: String, previewMessage: String, imageURL: String, groupID: UUID, gui: ExampleGui) :
        UIBlock(TRANSPARENT_MEDIUM_GRAY) {
        private val messagePreviewText: UIWrappedText

        init {
            setY(SiblingConstraint() + 1.pixels())
            setWidth(RelativeConstraint(1f))
            setHeight(RelativeConstraint(.095f)) // ~10% of the list, meaning we can fit ~10 previews on the screen at one time.
            enableEffect(ScissorEffect())

            onMouseClick { _, _, _ ->
                gui.openGroup(groupID)
            }

            UIImage.ofURL(URL(imageURL)).constrain {
                x = 1.pixels()
                y = CenterConstraint()
                width = AspectConstraint()
                height = RelativeConstraint(1f) - 2.pixels() as SizeConstraint
            } childOf this

            val textContainer = UIContainer().constrain {
                x = SiblingConstraint() + 4.pixels()
                y = 1.pixels()
                width = FillConstraint() - 2.pixels()
                height = FillConstraint() - 1.pixels()
            } childOf this effect ScissorEffect()

            UIText(name).constrain {
                width = TextAspectConstraint()
                height = 9.pixels().max(RelativeConstraint(.35f) as SizeConstraint)
            } childOf textContainer

            // TODO: This text can obviously overflow, figure out the best way to handle this case.
            //  In addition, we need to appropriately scale the text on smaller screens :)
            messagePreviewText = (UIWrappedText(previewMessage).constrain {
                textScale = PixelConstraint(.85f)
                y = SiblingConstraint() + 3.pixels()
                width = RelativeConstraint(1f)
            } childOf textContainer) as UIWrappedText
        }

        fun updatePreviewMessage(newText: String) {
            messagePreviewText.setText(newText)
        }
    }

    class MessageScreen(participants: List<String>, private val groupID: UUID) : UIContainer() {
        private val titleBarParticipants: UIText

        private val messageInput = UITextInput("Type your message here...").constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = RelativeConstraint(1f) - 8.pixels()
        } as UITextInput

        init {
            setWidth(FillConstraint())
            setHeight(FillConstraint())

            val titleBar = UIBlock(DARKEST_GRAY).constrain {
                width = RelativeConstraint(1f)
                height = 13.pixels()
            } childOf this

            titleBarParticipants = (UIText(participants.joinToString()).constrain {
                x = 2.pixels()
                y = CenterConstraint()
                height = 7.pixels()
                width = TextAspectConstraint()
            } childOf titleBar) as UIText

            val titleBarAddPeople = UIBlock(MEDIUM_GRAY).constrain {
                x = 6.pixels(alignOpposite = true)
                y = CenterConstraint()
                width = ChildBasedSizeConstraint()
                height = RelativeConstraint(1f) - 2.pixels() as SizeConstraint
            } childOf titleBar

            val addPeopleText = UIText("Add people to group").constrain {
                x = 2.pixels()
                y = CenterConstraint()
                height = 7.pixels()
                width = TextAspectConstraint()
            } childOf titleBarAddPeople

            // TODO: Make this actually do something obviously...
            val addPeoplePlusButton = UIBlock(DARK_GRAY).constrain {
                x = SiblingConstraint() + 2.pixels()
                width = ChildBasedSizeConstraint() + 4.pixels()
                height = RelativeConstraint(1f)
            } childOf titleBarAddPeople

            val addPeoplePlusText = UIText("+").constrain {
                x = 2.pixels()
                y = CenterConstraint()
            } childOf addPeoplePlusButton

            val mainMessageWindow = UIBlock(TRANSPARENT_DARK_GRAY).constrain {
                y = SiblingConstraint()
                width = RelativeConstraint(1f)
                height = RelativeConstraint(0.85f)
            } childOf this

            val messagesScroller = UIContainer().constrain {
                y = SiblingConstraint() + 2.pixels()
                width = RelativeConstraint(1f)
                height = FillConstraint()
            } childOf mainMessageWindow

            val messagesScrollList = (ScrollComponent(scrollOpposite = true).constrain {
                width = RelativeConstraint(1f) - 7.pixels() as SizeConstraint
                height = FillConstraint()
            } childOf messagesScroller) as ScrollComponent

            val messagesScrollBar = UIBlock(MEDIUM_GRAY).constrain {
                x = 0.pixels(alignOpposite = true)
                width = 6.pixels()
                height = FillConstraint()
            } childOf messagesScroller

            val messagesScrollBarContainer = UIContainer().constrain {
                y = 2.pixels()
                width = RelativeConstraint(1f)
                height = RelativeConstraint(1f) - 4.pixels()
            } childOf messagesScrollBar

            val messagesScrollBarGrip = UIBlock(LIGHTEST_GRAY).constrain {
                x = CenterConstraint()
                y = 3.pixels()
                width = RelativeConstraint(1f) - 2.pixels() as SizeConstraint
                height = 30.pixels()
            } childOf messagesScrollBarContainer

            messagesScrollList.setScrollBarComponent(messagesScrollBarGrip)

            repeat(200) { i ->
                MessageBox("My message!", i % 2 == 0) childOf messagesScrollList
            }

            val messageSendContainer = UIBlock(DARKEST_GRAY).constrain {
                y = SiblingConstraint()
                height = FillConstraint()
                width = RelativeConstraint(1f)
            } childOf this

            val messageSendBox = UIBlock(DARK_GRAY).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                width = RelativeConstraint(1f) - 4.pixels() as SizeConstraint
                height = RelativeConstraint(1f) - 4.pixels() as SizeConstraint
            }.onMouseClick { _, _, _ ->
                messageInput.active = true
            } childOf messageSendContainer effect ScissorEffect()

            messageInput childOf messageSendBox

            messageInput.onActivate { text ->
//                try {
//                    ModCore.getInstance().messageHandler.sendMessage(groupID, text)
//                } catch (e: NullPointerException) {
//                    ModCore.getInstance().notifications.pushNotification("No group!", "Can't find group $groupID")
//                }
                messageInput.text = ""
            }
        }

        fun isInputActive() = messageInput.active

        fun deactivateMessageInput() {
            messageInput.active = false
        }
    }

    class MessageBox(messageText: String, private val sent: Boolean) : UIContainer() {
        private val containingBox = UIBlock(Color(138, 198, 209)).constrain {
            width = RelativeConstraint(1f) - 3.pixels()
            height = ChildBasedSizeConstraint()
        }

        init {
            constrain {
                x = 10.pixels(sent)
                y = SiblingConstraint(alignOpposite = true) - 2.pixels()
                width = RelativeConstraint(0.4f) + 3.pixels()
                height = ChildBasedSizeConstraint() + 3.pixels()
            }

            containingBox childOf this

            val message = UIWrappedText(messageText).constrain {
                x = 2.pixels()
                y = 2.pixels()
                width = RelativeConstraint(1f) - 2.pixels()
            } childOf containingBox
        }
    }

    companion object {
        val LIGHTEST_GRAY = Color(168, 168, 168)
        val LIGHT_GRAY = Color(101, 101, 101)
        val MEDIUM_GRAY = Color(80, 80, 80)
        val TRANSPARENT_MEDIUM_GRAY = Color(80, 80, 80, 150)
        val DARK_GRAY = Color(64, 64, 64)
        val TRANSPARENT_DARK_GRAY = Color(64, 64, 64, 150)
        val DARKEST_GRAY = Color(34, 34, 34)
        val TRANSPARENT_BLACK = Color(0, 0, 0, 100)
    }
}