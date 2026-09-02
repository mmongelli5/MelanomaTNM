package com.example.melanomatnm.view

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState


@OptIn(ExperimentalMaterial3Api::class)
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


    //STATO PER PANNELLO DETTAGLI
    // (per controllare se il pannello in è aperto o chiuso)
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    //CONDIZIONI PER RENDERE ATITVI I CAMPI
        //Lo spessore è valido se non è vuoto e non ha errori
    val breslowValido = testoBreslow.isNotEmpty() && !isErrorBreslow

        //È "In Situ" (Tis) se lo spessore inserito è esattamente 0.0
    val isTis = breslowValido && valoreBreslowCorretto == 0.0

        //Gli altri campi si abilitano SOLO SE lo spessore è valido E NON è 0.0
    val altriCampiAbilitati = breslowValido && !isTis

    //LOGICA BOTTONE CALCOLA STADIO

   // Il modulo è valido se:
    //- Se è Tis (0.0 mm) -> basta lo spessore corretto
    // - Se è > 0.0 mm -> servono anche i linfonodi corretti
    val moduloValido = if (isTis) {
        breslowValido
    } else {
        breslowValido && testoLinfonodi.isNotEmpty() && !isErrorLinfonodi
    }

    //layout UI
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize() // Occupa tutto lo schermo
            .padding(innerPadding) // Rispetta i margini di sistema dello Scaffold
            .padding(16.dp) // Aggiunge 16dp di margine extra ai lati
            .verticalScroll(rememberScrollState()), //permette lo scroll verticale
        horizontalAlignment = Alignment.CenterHorizontally //allinea i componenti orizzontalmente
    ) {


        // TITOLO CON ICONA ACCANTO
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically, // Allinea verticalmente icona e testo al centro tra loro
            horizontalArrangement = Arrangement.Center, // Centra il gruppo orizzontalmente
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.MedicalInformation,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp), // Spazio tra icona e testo
                tint = MaterialTheme.colorScheme.primary // Colore dell'icona
            )

            Text(
                text = "MelanomaTNM",
                style = MaterialTheme.typography.displaySmall, // Carattere grande
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
                } else if (isTis) {
                    // Messaggio informativo per melanoma in Situ
                    Text(
                        text = "Per il melanoma in situ (0.0 mm) non sono necessari altri parametri.",
                        color = MaterialTheme.colorScheme.primary
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
                    label = { Text(label) },
                    enabled = altriCampiAbilitati
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

            modifier = Modifier.fillMaxWidth(),

            enabled = altriCampiAbilitati
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
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = opzioniMetastasi.size
                    ),
                    onClick = {
                        // Aggiorna il viewmodel: index 1 è true (Presente), 0 è false (Assente)
                        viewModel.aggiornaMetastasi(index == 1)
                    },
                    selected = index == indiceSelezionato,
                    label = { Text(label) },
                    enabled = altriCampiAbilitati
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // BOTTONE DI CALCOLO
        androidx.compose.material3.Button(
            onClick = { viewModel.calcolaStadioFinale() },
            modifier = Modifier.fillMaxWidth(),
            enabled = moduloValido // Si attiva solo se i dati sono corretti
        ) {
            Text(text = "Calcola Stadio", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // AREA RISULTATO E PANNELLO DETTAGLI
        if (stato.risultatoTNM.isNotEmpty()) {

            //per l'area risultato
            androidx.compose.material3.Card(
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Risultato Classificazione:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = stato.risultatoTNM,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Profilo: ${stato.profiloTNM}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            //per il pannello dettagli
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { showSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Visualizza raccomandazioni cliniche")
            }
        }

    }

    //Pannello dettagli in sovrapposizione (fuori dalla column)
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 8.dp)
            ) {
                Text(
                    text = "Raccomandazioni per ${stato.risultatoTNM}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(text = stato.raccomandazioni, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
