package com.niatmandiwajib.ghusl.data.repository

import com.niatmandiwajib.ghusl.domain.engine.DecisionTreeEngine
import com.niatmandiwajib.ghusl.domain.model.WizardNode

class WizardRepository(private val engine: DecisionTreeEngine) {

    fun getStartNode(): WizardNode? = engine.getStartNode()

    fun getNode(id: String): WizardNode? = engine.getNode(id)

    fun answerYes(currentNode: WizardNode): WizardNode? = engine.answerYes(currentNode)

    fun answerNo(currentNode: WizardNode): WizardNode? = engine.answerNo(currentNode)

    fun getDisclaimer(language: String): String = engine.getDisclaimer(language)
}
