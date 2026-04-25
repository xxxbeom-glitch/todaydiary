package com.todaydiary.app.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode

object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): Modifier.Node = NoIndicationNode

    override fun hashCode(): Int = javaClass.hashCode()

    override fun equals(other: Any?): Boolean = other === this
}

private object NoIndicationNode : Modifier.Node(), DrawModifierNode {
    override fun ContentDrawScope.draw() {
        drawContent()
    }
}

