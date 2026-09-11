package com.niatmandiwajib.ghusl.domain.model

/**
 * Represents a node in the decision tree for the Cek Kondisi wizard.
 * Data is loaded from assets/decision_tree.json
 * [JANGAN DIISI SENDIRI] - Logika fiqih akan diisi oleh ustadz/tim produk
 */
data class WizardNode(
    val id: String,
    val question: Map<String, String>, // language code -> question text
    val yesTarget: String?,  // node ID for "yes" answer
    val noTarget: String?,   // node ID for "no" answer
    val isResult: Boolean = false,
    val resultTitle: Map<String, String>? = null,
    val resultDescription: Map<String, String>? = null,
    val resultDalil: String? = null,
    val mustGhusl: Boolean? = null
)
