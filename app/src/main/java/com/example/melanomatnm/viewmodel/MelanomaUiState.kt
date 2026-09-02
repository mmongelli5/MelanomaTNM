package com.example.melanomatnm.viewmodel

import com.example.melanomatnm.model.DatiMelanoma

/**
 * Rappresenta lo stato dell'interfaccia utente
 * @property melanomaAnalizzato: rappresenta i valori iniziali per ogni parametro della TNM
 * @property risultatoTNM: rappresenta il risultato della classificazione
 */
data class MelanomaUiState(
    //creo uno stato iniziale vuoto per facilitare la scrittura del ViewModel
    val melanomaAnalizzato: DatiMelanoma= DatiMelanoma(spessoreBreslow = 0.0, ulcerazione = false, numeroLinfonodi = 0, metastasi = false),
    val risultatoTNM: String = "",
    val raccomandazioni: String = ""

)
