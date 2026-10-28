package com.niatmandiwajib.ghusl.domain.engine

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.niatmandiwajib.ghusl.domain.model.WizardNode

@Keep
data class DecisionTreeData(
    val version: Int = 1,
    val nodes: List<WizardNode> = emptyList(),
    val disclaimer: Map<String, String> = emptyMap()
)

class DecisionTreeEngine(context: Context) {
    private val treeData: DecisionTreeData
    private val nodeMap: Map<String, WizardNode>

    init {
        var parsedData: DecisionTreeData? = null
        try {
            val json = context.assets.open("decision_tree.json")
                .bufferedReader().use { it.readText() }
            parsedData = Gson().fromJson(json, DecisionTreeData::class.java)
        } catch (e: Exception) {
            Log.e("DecisionTreeEngine", "Failed to parse decision_tree.json", e)
        }
        treeData = parsedData ?: DecisionTreeData()
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
