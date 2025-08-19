# Lab-A
Questo è un repository per lo sviluppo del progetto Laboratorio A. Nome dei Integranti: Murilo Faleiros, Matteo Digosciu e Davide Franchi 

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Obiettivo del Progetto

   L'obiettivo di TheKnife è creare una piattaforma simile a TheFork, che permetta agli utenti di trovare e filtrare ristoranti in base a criteri come posizione, tipo di cucina,        fascia di prezzo e servizi disponibili (delivery, prenotazione online).

Il progetto sarà sviluppato in Java e dovrà includere un'interfaccia minima (a terminale o grafica).

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Compiti Principali

  🔹 Gestione dei Dati
  
  Dovranno essere creati file per memorizzare:

    - Ristoranti (nome, posizione, prezzo medio, tipo di cucina, ecc.).

    - Utenti (clienti e ristoratori, con credenziali sicure).

  🔹 Funzionalità dell'Applicazione
  
   Utenti non registrati:

    - Visualizzare le informazioni sui ristoranti.

    - Leggere recensioni in modo anonimo.

    - Creare un account.

   Clienti registrati:

    - Aggiungere ristoranti ai preferiti.

    - Aggiungere, modificare ed eliminare recensioni (da 1 a 5 stelle + commento).

   Ristoratori registrati:

    - Aggiungere i propri ristoranti e le relative caratteristiche.

    - Rispondere alle recensioni (una risposta per ogni recensione).

    - Visualizzare statistiche sulle recensioni.

  🔹 Ricerca e Filtri
  
  Gli utenti potranno cercare i ristoranti in base a:
  
     - Posizione (obbligatorio).
     - Tipo di cucina.
     - Fascia di prezzo.
     - Disponibilità di delivery e prenotazione online.
     -Media delle recensioni.

==================================================================================================================================================================================


  🔹Funzioni per i Ristoratori
  

    ✔ aggiungereRistorante(String nome, String posizione, double prezzoMedio, String tipoCucina, boolean delivery, boolean prenotazioneOnline)

Permette al ristoratore di registrare un nuovo ristorante.


    ✔ modificareRistorante(int ristoranteID, String nome, String posizione, double prezzoMedio, String tipoCucina, boolean delivery, boolean prenotazioneOnline)

Modifica i dati di un ristorante registrato.


    ✔ rimuovereRistorante(int ristoranteID)

Elimina un ristorante dal database.


    ✔ rispondereRecensione(int recensioneID, String risposta)

Permette ai ristoratori di rispondere alle recensioni lasciate dai clienti.


    ✔ visualizzareStatistiche(int ristoranteID) -> Statistiche

Restituisce metriche come media delle recensioni, numero totale di recensioni, recensioni positive/negative, ecc.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Il progetto dovrà includere:

    Codice sorgente in Java, ben documentato con Javadoc.

    Due manuali: uno tecnico e uno per l'utente.

    Un file eseguibile .jar.

    Struttura delle cartelle organizzata: /src, /bin, /data, /doc, /lib.

    README.txt con istruzioni per l'installazione e l'utilizzo.

  Criteri di Valutazione
  
     Corretta implementazione delle funzionalità.
     
     Gestione degli errori ed eccezioni.
     
     Uso efficiente di file e strutturazione del codice.
     
     Navigabilità e usabilità dell'interfaccia.
     
     Qualità della documentazione.

