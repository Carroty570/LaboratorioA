package com.view;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


import com.controller.UIController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class UIMenu {

    private Terminal terminal;
    private UIController controller;
    LineReader lineReader;
    
    //Inizializzazione terminale
    public UIMenu() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            terminal.enterRawMode();
            lineReader = LineReaderBuilder.builder().terminal(terminal).build();
            controller = new UIController();
        } catch (Exception e) {
            e.printStackTrace();  // ← questo lo stampa?
        }
    }

    private final List<String> opzioniMenu = List.of(
    "Accesso come ospite",
    "Login",
    "Registrati",
    "Esci"
    );

    private final List<String> opzioniLogin = List.of(
    "Login utente",
    "Login ristoratore",
    "Torna al menu principale"
    );

    private final List<String> opzioniReg = List.of(
    "Registrazione utente",
    "Registrazione ristoratore",
    "Torna al menu principale"
    );

    //Legge in input il numero ASCII corrispondente al tasto premuto per aggiornare la selezione e la visuale.
    private MenuTipo menuInterattivo(List<String> opzioni) {
        int selezione = 0;
        InputStream input = System.in;

        terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
        terminal.writer().print("\033[H\033[2J");
        printBanner();
        menuStatico(opzioni, selezione);

        while (true) {
            try {
                int c1 = input.read();
                switch (c1) {
                    case 119, 87: // W
                        selezione = aggiornaFreccina(selezione, -1, opzioni.size());
                        break;
                    case 115, 83: // S
                        selezione = aggiornaFreccina(selezione, +1, opzioni.size());
                        break;
                    case 13, 10: // Invio
                        MenuTipo nuovoMenu = gestisciScelta(selezione, opzioni);
                        if (nuovoMenu != MenuTipo.NESSUNO) return nuovoMenu;
                        break;
                }
            } catch (Exception e) {
                terminal.writer().println("Errore nella lettura dell'input: " + e.getMessage());
                e.printStackTrace(terminal.writer());
                terminal.flush();
            }
        }
    }

    public void avviaMenu() {
        MenuTipo statoCorrente = MenuTipo.PRINCIPALE;

        while (statoCorrente != MenuTipo.ESCI) {
            switch (statoCorrente) {
                case PRINCIPALE:
                    statoCorrente = menuInterattivo(opzioniMenu);
                    break;
                case LOGIN:
                    statoCorrente = menuInterattivo(opzioniLogin);
                    break;
                case REGISTRAZIONE:
                    statoCorrente = menuInterattivo(opzioniReg);
                    break;
                default:
                    statoCorrente = MenuTipo.PRINCIPALE;
            }
        }

        terminal.writer().println("Chiusura del menu...");
    }

    //Legge la posizione della freccina per comunicare al controller la scelta effettuata quando viene premuto Invio
    public MenuTipo gestisciScelta(int selezione, List<String> opzioni) {
        String scelta = opzioni.get(selezione);

        try {
            switch (scelta) {
                case "Accesso come ospite" -> controller.accessoGuest();
                case "Login" -> {
                    return MenuTipo.LOGIN;
                }
                case "Registrati" -> {
                    return MenuTipo.REGISTRAZIONE;
                }
                case "Esci" -> {
                    terminal.writer().println("\n\n\nUscita in corso...");
                    terminal.flush();
                    return MenuTipo.ESCI;
                }
                case "Login utente" -> controller.login(0);
                case "Login ristoratore" -> controller.login(1);
                case "Registrazione utente" -> controller.registrazione(0);
                case "Registrazione ristoratore" -> controller.registrazione(1);
                case "Torna al menu principale" -> {
                    return MenuTipo.PRINCIPALE;
                }
            }
        } catch (Exception e) {
            terminal.writer().println("Errore: " + e.getMessage());
            e.printStackTrace(terminal.writer());
            terminal.flush();
        }

        return MenuTipo.NESSUNO;  // Nessuna azione particolare, resta nel menu corrente
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

    public enum MenuTipo {
        PRINCIPALE, LOGIN, REGISTRAZIONE, ESCI, NESSUNO
    }
}
