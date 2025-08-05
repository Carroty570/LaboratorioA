package com.view;

import org.jline.terminal.Terminal;

import com.controller.UIController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class UIMenu {

    private Terminal terminal;
    private UIController controller;
    private final List<String> opzioniMenu = List.of(
    "Join come ospite",
    "Login",
    "Registrati",
    "Esci"
);
    //Metodo start per il Main
    public void printMenu() {
        printBanner();
        menuInterattivo();
    }

    //Legge in input il numero ASCII corrispondente al tasto premuto per aggiornare la selezione e la visuale.
    private void menuInterattivo() {

        int selezione = 0;
        boolean esci = false;

        InputStreamReader input = new InputStreamReader(System.in);

        menuStatico(opzioniMenu, selezione); // stampa una volta all'inizio

        while (!esci) {
            try {

                int c1 = input.read();
                switch (c1) {
                    case 119: // W
                        selezione = aggiornaFreccina(selezione, -1, opzioniMenu.size());
                        break;
                    case 87: // w
                        selezione = aggiornaFreccina(selezione, -1, opzioniMenu.size());
                        break;
                    case 115: //S
                        selezione = aggiornaFreccina(selezione, +1, opzioniMenu.size());
                        break;
                    case 83: //s
                        selezione = aggiornaFreccina(selezione, +1, opzioniMenu.size());
                        break;
                    case 10: //Invio
                        esci = controller.gestisciScelta(selezione);
                        break;
                    case 13: //Invio
                        esci = controller.gestisciScelta(selezione);
                        break;
                    }

            } catch (Exception e) {
                terminal.writer().println("Errore nella lettura dell'input");
                terminal.flush();
            }
        }
    }

    private void spostaFreccinaVisuale(int vecchiaPos, int nuovaPos) {
        int offsetOrizzontale = 28; // Numero di spazi prima del bordo '│' nel menu
        int colonnaFreccinaInMenu = 2; // Posizione della freccina dopo il bordo

        int colonnaFreccinaAssoluta = offsetOrizzontale + colonnaFreccinaInMenu;

        // Cancella la freccina nella vecchia posizione
        terminal.writer().print("\033[" + (vecchiaPos + offsetRighe) + ";" + colonnaFreccinaAssoluta + "H");
        terminal.writer().print(" ");

        // Stampa la freccina nella nuova posizione
        terminal.writer().print("\033[" + (nuovaPos + offsetRighe) + ";" + colonnaFreccinaAssoluta + "H");
        terminal.writer().print("▶");

        terminal.flush();
    }

    private static final int offsetRighe = 13;
    private int aggiornaFreccina(int selezioneCorrente, int delta, int max) {

        // Calcola la nuova posizione della freccina
        int nuovaSelezione = (selezioneCorrente + delta + max) % max;

        // Utilizzo il metofo spostaFreccina per andarla a stampare nella nuova posizione 
        spostaFreccinaVisuale(selezioneCorrente, nuovaSelezione);
        return nuovaSelezione;
    }

    //Print del banner salvato in resources
    private void printBanner() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("banner.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                terminal.writer().println(line);
            }
            terminal.flush();
        } catch (Exception e) {
            terminal.writer().println("Errore nel caricamento del banner.");
        }
    }

    //Stampa il menu iniziale
    private void menuStatico(List<String> opzioni, int selezione) {
        terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
        terminal.writer().println("                           ┌───────────────────────────────────┐");
        terminal.writer().println("                           │        WELCOME TO THE KNIFE       │");
        terminal.writer().println("                           ├───────────────────────────────────┤");

        for (int i = 0; i < opzioni.size(); i++) {
            String freccia = (i == selezione) ? "▶" : " ";
            terminal.writer().println("                           │ " + freccia + " " + (i + 1) + ". " + opzioni.get(i) + " ".repeat(29 - opzioni.get(i).length()) + "│");
        }

        terminal.writer().println("                           └───────────────────────────────────┘");
        terminal.flush();
    } 
}
