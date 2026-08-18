package com.example.melanomatnm.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * classe che gestisce lo stato dell'interfaccia utente
 *
 */
class MelanomaViewModel: ViewModel() {

    //stato privato, modificabile solo da questa classe
    private val _uiState = MutableStateFlow(MelanomaUiState())

    //stato pubblico di sola lettura usato dall'interfaccia grafica
    val  uiState: StateFlow<MelanomaUiState> = _uiState.asStateFlow()

    //funzioni che aggiornano lo stato
    fun aggiornaBreslow(nuovoSpessore: Double) {
        val datoAttuale= _uiState.value.melanomaAnalizzato
        val datoAggiornato = datoAttuale.copy(spessoreBreslow = nuovoSpessore)
        _uiState.value = _uiState.value.copy(melanomaAnalizzato = datoAggiornato)
    }
}