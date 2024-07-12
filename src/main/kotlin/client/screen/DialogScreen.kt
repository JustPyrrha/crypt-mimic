package gay.pyrrha.mimic.client.screen

import gay.pyrrha.mimic.client.ModColours
import gay.pyrrha.mimic.client.entity.ClientNPCEntity
import gay.pyrrha.mimic.client.screen.widget.MimicButtonWidget
import gay.pyrrha.mimic.dialog.DialogFrame
import gay.pyrrha.mimic.entity.ServerNPCEntity
import gay.pyrrha.mimic.ident
import gay.pyrrha.mimic.net.payload.c2s.DialogActionPayload
import gay.pyrrha.mimic.npc.Npc
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.text.Text
import kotlin.math.max

public class DialogScreen(private val frame: DialogFrame, private val npc: Npc, private val entity: ClientNPCEntity) :
    Screen(frame.text) {
    init {
        shouldHideHud = true
    }

    override fun init() {
        val dialogWidth = (width / 3) * 2
        val dialogHeight = (height / 4)
        val x = width / 2 - dialogWidth / 2
        val y = height - (dialogHeight + 20)

        addDrawableChild(MimicButtonWidget.builder(
            Text.translatable("ui.crypt-mimic.theme", ModColours.active.name),
            { btn ->
                ModColours.switch()
                btn.message = Text.translatable("ui.crypt-mimic.theme", ModColours.active.name)
            }) {
            pos(2, 2)
            width(98)
        })

        frame.actions.forEachIndexed { index, action ->
            addDrawableChild(MimicButtonWidget.builder(
                Text.translatable(action.action.toTranslationKey("action")),
                {
                    if (action.action == ident("close")) {
                        close()
                    } else {
                        ClientPlayNetworking.send(DialogActionPayload(action.action, entity.id, action.value))
                    }
                }) {
                pos(
                    x + textRenderer.fontHeight + (98 + 4) * index,
                    y + dialogHeight - (textRenderer.fontHeight / 2) - 4
                )
                width(98)
            })
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)
        val dialogWidth = (width / 3) * 2
        val dialogHeight = (height / 4)
        val x = width / 2 - dialogWidth / 2
        val y = height - (dialogHeight + 20)
        renderNpc(context, x + 10, y, mouseX, mouseY)
        context.fill(x, y, x + dialogWidth, y + dialogHeight, ModColours.active.surface0)
        context.drawBorder(x, y, dialogWidth, dialogHeight, ModColours.active.lavender)
        renderNamePlate(context, x + 10, y)

        val text = textRenderer.wrapLines(frame.text, dialogWidth - textRenderer.fontHeight * 2)
        text.forEachIndexed { index, txt ->
            context.drawText(
                textRenderer,
                txt,
                x + textRenderer.fontHeight,
                y + textRenderer.fontHeight * 2 + (textRenderer.fontHeight * index),
                ModColours.active.text,
                false
            )
        }

        this.children().forEach {
            if (it is Drawable) {
                it.render(context, mouseX, mouseY, delta)
            }
        }
    }

    private fun renderNamePlate(context: DrawContext, x: Int, y: Int) {
        val subtitleText = npc.title ?: Text.empty()
        val textWidth = max(max(textRenderer.getWidth(npc.name), textRenderer.getWidth(subtitleText)), 30)
        val margin = 4
        val lines = if (npc.title == null) 1 else 2
        val width = textWidth + margin * 2
        val height = textRenderer.fontHeight * lines + margin * 2
        val yActual = y - height / 2
        context.fill(x, yActual, x + width, yActual + height, ModColours.active.surface2)
        context.drawBorder(x, yActual, width, height, ModColours.active.lavender)
        context.drawText(
            textRenderer,
            npc.name.copy().styled { it.withBold(true) },
            x + margin,
            yActual + margin,
            ModColours.active.text,
            false
        )
        if (npc.title != null) {
            context.drawText(
                textRenderer,
                subtitleText,
                x + margin,
                yActual + margin + textRenderer.fontHeight,
                ModColours.active.subtext0,
                false
            )
        }
    }

    private fun renderNpc(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        val subtitleText = npc.title ?: Text.empty()
        val textWidth = max(max(textRenderer.getWidth(npc.name), textRenderer.getWidth(subtitleText)), 30)
        val margin = 4
        val width = textWidth + margin * 2

        InventoryScreen.drawEntity(
            context, x, y - (textRenderer.fontHeight + 95), x + width, y, 30, 0.0625f, mouseX.toFloat(),
            mouseY.toFloat(), entity
        )
    }

    override fun shouldPause(): Boolean = false

    override fun close() {
        super.close()
        shouldHideHud = false
    }

    public companion object {
        public var shouldHideHud: Boolean = false
    }
}
