TheKnife — Progetto Java (CLI)
================================

Requisiti
---------
- Java 17+
- Maven 3.9+

Build
-----
1) Da terminale nella cartella del progetto:
   mvn -q -e -DskipTests package

2) Verrà generato: target/TheKnife-1.0.0.jar

Esecuzione
----------
Eseguire dalla root del progetto per consentire l'accesso alla cartella `data/`:
   java -jar target/TheKnife-1.0.0.jar

Dati
----
I file CSV sono in `data/`:
- restaurants.csv
- users.csv
- reviews.csv
- favorites.csv

Funzionalità
------------
- Guest: ricerca e visualizzazione ristoranti e recensioni
- Cliente: preferiti, inserisci/modifica/cancella recensioni
- Ristoratore: inserisci ristorante, riepilogo, risposta alle recensioni

Note
----
- Password salvate con SHA-256.
- CSV con header alla prima riga.
- Per generare JavaDoc: `mvn javadoc:javadoc` (output in `target/site/apidocs`).

Cartelle
--------
- src/main/java/... codice sorgente con package `theknife`
- data/ file CSV
- doc/ manuali (inserire PDF)
- bin/ (vuota, il jar viene prodotto in `target/` da Maven)
- lib/ (per eventuali librerie esterne)
