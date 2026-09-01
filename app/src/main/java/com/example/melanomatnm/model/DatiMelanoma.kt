package com.example.melanomatnm.model

/**
 * Classe per rappresentare i parametri rilevanti per la classificazione del melanoma
 * @property spessoreBreslow: Spessore di Breslow, ovvero la profondità in mm del melanoma
 * @property ulcerazione: Indica se è presente o meno ulcerazione del melanoma
 * @property numeroLinfonodi: Numero di linfonodi regionali in metastasi
 * @property metastasi: Indica se è presente o meno metastasi a distanza
 */
data class DatiMelanoma(
    val spessoreBreslow: Double,
    val ulcerazione: Boolean,
    val numeroLinfonodi: Int,
    val metastasi: Boolean,
)



