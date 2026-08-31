package com.example.melanomatnm.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.melanomatnm.ui.theme.MelanomaTNMTheme
import com.example.melanomatnm.viewmodel.MelanomaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abilita il design edge-to-edge (contenuto dietro le barre di sistema)
        enableEdgeToEdge()

        setContent {
            MelanomaTNMTheme {
                // Inizializzazione del ViewModel tramite la libreria lifecycle-viewmodel-compose
                val viewModel: MelanomaViewModel = viewModel()

                //Fornisce la struttura base Material Design
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Passiamo il viewModel e il padding alla schermata
                    MelanomaAppScreen(
                        viewModel = viewModel,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}