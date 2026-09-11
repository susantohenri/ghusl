package com.niatmandiwajib.ghusl.domain.engine

import android.content.Context
import com.google.gson.Gson
import com.niatmandiwajib.ghusl.domain.model.WizardNode

data class DecisionTreeData(
    val version: Int,
    val nodes: List<WizardNode>,
    val disclaimer: Map<String, String>
)

class DecisionTreeEngine(context: Context) {
    private val treeData: DecisionTreeData
    private val nodeMap: Map<String, WizardNode>

    init {
        val json = context.assets.open("decision_tree.json")
            .bufferedReader().use { it.readText() }
        treeData = Gson().fromJson(json, DecisionTreeData::class.java)
        nodeMap = treeData.nodes.associateBy { it.id }
    }

    fun getStartNode(): WizardNode? = nodeMap["start"]

    fun getNode(id: String): WizardNode? = nodeMap[id]

    fun answerYes(currentNode: WizardNode): WizardNode? {
        return currentNode.yesTarget?.let { nodeMap[it] }
    }

    fun answerNo(currentNode: WizardNode): WizardNode? {
        return currentNode.noTarget?.let { nodeMap[it] }
    }

    fun getDisclaimer(language: String): String {
        return treeData.disclaimer[language] ?: treeData.disclaimer["en"] ?: ""
    }
}
