package com.example.melanomatnm.model

/**
 * Classe che implementa la logica di calcolo dei parametri TNM seguendo le regole AJCC
 */
class CalcolatoreTNM {

    /**
     * Calcola il parametro T della classificazione TNM
     * @param breslow: Spessore di Breslow del melanoma
     * @param ulcerazione: Indica se è presente o meno ulcerazione del melanoma
     */
    fun calcolaT(breslow: Double, ulcerazione: Boolean): String {
        return when {
            //Tis
            breslow == 0.0 -> "Tis" // Melanoma in situ (non ha mai ulcerazione)
            //T1
            breslow < 0.8 && !ulcerazione -> "T1a"
            (breslow < 0.8 && ulcerazione) || (breslow >= 0.8 && breslow <= 1.0) -> "T1b"
            //T2
            breslow > 1.0 && breslow <= 2.0 && !ulcerazione -> "T2a"
            breslow > 1.0 && breslow <= 2.0 && ulcerazione -> "T2b"
            //T3
            breslow > 2.0 && breslow <= 4.0 && !ulcerazione -> "T3a"
            breslow > 2.0 && breslow <= 4.0 && ulcerazione -> "T3b"
            //T4
            breslow > 4.0 && !ulcerazione -> "T4a"
            breslow > 4.0 && ulcerazione -> "T4b"
            //input non valido: spessore di breslow non validabile
            else -> "Tx"
        }
    }

    /**
     * Calcola il parametro N della classificazione TNM
     * @param numLinfonodi: Numero di linfonodi regionali colpiti
     */
    fun calcolaN(numLinfonodi: Int): String {
        return when {
            numLinfonodi == 0 -> "N0"
            numLinfonodi == 1 -> "N1"
            (numLinfonodi == 2) || (numLinfonodi == 3) -> "N2"
            numLinfonodi >= 4 -> "N3"
            //input non valido: numero di linfonodi non valido
            else -> "Nx"
        }
    }

    /**
     * Calcola il parametro M della classificazione TNM
     * @param metastasi: Indica se è presente o meno metastasi a distanza del melanoma
     */
    fun calcolaM(metastasi: Boolean): String {
        return if (metastasi) "M1" else "M0"
    }

    /**
     * Calcola il stadio TNM del melanoma
     * @param breslow: Spessore di Breslow del melanoma
     * @param ulcerazione: Indica se è presente o meno ulcerazione del melanoma
     * @param numLinfonodi: Numero di linfonodi regionali colpiti
     * @param metastasi: Indica se è presente o meno metastasi a distanza
     */
    fun calcolaTNM(
        breslow: Double,
        ulcerazione: Boolean,
        numLinfonodi: Int,
        metastasi: Boolean
    ): String {
        val t = calcolaT(breslow, ulcerazione)
        val n = calcolaN(numLinfonodi)
        val m = calcolaM(metastasi)

        val profiloTNM = "$t$n$m"

        //gestione casi non valutabili
        if (t == "Tx" || n == "Nx") return "$profiloTNM: caso non valutabile"

        return when {
            //caso 0: melanoma in situ
            t == "Tis" && n == "N0" && m == "M0" -> "Stadio 0"

            //caso 1: metastasi sovrasta il resto
            m == "M1" -> "Stadio IV"

            // caso 2: presenza di interessamento linfonodale: sovrasta lo spessore
            n != "N0" -> "Stadio III"

            //caso 3: assenza di linfonodi e metastasi, si usa solo T
            (t == "T1a") || (t == "T1b") -> "Stadio IA"

            (t == "T2a") -> "Stadio IB"

            t == "T2b" || t == "T3a" -> "Stadio IIA"

            t == "T3b" || t == "T4a" -> "Stadio IIB"

            t == "T4b" -> "Stadio IIC"

            else -> "$profiloTNM: Stadio II/III, vedi tabelle AJCC"
        }


    }

    fun ottieniRaccomandazione(stadio: String): String {
        return when {
            stadio == "Stadio 0" ->
                "• Escissione chirurgica completa del melanoma in situ.\n" +
                        "• SLNB non raccomandata.\n" +
                        "• Follow-up dermatologico periodico e autoesame cutaneo."


            stadio == "Stadio IA" ->
                "• Ampia escissione chirurgica (margine generalmente 1 cm).\n" +
                        "• SLNB generalmente non raccomandata.\n" +
                        "• Follow-up clinico/dermatologico periodico e autoesame cutaneo."

            stadio == "Stadio IB" || stadio == "Stadio IIA" ->
                "• Ampia escissione chirurgica (margini 1-2 cm).\n" +
                        "• Valutazione/considerazione della biopsia del linfonodo sentinella (SLNB).\n" +
                        "• Sorveglianza clinica e dermatologica periodica."

            stadio == "Stadio IIB" || stadio == "Stadio IIC" ->
                "• Ampia escissione chirurgica (margine 2 cm) + SLNB.\n" +
                        "• Valutazione per immunoterapia adiuvante anti-PD-1.\n" +
                        "• Follow-up clinico e imaging secondo il rischio individuale."

            stadio.startsWith("Stadio III") ->
                "• Valutazione multidisciplinare per trattamento chirurgico e/o sistemico.\n" +
                        "• Valutazione dello stato mutazionale BRAF.\n" +
                        "• Valutazione della terapia sistemica adiuvante o neoadiuvante secondo il sottostadio e la resecabilità.\n" +
                        "• Follow-up clinico e imaging secondo il rischio individuale."

            stadio.startsWith("Stadio IV") ->
                "• Profilazione molecolare, inclusa la valutazione di BRAF.\n" +
                        "• Terapia sistemica di prima linea (immunoterapia e/o terapia target secondo le caratteristiche della malattia).\n" +
                        "• Valutazione multidisciplinare per eventuali trattamenti locali, chirurgici o radioterapici."

            else ->
                "Consultare le linee guida complete per la stadiazione e il trattamento del melanoma."
        }
    }
}
