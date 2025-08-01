package com.view;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.controller.UIController;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class UIMenu {
    private Terminal terminal;
    private LineReader lineReader;
    private UIController controller;
    private final List<String> opzioniMenu = List.of(
    "Join come ospite",
    "Login",
    "Registrati",
    "Esci"
);

    public UIMenu() {
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            terminal.enterRawMode(); // ⬅️ Abilita la lettura raw dei tasti

            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            controller = new UIController(); // logica di utenti
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
        printBanner();
        menuInterattivo();
    }

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

    private void menuInterattivo() {

        int selezione = 0;
        boolean esci = false;

        InputStreamReader input = new InputStreamReader(System.in);

        stampaMenu(opzioniMenu, selezione); // stampa una volta all'inizio

        while (!esci) {
            try {
                int c1 = input.read();
                if (c1 == 27) { // sequenza escape
                    int c2 = input.read();
                    if (c2 == 91) {
                        int c3 = input.read();
                        if (c3 == 65) { // freccia su
                            selezione = aggiornaFreccina(selezione, -1, opzioniMenu.size());
                        } else if (c3 == 66) { // freccia giù
                            selezione = aggiornaFreccina(selezione, +1, opzioniMenu.size());
                        }
                    }
                } else if (c1 == 'w' || c1 == 'W') {
                    selezione = aggiornaFreccina(selezione, -1, opzioniMenu.size());
                } else if (c1 == 's' || c1 == 'S') {
                    selezione = aggiornaFreccina(selezione, +1, opzioniMenu.size());
                } else if (c1 == 10 || c1 == 13) { // invio
                    esci = gestisciScelta(selezione);
                }
            } catch (Exception e) {
                terminal.writer().println("Errore nella lettura dell'input");
                terminal.flush();
            }
        }
    }



    private void stampaMenu(List<String> opzioni, int selezione) {
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

    private static final int offsetRighe = 13;
    private int aggiornaFreccina(int selezioneCorrente, int delta, int max) {
        // Muove la freccina nel range circolare
        int nuovaSelezione = (selezioneCorrente + delta + max) % max;
        // Sposta la freccina visivamente senza riscrivere tutto il menu
        spostaFreccinaVisuale(selezioneCorrente, nuovaSelezione);
        return nuovaSelezione;
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

    private boolean gestisciScelta(int scelta) {
        switch (scelta) {
            case 0 -> accessoGuest();
            case 1 -> login();
            case 2 -> registrazione();
            case 3 -> {
                terminal.writer().println("Uscita in corso...");
                terminal.flush();
                return true;
            }
        }
        return false;
    }

    private void accessoGuest() {

        apriNuovoTerminale();

        Console console = System.console();
        if (console == null) {
            System.out.println("Console non disponibile. Esegui da terminale reale.");
            return;
        }
        
    }

    private void login() {

        apriNuovoTerminale();

        Console console = System.console();

        if (console == null) {
            System.out.println("Console non disponibile. Esegui da terminale reale.");
            return;
        }

        String email = console.readLine("Email: ");
        char[] passwordChars = console.readPassword("Password: ");
        String password = new String(passwordChars);

        boolean success = controller.login(email, password);
        System.out.println(success ? "Login riuscito!" : "Login fallito.");
        
    }

    private void registrazione() {

        apriNuovoTerminale();

        Console console = System.console();

        if (console == null) {
            System.out.println("Console non disponibile. Esegui da terminale reale.");
            return;
        }

        String email = console.readLine("Inserisci la tua mail: ");
        char[] passwordChars = console.readPassword("Scegli una password: ");
        String password = new String(passwordChars);

        boolean success = controller.registra(email, password);
        System.out.println(success ? "Registrazione completata!" : "Email già registrata già esistente.");
        
    }

    private void spostaCursoreFineMenu(List<String> opzioni) {
        int ultimaRiga = offsetRighe + opzioni.size() + 2; // +2 per i bordi
        terminal.writer().print("\033[" + (ultimaRiga + 1) + ";1H"); // Riga sotto il menu, colonna 1
        terminal.flush();
    }


    private void apriNuovoTerminale() {
        try {
            // Costruisci il comando per aprire cmd in nuova finestra e far partire un programma Java
            // Es: java -cp path_al_jar MainLoginClass
            String comando = "cmd /c start cmd /k \"java -cp path\\to\\your\\jar com.tua.classe.MainLogin\"";
            Runtime.getRuntime().exec(comando);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
