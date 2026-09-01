package com.example.melanomatnm.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.melanomatnm.viewmodel.MelanomaViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Button

@Composable
fun MelanomaAppScreen(viewModel: MelanomaViewModel, innerPadding: PaddingValues) {
    //Ascolto del viewmodel/logica di stato
    val stato by viewModel.uiState.collectAsStateWithLifecycle()

    //STATO PER BRESLOW

    var testoBreslow by remember { mutableStateOf(stato.melanomaAnalizzato.spessoreBreslow.toString()) }

    // calcolo dell'errore (numero inserito non valido)
    val valoreBreslowCorretto = testoBreslow.replace(',', '.').toDoubleOrNull()
    val isErrorBreslow =
        testoBreslow.isNotEmpty() &&
                (valoreBreslowCorretto == null) || (valoreBreslowCorretto ?: 0.0) < 0.0

    //STATO PER LINFONODI

    var testoLinfonodi by remember { mutableStateOf(stato.melanomaAnalizzato.numeroLinfonodi.toString()) }

    // calcolo del valore numerico in modo sicuro (può essere null)
    val numeroLinfonodiValido = testoLinfonodi.toIntOrNull()

    // è errore se non è vuoto E (non è un numero O è minore di 0)
    //NON uso!! così se è null non crasha
    val isErrorLinfonodi =
        testoLinfonodi.isNotEmpty() && (numeroLinfonodiValido == null || numeroLinfonodiValido < 0)

    //LOGICA BOTTONE CALCOLA STADIO

    val moduloValido = testoBreslow.isNotEmpty() && !isErrorBreslow &&
            testoLinfonodi.isNotEmpty() && !isErrorLinfonodi

    //layout UI
    Column(
        modifier = Modifier
            .fillMaxSize() // Occupa tutto lo schermo
            .padding(innerPadding) // Rispetta i margini di sistema dello Scaffold
            .padding(16.dp) // Aggiunge 16dp di margine extra ai lati
            .verticalScroll(rememberScrollState()), //permette lo scroll verticale
        horizontalAlignment = Alignment.CenterHorizontally //allinea i componenti orizzontalmente
    ) {

        //TITOLO
        Text(text = "MelanomaTNM", style = MaterialTheme.typography.headlineLarge) //Titolo

        Spacer(modifier = Modifier.height(32.dp)) //spazio vuoto per distanziare dal titolo

        //SOTTOTITOLO 1 (SPESSORE DI BRESLOW)
        Text(
            text = "Spessore di Breslow",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))


        //Campo di testo per spessore di Breslow
        OutlinedTextField(
            //converto il double in stringa per farlo accettare come value della TextField
            value = testoBreslow,

            //aggiorna la scelta locale
            onValueChange = { nuovoValore ->
                //aggiorna il buffer locale
                testoBreslow = nuovoValore

                //sostituisco la virgola col punto per la compatibilità
                val testoConvertito = nuovoValore.replace(',', '.')

                val valoreConvertito = testoConvertito.toDoubleOrNull() ?: 0.0

                viewModel.aggiornaBreslow(valoreConvertito)
            },

            label = { Text("inserisci lo spessore (mm)") },

            isError = isErrorBreslow, // Se true, il bordo e la label diventano rossi

            supportingText = { // Aggiunge il messaggio sotto il campo
                if (isErrorBreslow) {
                    Text(
                        text = "Inserisci un numero valido (es. 1.5)",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            //forza l'apertura del tastierino numerico decimale
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),

            //impedisce di inserire più di una riga
            singleLine = true,

            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        //SOTTOTITOLO 2 (ULCERAZIONE)
        Text(
            text = "Ulcerazione",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        val opzioni = listOf("Assente", "Presente")

        val indiceSelezionatoB = if (stato.melanomaAnalizzato.ulcerazione) 1 else 0

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            //crea entrambe le metà del bottone con un ciclo
            opzioni.forEachIndexed { index, label ->
                SegmentedButton(
                    //arrotonda automaticamente i bordi del bottone per ogni opzione
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = opzioni.size),
                    onClick = {
                        // Aggiorna il viewmodel: index 1 è true (Presente), 0 è false (Assente)
                        viewModel.aggiornaUlcerazione(index == 1)
                    },
                    selected = index == indiceSelezionatoB,
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        //SOTTOTITOLO 3 (LINFONODI)
        Text(
            text = "Linfonodi coinvolti",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = testoLinfonodi,
            onValueChange = { nuovoValore ->
                testoLinfonodi = nuovoValore
                val valoreConvertito = nuovoValore.toIntOrNull() ?: 0
                viewModel.aggiornaLinfonodi(valoreConvertito)
            },

            label = { Text("numero di linfonodi positivi") },

            isError = isErrorLinfonodi,

            supportingText = {
                if (isErrorLinfonodi) {
                    Text(
                        text = "inserisci un numero intero positivo",
                        color = MaterialTheme.colorScheme.error
                    )
                }

            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

            singleLine = true,

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))
        //SOTTOTITOLO 4 (METASTASI)
        Text(
            text = "Metastasi",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))


        val opzioniMetastasi = listOf("Assente", "Presente")
        val indiceSelezionato = if (stato.melanomaAnalizzato.metastasi) 1 else 0

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            //crea entrambe le metà del bottone con un ciclo
            opzioniMetastasi.forEachIndexed { index, label ->
                SegmentedButton(
                    //arrotonda automaticamente i bordi del bottone per ogni opzione
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = opzioniMetastasi.size),
                    onClick = {
                        // Aggiorna il viewmodel: index 1 è true (Presente), 0 è false (Assente)
                        viewModel.aggiornaMetastasi(index == 1)
                    },
                    selected = index == indiceSelezionato,
                    label = { Text(label) }
                )
            }
        }
    }
}