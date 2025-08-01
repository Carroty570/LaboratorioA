package com.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.controller.UIController;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class UIMenu {

    private UIController controller;
    private Terminal terminal;
    private LineReader lineReader;

    public UIMenu() throws IOException {
        this.controller = new UIController();
        this.terminal = TerminalBuilder.builder().system(true).build();
        this.lineReader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    public void start() throws IOException {
        int scelta = menuInterattivo("Benvenuto nel sistema login", new String[]{
            "Join come ospite",
            "Login",
            "Registrati",
            "Esci"
        });

        switch (scelta) {
            case 0:
                controller.joinAsGuest();
                break;
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Uscita in corso...");
                return;
        }

        // Mostra di nuovo il menu dopo aver completato l'azione
        start();
    }

    public int menuInterattivo(String titolo, String[] opzioni) {
        int selezionata = 0;

        try {
            Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .jansi(true)
                .build();

            terminal.enterRawMode();
            var reader = terminal.reader();
            boolean esegui = true;

            while (esegui) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                stampaMenu(titolo, opzioni, selezionata);

                int key = reader.read();

                switch (key) {
                    case 27: // sequenza ESC per frecce
                        if (reader.read() == 91) {
                            int freccia = reader.read();
                            if (freccia == 65) { // su
                                selezionata = (selezionata - 1 + opzioni.length) % opzioni.length;
                            } else if (freccia == 66) { // giù
                                selezionata = (selezionata + 1) % opzioni.length;
                            }
                        }
                        break;
                    case 10: // INVIO
                    case 13: // INVIO (Windows)
                        esegui = false;
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return selezionata;
    }

    public void stampaMenu(String titolo, String[] opzioni, int selezionata) {
        int larghezzaContenuto = getMaxLunghezza(opzioni, titolo);
        int padding = 4;
        int larghezzaBox = larghezzaContenuto + padding * 2;

        String bordoTop = "┌" + "─".repeat(larghezzaBox) + "┐";
        String bordoMid = "├" + "─".repeat(larghezzaBox) + "┤";
        String bordoBot = "└" + "─".repeat(larghezzaBox) + "┘";

        System.out.println(bordoTop);

        int spaziSx = (larghezzaBox - titolo.length()) / 2;
        int spaziDx = Math.max(larghezzaBox - titolo.length() - spaziSx, 0);
        System.out.println("│" + " ".repeat(spaziSx) + titolo + " ".repeat(spaziDx) + "│");

        System.out.println(bordoMid);

        for (int i = 0; i < opzioni.length; i++) {
            String prefisso = (i == selezionata) ? "→ " : "  ";
            String colore = (i == selezionata) ? "\033[31m" : "";
            String reset = (i == selezionata) ? "\033[0m" : "";

            String voce = (i + 1) + ". " + opzioni[i];
            String riga = prefisso + voce;
            int spaziFine = Math.max(larghezzaBox - riga.length(), 0);

            System.out.println("│" + colore + riga + " ".repeat(spaziFine) + reset + "│");
        }

        System.out.println(bordoBot);
    }

    public static int getMaxLunghezza(String[] opzioni, String titolo) {
        int max = titolo.length();
        for (int i = 0; i < opzioni.length; i++) {
            int len = ("→ " + (i + 1) + ". " + opzioni[i]).length();
            if (len > max) max = len;
        }
        return max;
    }
    private void stampaBanner() {
        try (InputStream inputStream = UIMenu.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (inputStream != null) {
                String banner = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().collect(Collectors.joining("\n"));
                terminal.writer().println(banner);
                terminal.flush();
            }
        } catch (Exception e) {
            terminal.writer().println("Errore nella lettura del banner:");
            e.printStackTrace();
        }
    }

    private void clearConsole() {
        terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
        terminal.flush();
    }

        private void login() throws IOException {
        String email = lineReader.readLine("Email: ");
        String password = lineReader.readLine("Password: ", '*');
        controller.login(email, password);
    }

    private void register() throws IOException {
        String typeStr = lineReader.readLine("1. Registrati come Cliente\n2. Registrati come Admin\nScelta: ");
        int type;
        try {
            type = Integer.parseInt(typeStr);
        } catch (NumberFormatException e) {
            terminal.writer().println("Tipo non valido.");
            terminal.flush();
            return;
        }

        String name = lineReader.readLine("Nome: ");
        String email = lineReader.readLine("Email: ");
        String password = lineReader.readLine("Password: ", '*');

        if (type == 1) {
            controller.registerClient(name, email, password);
        } else if (type == 2) {
            controller.registerAdmin(name, email, password);
        } else {
            terminal.writer().println("Tipo non valido.");
        }
        terminal.flush();
    }
}
