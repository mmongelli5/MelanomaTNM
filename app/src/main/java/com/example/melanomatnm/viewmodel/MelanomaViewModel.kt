package com.example.melanomatnm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.melanomatnm.model.CalcolatoreTNM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Classe che gestisce lo stato dell'interfaccia utente
 * @property _uiState rappresenta lo stato dell'interfaccia utente modificabile
 * @property uiState rappresenta lo stato dell'interfaccia utente di sola lettura
 */
class MelanomaViewModel: ViewModel() {

    //stato privato, modificabile solo da questa classe
    private val _uiState = MutableStateFlow(MelanomaUiState())

    //stato pubblico di sola lettura usato dall'interfaccia grafica (
    val  uiState: StateFlow<MelanomaUiState> = _uiState.asStateFlow()

    //funzioni che aggiornano lo stato

    /**
     * Aggiorna il parametro relativo allo spessore di Breslow del melanoma tramite .copy()
     * @param nuovoSpessore rappresenta il nuovo valore del parametro
     */
    fun aggiornaBreslow(nuovoSpessore: Double) {
        val datoAttuale= _uiState.value.melanomaAnalizzato
        val datoAggiornato = datoAttuale.copy(spessoreBreslow = nuovoSpessore)
        _uiState.value = _uiState.value.copy(melanomaAnalizzato = datoAggiornato)
    }

    /**
     * Aggiorna il parametro relativo alla presenza o meno di ulcerazione del melanoma tramite .copy()
     * @param statoUlcerazione rappresenta il nuovo valore del parametro
     */
    fun aggiornaUlcerazione(statoUlcerazione: Boolean) {
        val datoAttuale = _uiState.value.melanomaAnalizzato
        val datoAggiornato = datoAttuale.copy(ulcerazione = statoUlcerazione)
        _uiState.value = _uiState.value.copy(melanomaAnalizzato = datoAggiornato)
    }

    /**
     * Aggiorna il parametro relativo al numero di linfonodi regionali del melanoma tramite .copy()
     * @param nuovoNumeroLinfonodi rappresenta il nuovo valore del parametro
     */

    fun aggiornaLinfonodi(nuovoNumeroLinfonodi: Int) {
        val datoAttuale = _uiState.value.melanomaAnalizzato
        val datoAggiornato = datoAttuale.copy(numeroLinfonodi = nuovoNumeroLinfonodi)
        _uiState.value = _uiState.value.copy(melanomaAnalizzato = datoAggiornato)
    }

    /**
     * Aggiorna il parametro relatico alla presenza o meno di metastasi a distanza del melanoma tramite .copy()
     * @param statoMetastasi rappresenta il nuovo valore del parametro
     */

    fun aggiornaMetastasi(statoMetastasi: Boolean) {
        val datoAttuale = _uiState.value.melanomaAnalizzato
        val datoAggiornato = datoAttuale.copy(metastasi = statoMetastasi)
        _uiState.value = _uiState.value.copy(melanomaAnalizzato = datoAggiornato)

    }

    /**
     * Calcola stadio e profilo TNM del melanoma secondo classificazioneTNM, nonché le raccomandazioni mediche indicate (ispirandosi a NCCN ed ESMO)
     */
    fun calcolaStadioFinale() {
        val calcolatore = CalcolatoreTNM()
        //Prendo i dati attuali dal UiState
        val datiPaziente = _uiState.value.melanomaAnalizzato

        //Chiamo il Model passandogli i dati
        val risultatoCalcolato = calcolatore.calcolaTNM(
            breslow = datiPaziente.spessoreBreslow,
            ulcerazione = datiPaziente.ulcerazione,
            numLinfonodi = datiPaziente.numeroLinfonodi,
            metastasi = datiPaziente.metastasi
        )

        val profiloCalcolato = calcolatore.calcolaProfiloTNM(
            breslow = datiPaziente.spessoreBreslow,
            ulcerazione = datiPaziente.ulcerazione,
            numLinfonodi = datiPaziente.numeroLinfonodi,
            metastasi = datiPaziente.metastasi
        )

        val raccomandazioniOttenute = calcolatore.ottieniRaccomandazione(risultatoCalcolato)


        //Preparo il risultato per la View
        _uiState.value = _uiState.value.copy(
            risultatoTNM = risultatoCalcolato,
            profiloTNM = profiloCalcolato,
            raccomandazioni = raccomandazioniOttenute)
    }

}